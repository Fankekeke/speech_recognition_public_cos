package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.VoiceFeature;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface IVoiceFeatureService extends IService<VoiceFeature> {

    /**
     * 分页获取声纹库特征信息
     *
     * @param page         分页对象
     * @param voiceFeature 声纹库特征信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> selectVoiceFeaturePage(Page<VoiceFeature> page, VoiceFeature voiceFeature);

    /**
     * 添加声纹特征 MultipartFile版本
     * @param file  MultipartFile类型
     * @param type
     * @return
     */
    VoiceFeature addFeature(MultipartFile file,  String type);


    /**
     * 添加声纹特征 File版本
     * @param file File类型
     * @param type
     * @return
     */
    VoiceFeature addFeature(File file, String type);
}
