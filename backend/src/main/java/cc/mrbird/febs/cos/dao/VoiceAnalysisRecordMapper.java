package cc.mrbird.febs.cos.dao;

import cc.mrbird.febs.cos.entity.VoiceAnalysisRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface VoiceAnalysisRecordMapper extends BaseMapper<VoiceAnalysisRecord> {

    /**
     * 分页获取语音解析记录信息
     *
     * @param page                分页对象
     * @param voiceAnalysisRecord 语音解析记录信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> selectAnalysisRecordPage(Page<VoiceAnalysisRecord> page, @Param("voiceAnalysisRecord") VoiceAnalysisRecord voiceAnalysisRecord);
}
