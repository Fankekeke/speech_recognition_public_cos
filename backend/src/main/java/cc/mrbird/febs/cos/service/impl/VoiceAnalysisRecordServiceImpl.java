package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.dao.VoiceAnalysisRecordMapper;
import cc.mrbird.febs.cos.entity.VoiceAnalysisRecord;
import cc.mrbird.febs.cos.service.IVoiceAnalysisRecordService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
@Service
public class VoiceAnalysisRecordServiceImpl extends ServiceImpl<VoiceAnalysisRecordMapper, VoiceAnalysisRecord> implements IVoiceAnalysisRecordService {

    /**
     * 分页获取语音解析记录信息
     *
     * @param page                分页对象
     * @param voiceAnalysisRecord 语音解析记录信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> selectAnalysisRecordPage(Page<VoiceAnalysisRecord> page, VoiceAnalysisRecord voiceAnalysisRecord) {
        return baseMapper.selectAnalysisRecordPage(page, voiceAnalysisRecord);
    }
}
