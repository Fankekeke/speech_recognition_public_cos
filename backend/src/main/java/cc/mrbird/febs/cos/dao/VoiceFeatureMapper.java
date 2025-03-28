package cc.mrbird.febs.cos.dao;

import cc.mrbird.febs.cos.entity.VoiceFeature;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface VoiceFeatureMapper extends BaseMapper<VoiceFeature> {

    /**
     * 分页获取声纹库特征信息
     *
     * @param page         分页对象
     * @param voiceFeature 声纹库特征信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> selectVoiceFeaturePage(Page<VoiceFeature> page, @Param("voiceFeature") VoiceFeature voiceFeature);
}
