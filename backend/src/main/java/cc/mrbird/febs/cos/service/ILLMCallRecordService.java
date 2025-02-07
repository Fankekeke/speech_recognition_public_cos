package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.LLMCallRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ILLMCallRecordService extends IService<LLMCallRecord> {


    List<LLMCallRecord> getLLMRecordByVoiceCode(String voiceCode);

    /**
     * 调用LLM
     * @param voiceId
     * @return
     */
    List<LLMCallRecord> callLLM(Integer voiceId, String templateId);


    /**
     * 根据LLM对话session继续生成
     */
    List<LLMCallRecord> continueCallLLM(Integer voiceId, String sessionId, String content);

}
