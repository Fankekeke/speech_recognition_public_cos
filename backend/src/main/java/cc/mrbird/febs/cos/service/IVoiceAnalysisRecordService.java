package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.VoiceAnalysisRecord;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface IVoiceAnalysisRecordService extends IService<VoiceAnalysisRecord> {

    /**
     * 分页获取语音解析记录信息
     *
     * @param page                分页对象
     * @param voiceAnalysisRecord 语音解析记录信息
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> selectAnalysisRecordPage(Page<VoiceAnalysisRecord> page, VoiceAnalysisRecord voiceAnalysisRecord);
}
