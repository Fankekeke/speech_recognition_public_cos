package cc.mrbird.febs.cos.controller;

import cc.mrbird.febs.common.config.MinioConfig;
import cc.mrbird.febs.common.utils.AliOssUtils;
import cc.mrbird.febs.common.utils.FileUtil;
import cc.mrbird.febs.cos.analysis.SubmitFiletransTask;
import cc.mrbird.febs.cos.entity.VoiceAnalysisRecord;
import cc.mrbird.febs.cos.service.IVoiceAnalysisRecordService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrSplitter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * @author FanK
 */
@RestController
@CrossOrigin
@RequestMapping("/cos/minio")
public class MinioController {

    @Resource
    private MinioConfig minioConfig;

    @Resource
    private IVoiceAnalysisRecordService voAnalysisRecordService;

    @Resource
    private SubmitFiletransTask submitFiletransTask;

    @Resource
    private AliOssUtils aliOSSUtils;

    /**
     * 上传
     *
     * @param multipartFile 文件
     * @return 结果
     * @throws Exception 异常
     */
    @PostMapping("/voice/upload/db")
    public Object uploadVoice(@RequestParam("file") MultipartFile multipartFile) throws Exception {

        // 定义要上传文件 的存放路径
        String localPath = "/xz/complaint/db";
        // 获得文件名字
        String fileName = multipartFile.getOriginalFilename();
        // 上传失败提示
        String newFileName = FileUtil.upload(multipartFile, localPath, fileName);

        // 保存解析记录
        VoiceAnalysisRecord record = new VoiceAnalysisRecord();
        record.setCode("VAR-" + System.currentTimeMillis());
        record.setFileName(fileName);

        record.setFileUrl("https://cp.xzhisoft.com/api/web/" + newFileName);

        record.setStatus("0");
        record.setDealStatus("0");
        record.setCreateDate(DateUtil.formatDateTime(new Date()));
        record.setDelFlag("0");

        // 文件名称获取投诉电话与接线时间   20240209120526-18600236766-1-0001Admin.Mp3
        List<String> split = StrSplitter.split(record.getFileName(), '-', 0, true, true);
        if (split.size() > 2) {
            record.setConnectionTime(DateUtil.parse(split.get(0)).toString());
            record.setPhone(split.get(1));
        }
        // 创建读写任务
        String taskId = submitFiletransTask.summitTask(record.getFileUrl());
        record.setTaskId(taskId);
        voAnalysisRecordService.save(record);

        // 异步获取任务状态
        submitFiletransTask.getTaskInfo(taskId);
        return record.getFileUrl();
    }

    /**
     * 音频解析上传OSS
     *
     * @param multipartFile 文件
     * @return 结果
     * @throws Exception 异常
     */
    @PostMapping("/voice/upload/oss")
    public Object uploadVoiceOss(@RequestParam("file") MultipartFile multipartFile) throws Exception {
        // 获得文件名字
        String fileName = multipartFile.getOriginalFilename();

        String filePath = aliOSSUtils.upload(multipartFile);
        // 保存解析记录
        VoiceAnalysisRecord record = new VoiceAnalysisRecord();
        record.setCode("VAR-" + System.currentTimeMillis());
        record.setFileName(fileName);

        record.setFileUrl(filePath);

        record.setStatus("0");
        record.setDealStatus("0");
        record.setCreateDate(DateUtil.formatDateTime(new Date()));
        record.setDelFlag("0");

        // 文件名称获取投诉电话与接线时间   20240209120526-18600236766-1-0001Admin.Mp3
        List<String> split = StrSplitter.split(record.getFileName(), '-', 0, true, true);
        if (split.size() > 2) {
            record.setConnectionTime(DateUtil.parse(split.get(0)).toString());
            record.setPhone(split.get(1));
        }
        // 创建读写任务
        String taskId = submitFiletransTask.summitTask(record.getFileUrl());
        record.setTaskId(taskId);
        voAnalysisRecordService.save(record);

        // 异步获取任务状态
        submitFiletransTask.getTaskInfo(taskId);
        return record.getFileUrl();
    }

