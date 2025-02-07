package cc.mrbird.febs.cos.analysis;

import cc.mrbird.febs.common.config.MinioConfig;
import cc.mrbird.febs.common.utils.DirUtil;
import cc.mrbird.febs.cos.entity.TextPolish;
import cc.mrbird.febs.cos.entity.VoiceAnalysisRecord;
import cc.mrbird.febs.cos.service.ITextPolishService;
import cc.mrbird.febs.cos.service.IVoiceAnalysisRecordService;
import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.xml.soap.Text;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 声音拆分 过滤
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/test")
public class VoiceSplitFilterTask {

    private final ITextPolishService textPolishService;
    private final IVoiceAnalysisRecordService voiceAnalysisRecordService;

    //用来存储删除失败的临时文件
    private static List<String> waitDelFiles = new ArrayList<>();

    @Resource
    private MinioConfig minioConfig;

//
//    @GetMapping("/voiceSplitTest/{voiceId}")
//    public R voiceSplitTest(@PathVariable Integer voiceId) {
//        return R.ok(getVoiceSplitFile(voiceId));
//    }

    /**
     * 根据音频id 切分音频
     *
     * @param voiceId 音频id
     * @return
     */
    public Map<Integer, List<Map<String, Object>>> getVoiceSplitFile(Integer voiceId) {
        log.info("Method::getVoiceSplitFile");
        //获取文件名称和对话段
        VoiceAnalysisRecord voiceAnalysisRecord = voiceAnalysisRecordService.getById(voiceId);
        List<TextPolish> textPolishList = textPolishService.list(new QueryWrapper<TextPolish>().eq("voice_id",voiceId));
        String fileName = voiceAnalysisRecord.getFileName();

        //获取项目临时存储路径
        String savePathRoot = DirUtil.getAudioTmpPath();
        log.info("Method::getVoiceSplitFile::savePathRoot={}",  savePathRoot);

        String tempSavePath = savePathRoot + "/" + fileName.trim();
        waitDelFiles.add(tempSavePath);

        File file = new File(tempSavePath);
        if(!file.exists()){
            file.mkdirs();
        }
        log.info("Method::getVoiceSplitFile::tempSavePath is exists", file.exists());

        String saveLocalPath = "";
        //将minio中的文件存储到本地
        try {
            saveLocalPath = voiceAnalysisRecord.getFileUrl();
//            saveLocalPath = "/xz/complaint/db/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("Method::getVoiceSplitFile::saveLocalPath={}", saveLocalPath);
        //音频分割
        return voiceSplit(saveLocalPath, textPolishList, file.getAbsolutePath());
    }

    /**
     * 清除临时文件
     *
     * @return
     */
    public void destory() {
        waitDelFiles.forEach(item -> {
            Boolean delFlag = FileUtil.del(item);
            if (!delFlag) {
                System.out.println(String.format("文件：{%s}删除失败", item));
            }
        });
    }


    private static Map<Integer, List<Map<String, Object>>> voiceSplit(String filePath, List<TextPolish> textPolishList, String saveSplitFilePath) {
        log.info("Method::voiceSplit");

        //拆分后的说话人和音频路径
        Map<Integer, List<Map<String, Object>>> splitFilesMap = new HashMap<>();
        log.info("Method::voiceSplit::splitFilesMap Length: {}",splitFilesMap.size());
        System.out.println(splitFilesMap);
        try {
            String savePath = saveSplitFilePath + "/speaker%d_seg%d.mp3";
            System.out.println("切割音频-------：" + savePath);
            log.debug("切割音频-------：" + savePath);
            //将文本段根据说话人拆分
            Map<Integer, List<TextPolish>> groupedBySpeaker = textPolishList.stream()
                    .collect(Collectors.groupingBy(TextPolish::getSpeakerId));
            log.info("Method::voiceSplit::groupedBySpeaker length: {}", groupedBySpeaker);
            groupedBySpeaker.forEach((speakerId, textPolishes) -> {

                List<Map<String, Object>> splitFiles = new ArrayList<>();
                for (int i = 0; i < textPolishes.size(); i++) {
                    Map<String, Object> fileMap = new HashMap<>();
                    TextPolish textPolish = textPolishes.get(i);
                    //过滤掉小于2s的音频 大于20s的
                    if(textPolish.getEndTime() - textPolish.getStartTime() < 3000){
                        continue;
                    }

                    String saveFilePath = String.format(savePath, speakerId, i);
                    // 初始化FFmpeg和FFprobe
                    FFmpeg ffmpeg = null;
                    try {
                        ffmpeg = new FFmpeg("/xz/complaint/ffmpeg/bin/ffmpeg");
                        log.info("Method::voiceSplit::FFmpeg init");
//                        ffmpeg = new FFmpeg();
                        log.debug("ffmpeg初始化成功~~~~~~~");
                    } catch (IOException e) {
                        log.error("Method::voiceSplit::FFmpeg init error");
                        throw new RuntimeException(e);
                    }
                    log.debug("filePath------------" + filePath);
                    log.debug("saveFilePath------------" + saveFilePath);
                    FFmpegBuilder builder = new FFmpegBuilder()
                            .setInput(filePath).addOutput(saveFilePath)
                            .setStartOffset(textPolish.getStartTime(), TimeUnit.MILLISECONDS)
                            .setDuration(textPolish.getEndTime() - textPolish.getStartTime(), TimeUnit.MILLISECONDS)
                            .setAudioSampleRate(16000) // 设置音频采样率为 16000 Hz
                            .setAudioChannels(1)       //设置音频为单声道
                            .setAudioBitRate(16_000)   //设置音频比特率 16k
                            .done();

                    FFmpegExecutor executor = null;
                    try {
                        executor = new FFmpegExecutor(ffmpeg);
                        executor.createJob(builder).run();
                    } catch (IOException e) {
                        log.error("Method::voiceSplit::FFmpegExecutor error");
                        throw new RuntimeException(e);
                    }
                    fileMap.put("duration", textPolish.getEndTime() - textPolish.getStartTime());
                    fileMap.put("path", saveFilePath);
                    splitFiles.add(fileMap);
                }

                splitFiles.sort(Comparator.comparing(map -> (Integer) map.get("duration")));

                splitFilesMap.put(speakerId, splitFiles);
            });


        } finally {

            return splitFilesMap;
        }
    }


}
