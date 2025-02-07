package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.analysis.VoiceprintRecognition;
import cc.mrbird.febs.cos.entity.VoiceFeature;
import cc.mrbird.febs.cos.service.IVoiceFeatureService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/voice-feature")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VoiceFeatureController {

    private final IVoiceFeatureService voiceFeatureService;

    private final VoiceprintRecognition voiceprintRecognition;

    /**
     * 分页获取声纹库特征信息
     *
     * @param page         分页对象
     * @param voiceFeature 声纹库特征信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<VoiceFeature> page, VoiceFeature voiceFeature) {
        return R.ok(voiceFeatureService.selectVoiceFeaturePage(page, voiceFeature));
    }

    /**
     * 新增声纹库特征信息
     *
     * @param voiceFeature 声纹库特征信息
     * @return 结果
     */
    @PostMapping
    public R save(VoiceFeature voiceFeature) {
        return R.ok(voiceFeatureService.save(voiceFeature));
    }

    /**
     * 1：n验证
     *
     * @param file 录音文件
     * @return 结果
     * @throws IOException
     */
    @PostMapping("/verification")
    public R voiceVerification(@RequestParam("file") MultipartFile file) throws IOException {
        return R.ok(VoiceprintRecognition.queryVoiceFeatureManual(file.getInputStream()));
    }

    /**
     * 声纹特征添加
     *
     * @param file 录音文件
     * @return 结果
     * @throws IOException
     */
    @PostMapping("/addFeature")
    public R addFeature(@RequestParam("file") MultipartFile file,  @RequestParam("type") String type) {
        try {
            Map<String,List> queryVoiceFeature = VoiceprintRecognition.queryVoiceFeatureManual(file.getInputStream());
            List scoreList = queryVoiceFeature.get("scoreList");
            if(scoreList.size() > 0){
                return R.error("特征已经存在");
            }
            VoiceFeature voiceFeature = voiceFeatureService.addFeature(file, type);
            if(voiceFeature != null){
                return R.ok("特征创建成功");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.error("特征创建失败,请尝试上传其他音频");
    }


    /**
     * 修改声纹库特征信息
     *
     * @param voiceFeature 声纹库特征信息
     * @return 结果
     */
    @PutMapping
    public R edit(VoiceFeature voiceFeature) {
        return R.ok(voiceFeatureService.updateById(voiceFeature));
    }

    /**
     * 删除声纹库特征信息
     *
     * @param ids ids
     * @return 声纹库特征信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        // 删除特征
        voiceprintRecognition.batchDelCVoiceFeature(ids);
        return R.ok(voiceFeatureService.removeByIds(ids));
    }
}
