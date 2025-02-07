package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.analysis.LLMAnalysisProcess;
import cc.mrbird.febs.cos.analysis.TaskCallBack;
import cc.mrbird.febs.cos.dao.LLMCallRecordMapper;
import cc.mrbird.febs.cos.entity.LLMCallRecord;
import cc.mrbird.febs.cos.entity.LLMReplyTemplate;
import cc.mrbird.febs.cos.entity.VoiceAnalysisRecord;
import cc.mrbird.febs.cos.service.ILLMCallRecordService;
import cc.mrbird.febs.cos.service.ILLMReplyTemplateService;
import cc.mrbird.febs.cos.service.IVoiceAnalysisRecordService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LLMCallRecordServiceImpl extends ServiceImpl<LLMCallRecordMapper, LLMCallRecord> implements ILLMCallRecordService {

    private final IVoiceAnalysisRecordService iVoiceAnalysisRecordService;
    private final LLMAnalysisProcess llmAnalysisProcess;
    private final ILLMReplyTemplateService illmReplyTemplateService;


    @Override
    public List<LLMCallRecord> getLLMRecordByVoiceCode(String voiceCode) {
        List<LLMCallRecord> llmCallRecords = this.baseMapper.selectList(
                new QueryWrapper<LLMCallRecord>()
                        .eq("voice_code", voiceCode)
                        .eq("response_code",200)
        );

        return llmCallRecords;
    }

    @Override
    public List<LLMCallRecord> callLLM(Integer voiceId, String templateId) {
        List<LLMCallRecord> result = Collections.emptyList();

        LLMReplyTemplate llmReplyTemplate = illmReplyTemplateService.getOne(new QueryWrapper<LLMReplyTemplate>().eq("template_id", templateId));
        LLMCallRecordMapper llmCallRecordMapper = this.baseMapper;
        VoiceAnalysisRecord voiceAnalysisRecord = iVoiceAnalysisRecordService.getById(voiceId);
        String content = voiceAnalysisRecord.getParagraphSummary();
        Boolean processFlag = llmAnalysisProcess.startLLMScheduling(content, llmReplyTemplate.getTemplateContent(), new TaskCallBack() {
            @Override
            public void onComplete(Object msg) {
                LLMCallRecord llmCallRecord = (LLMCallRecord) msg;
                llmCallRecord.setVoiceCode(voiceAnalysisRecord.getCode());
                llmCallRecordMapper.insert(llmCallRecord);
            }

            @Override
            public void onFail(String error) {

            }
        });
        if(processFlag){
            result = this.baseMapper.selectList(new QueryWrapper<LLMCallRecord>()
                            .eq("voice_code", voiceAnalysisRecord.getCode())
            );
        }

        return result;
    }


    @Override
    public List<LLMCallRecord> continueCallLLM(Integer voiceId, String sessionId, String content) {
        List<LLMCallRecord> result = Collections.emptyList();

        LLMCallRecordMapper llmCallRecordMapper = this.baseMapper;
        VoiceAnalysisRecord voiceAnalysisRecord = iVoiceAnalysisRecordService.getById(voiceId);
        Boolean processFlag = llmAnalysisProcess.sessionLLMScheduling(content, sessionId, new TaskCallBack() {
            @Override
            public void onComplete(Object msg) {
                LLMCallRecord llmCallRecord = (LLMCallRecord) msg;
                llmCallRecord.setVoiceCode(voiceAnalysisRecord.getCode());
                llmCallRecordMapper.insert(llmCallRecord);
            }

            @Override
            public void onFail(String error) {

            }
        });
        if(processFlag){
            result = this.baseMapper.selectList(new QueryWrapper<LLMCallRecord>()
                    .eq("voice_code", voiceAnalysisRecord.getCode())
            );
        }

        return result;
    }
}
