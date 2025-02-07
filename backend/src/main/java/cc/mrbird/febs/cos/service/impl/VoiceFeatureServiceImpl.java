package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.common.utils.AliOssUtils;
import cc.mrbird.febs.cos.analysis.IgrRecogntion;
import cc.mrbird.febs.cos.analysis.TaskCallBack;
import cc.mrbird.febs.cos.analysis.VoiceprintRecognition;
import cc.mrbird.febs.cos.dao.VoiceFeatureMapper;
import cc.mrbird.febs.cos.entity.VoiceFeature;
import cc.mrbird.febs.cos.service.IVoiceFeatureService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.security.auth.callback.Callback;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * @author FanK
 */
@Service
@RequiredArgsConstructor
public class VoiceFeatureServiceImpl extends ServiceImpl<VoiceFeatureMapper, VoiceFeature> implements IVoiceFeatureService {

    private final IgrRecogntion igrRecogntion;

    @Resource
    private AliOssUtils aliOSSUtils;

    /**
     * 分页获取声纹库特征信息
     *
     * @param page         分页对象
     * @param voiceFeature 声纹库特征信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> selectVoiceFeaturePage(Page<VoiceFeature> page, VoiceFeature voiceFeature) {
        return baseMapper.selectVoiceFeaturePage(page, voiceFeature);
    }

    /**
     * 添加声纹特征
     * @param fileInputStream  文件输入流
     * @param type
     * @return
     */
    @Override
    @Transactional(rollbackFor=Exception.class)
    public VoiceFeature addFeature(MultipartFile file, String type) {
        VoiceFeature voiceFeature = null;
        try {
            String fileUrl = aliOSSUtils.upload(file.getInputStream(), file.getOriginalFilename());

            // 添加声纹库特征
            voiceFeature = addVoiceFeature(type, fileUrl);

            // 声纹注册
            VoiceprintRecognition.addVoiceFeatureManual(voiceFeature.getId().toString(), (voiceFeature.getCode() + ("0".equals(type) ? "投诉人" : "接线员")), file.getInputStream());

            VoiceFeature finalVoiceFeature = voiceFeature;
            igrRecogntion.processByFileInputStream((FileInputStream) file.getInputStream(), new TaskCallBack() {
                @Override
                public void onComplete(Object msg) {
                    System.out.println("msg: " + msg);
                    // 更新声纹库特征
                    editVoiceFeature(msg.toString(), finalVoiceFeature);
                }

                @Override
                public void onFail(String error) {

                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            return voiceFeature;
        }
    }

    @Override
    public VoiceFeature addFeature(File file, String type) {
        VoiceFeature voiceFeature = null;
        try {
            String fileUrl = aliOSSUtils.upload(new FileInputStream(file), file.getName());
            // 添加声纹库特征
            voiceFeature = addVoiceFeature(type, fileUrl);
            // 声纹注册
            VoiceprintRecognition.addVoiceFeatureManual(voiceFeature.getId().toString(), (voiceFeature.getCode() + ("0".equals(type) ? "投诉人" : "接线员")), new FileInputStream(file));
            // 分析性别年龄
            VoiceFeature finalVoiceFeature = voiceFeature;
            igrRecogntion.processByFileInputStream(new FileInputStream(file), new TaskCallBack() {
                @Override
                public void onComplete(Object msg) {
                    System.out.println("msg: " + msg);
                    // 更新声纹库特征
                    editVoiceFeature(msg.toString(), finalVoiceFeature);
                }

                @Override
                public void onFail(String error) {

                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            return voiceFeature;
        }
    }

    /**
     * 添加声纹库特征
     *
     * @param type 类型（0.投诉人 1.接线员）
     * @return 结果
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public VoiceFeature addVoiceFeature(String type, String fileUrl) {
        VoiceFeature voiceFeature = new VoiceFeature();
        voiceFeature.setCode("VF-" + System.currentTimeMillis());
        voiceFeature.setType(type);
        voiceFeature.setCreateDate(DateUtil.formatDateTime(new Date()));
        voiceFeature.setFeatureVoice(fileUrl);
        save(voiceFeature);
        return voiceFeature;
    }

    /**
     * 更新声纹库特征
     *
     * @param msg          特征消息
     * @param voiceFeature 声纹特征记录
     * @return 结果
     */
    public boolean editVoiceFeature(String msg, VoiceFeature voiceFeature) {
        if (StrUtil.isEmpty(msg)) {
            return false;
        }

        JSONObject featureObject = JSONUtil.parseObj(msg);
        if (!"success".equals(featureObject.get("message", String.class ))) {
            return false;
        }

        // 年龄
        String ageRate = featureObject.getByPath("data.result.age.age_type", String.class);

        // 性别
        String sex = featureObject.getByPath("data.result.gender.gender_type", String.class);

        voiceFeature.setSex(sex);
        voiceFeature.setAgeRate(ageRate);
        return updateById(voiceFeature);
    }


}
