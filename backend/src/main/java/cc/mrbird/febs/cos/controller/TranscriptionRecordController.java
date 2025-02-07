package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.TranscriptionRecord;
import cc.mrbird.febs.cos.service.ITranscriptionRecordService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/transcription-record")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TranscriptionRecordController {

    private final ITranscriptionRecordService transcriptionRecordService;

    /**
     * 分页获取语音转写记录信息
     *
     * @param page                分页对象
     * @param transcriptionRecord 语音转写记录信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<TranscriptionRecord> page, TranscriptionRecord transcriptionRecord) {
        return R.ok();
    }

    /**
     * 查询语音转写记录信息详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(transcriptionRecordService.getById(id));
    }

    /**
     * 查询语音转写记录信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(transcriptionRecordService.list());
    }

    /**
     * 新增语音转写记录信息
     *
     * @param transcriptionRecord 语音转写记录信息
     * @return 结果
     */
    @PostMapping
    public R save(TranscriptionRecord transcriptionRecord) {
        return R.ok(transcriptionRecordService.save(transcriptionRecord));
    }

    /**
     * 修改语音转写记录信息
     *
     * @param transcriptionRecord 语音转写记录信息
     * @return 结果
     */
    @PutMapping
    public R edit(TranscriptionRecord transcriptionRecord) {
        return R.ok(transcriptionRecordService.updateById(transcriptionRecord));
    }

    /**
     * 删除语音转写记录信息
     *
     * @param ids ids
     * @return 语音转写记录信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(transcriptionRecordService.removeByIds(ids));
    }
}
