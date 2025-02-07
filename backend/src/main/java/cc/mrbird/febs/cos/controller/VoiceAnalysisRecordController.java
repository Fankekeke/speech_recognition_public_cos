package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.analysis.DataAnalysisProcess;
import cc.mrbird.febs.cos.entity.VoiceAnalysisRecord;
import cc.mrbird.febs.cos.service.IVoiceAnalysisRecordService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/voice-analysis-record")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VoiceAnalysisRecordController {

    private final IVoiceAnalysisRecordService voiceAnalysisRecordService;

    private final DataAnalysisProcess dataAnalysisProcess;

    @GetMapping("/test")
    public void test() {
        // dataAnalysisProcess.meetingAssistanceHandel("https://prod-tingwu-paas-common-beijing.oss-cn-beijing.aliyuncs.com/tingwu/output/1596181557257961/82d86db8347744cca10ad711be7c69a1/82d86db8347744cca10ad711be7c69a1_MeetingAssistance_20240725095355.json?Expires=1724464516&OSSAccessKeyId=LTAI5tMzZ1D4o1drkJN1TfCr&Signature=voDtm5OIVJS8eoFXeLoO76amkOY%3D", 1);
        // dataAnalysisProcess.summaryHandel("https://prod-tingwu-paas-common-beijing.oss-cn-beijing.aliyuncs.com/tingwu/output/1596181557257961/82d86db8347744cca10ad711be7c69a1/82d86db8347744cca10ad711be7c69a1_Summarization_20240725095410.json?Expires=1724464516&OSSAccessKeyId=LTAI5tMzZ1D4o1drkJN1TfCr&Signature=1q4gT2eqaQXCipNK5nwKnz97VB8%3D", 1);
        // dataAnalysisProcess.autoChaptersHandel("https://prod-tingwu-paas-common-beijing.oss-cn-beijing.aliyuncs.com/tingwu/output/1596181557257961/82d86db8347744cca10ad711be7c69a1/82d86db8347744cca10ad711be7c69a1_AutoChapters_20240725095403.json?Expires=1724464516&OSSAccessKeyId=LTAI5tMzZ1D4o1drkJN1TfCr&Signature=iuwoDm2PEdpVyVxCrH%2BU8P1BdjQ%3D", 1);
        // dataAnalysisProcess.transcriptionHandel("https://prod-tingwu-paas-common-beijing.oss-cn-beijing.aliyuncs.com/tingwu/output/1596181557257961/82d86db8347744cca10ad711be7c69a1/82d86db8347744cca10ad711be7c69a1_Transcription_20240725095350.json?Expires=1724464516&OSSAccessKeyId=LTAI5tMzZ1D4o1drkJN1TfCr&Signature=XVLQqMWgEqd2NB4U%2BU6o2IJoDBA%3D", 1);
        // dataAnalysisProcess.textPolishHandel("https://prod-tingwu-paas-common-beijing.oss-cn-beijing.aliyuncs.com/tingwu/output/1596181557257961/82d86db8347744cca10ad711be7c69a1/82d86db8347744cca10ad711be7c69a1_TextPolish_20240725095400.json?Expires=1724464516&OSSAccessKeyId=LTAI5tMzZ1D4o1drkJN1TfCr&Signature=7jqIIv%2ByccDUzFGL%2FJLhld187ik%3D", 1);
    }

    /**
     * 分页获取语音解析记录信息
     *
     * @param page                分页对象
     * @param voiceAnalysisRecord 语音解析记录信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<VoiceAnalysisRecord> page, VoiceAnalysisRecord voiceAnalysisRecord) {
        return R.ok(voiceAnalysisRecordService.selectAnalysisRecordPage(page, voiceAnalysisRecord));
    }

    /**
     * 查询语音解析记录信息详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(dataAnalysisProcess.selectVoiceDetail(id));
    }

    /**
     * 查询语音解析记录信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(voiceAnalysisRecordService.list());
    }

    /**
     * 新增语音解析记录信息
     *
     * @param voiceAnalysisRecord 语音解析记录信息
     * @return 结果
     */
    @PostMapping
    public R save(VoiceAnalysisRecord voiceAnalysisRecord) {
        return R.ok(voiceAnalysisRecordService.save(voiceAnalysisRecord));
    }

    /**
     * 修改语音解析记录信息
     *
     * @param voiceAnalysisRecord 语音解析记录信息
     * @return 结果
     */
    @PutMapping
    public R edit(VoiceAnalysisRecord voiceAnalysisRecord) {
        return R.ok(voiceAnalysisRecordService.updateById(voiceAnalysisRecord));
    }

    /**
     * 删除语音解析记录信息
     *
     * @param ids ids
     * @return 语音解析记录信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(voiceAnalysisRecordService.removeByIds(ids));
    }
}
