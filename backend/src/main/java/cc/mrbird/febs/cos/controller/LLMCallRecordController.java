package cc.mrbird.febs.cos.controller;

import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.service.ILLMCallRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cos/LLMCallRecord")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LLMCallRecordController {

    private final ILLMCallRecordService illmCallRecordService;


    /**
     * 查询口语书面化信息详情
     *
     * @param voiceCode 声音code
     * @return 结果
     */
    @GetMapping("/getLLMRecord/{voiceCode}")
    public R getLLMRecordByVoiceCode(@PathVariable("voiceCode") String voiceCode) {
        return R.ok(illmCallRecordService.getLLMRecordByVoiceCode(voiceCode));
    }

    /**
     * 调用LLM 插入到记录表 稍后才可查询结果
     * @param voiceId
     * @return
     */
    @GetMapping("/callLLM/{voiceId}")
    public R callLLM(@PathVariable("voiceId")Integer voiceId, @RequestParam String templateId){
        return R.ok(illmCallRecordService.callLLM(voiceId, templateId));
    }

    /**
     * 继续调用LLM生成
     * @param voiceId
     * @param sessionId
     * @param content
     * @return
     */
    @GetMapping("/continueCallLLM/{voiceId}")
    public R continueCallLLM(@PathVariable("voiceId") Integer voiceId, @RequestParam String sessionId, String content){
        return R.ok(illmCallRecordService.continueCallLLM(voiceId, sessionId, content));
    }


}