    /**
     * 上传
     *
     * @param multipartFile 文件
     * @return 结果
     * @throws Exception 异常
     */
    @PostMapping("/voice/upload")
    public Object upload(@RequestParam("file") MultipartFile multipartFile) throws Exception {
        String voiceUrl = this.minioConfig.putObject(multipartFile);
        // 保存解析记录
        VoiceAnalysisRecord record = new VoiceAnalysisRecord();
        record.setCode("VAR-" + System.currentTimeMillis());
        record.setFileName(multipartFile.getOriginalFilename());
        record.setFileUrl(voiceUrl);
        record.setStatus("0");
        record.setDealStatus("0");
        record.setCreateDate(DateUtil.formatDateTime(new Date()));
        record.setDelFlag("0");

        // 文件名称获取投诉电话与接线时间   20240209120526-18600236766-1-0001Admin.Mp3
        List<String> split = StrSplitter.split(record.getFileName(), '-', 0, true, true);
        if (split.size() > 2) {
            record.setConnectionTime(DateUtil.parse(split.get(0)).toString());
            record.setPhone(split.get(1));
        }
        // 创建读写任务
        String taskId = submitFiletransTask.summitTask(voiceUrl);
//        String taskId = submitFiletransTask.summitTask("https://xzhi-app.oss-cn-beijing.aliyuncs.com/20240209120526-18600236766-1-0001Admin.Mp3");
        record.setTaskId(taskId);
        voAnalysisRecordService.save(record);

        // 异步获取任务状态
        submitFiletransTask.getTaskInfo(taskId);
        return voiceUrl;
    }

    /**
     * 下载文件
     *
     * @param fileName 文件名称
     * @param response 返回
     */
    @GetMapping("/download")
    public void download(@RequestParam("fileName") String fileName, HttpServletResponse response) {
        this.minioConfig.download(fileName, response);
    }

    /**
     * 列出所有存储桶名称
     *
     * @return 结果
     * @throws Exception 异常
     */
    @PostMapping("/list")
    public List<String> list() throws Exception {
        return this.minioConfig.listBucketNames();
    }

    /**
     * 创建存储桶
     *
     * @param bucketName 桶名称
     * @return 结果
     * @throws Exception 异常
     */
    @PostMapping("/createBucket")
    public boolean createBucket(String bucketName) throws Exception {
        return this.minioConfig.makeBucket(bucketName);
    }

    /**
     * 删除存储桶
     *
     * @param bucketName 桶名称
     * @return 结果
     * @throws Exception 异常
     */
    @PostMapping("/deleteBucket")
    public boolean deleteBucket(String bucketName) throws Exception {
        return this.minioConfig.removeBucket(bucketName);
    }

    /**
     * 列出存储桶中的所有对象名称
     *
     * @param bucketName 桶名称
     * @return 结果
     * @throws Exception 异常
     */
    @PostMapping("/listObjectNames")
    public List<String> listObjectNames(String bucketName) throws Exception {
        return this.minioConfig.listObjectNames(bucketName);
    }

    /**
     * 删除一个对象
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     * @return 结果
     * @throws Exception 异常
     */
    @PostMapping("/removeObject")
    public boolean removeObject(String bucketName, String objectName) throws Exception {
        return this.minioConfig.removeObject(bucketName, objectName);
    }

    /**
     * 文件访问路径
     *
     * @param bucketName 桶名称
     * @param objectName 对象名称
     * @return 结果
     * @throws Exception 异常
     */
    @PostMapping("/getObjectUrl")
    public String getObjectUrl(String bucketName, String objectName) throws Exception {
        return this.minioConfig.getObjectUrl(bucketName, objectName);
    }
}
