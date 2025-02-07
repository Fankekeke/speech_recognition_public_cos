package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.SummarizationRecord;
import cc.mrbird.febs.cos.service.ISummarizationRecordService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/summarization-record")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SummarizationRecordController {

    private final ISummarizationRecordService summarizationRecordService;

    /**
     * 分页获取发言总结信息
     *
     * @param page                分页对象
     * @param summarizationRecord 发言总结信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<SummarizationRecord> page, SummarizationRecord summarizationRecord) {
        return R.ok();
    }

    /**
     * 查询发言总结信息详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(summarizationRecordService.getById(id));
    }

    /**
     * 查询发言总结信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(summarizationRecordService.list());
    }

    /**
     * 新增发言总结信息
     *
     * @param summarizationRecord 发言总结信息
     * @return 结果
     */
    @PostMapping
    public R save(SummarizationRecord summarizationRecord) {
        return R.ok(summarizationRecordService.save(summarizationRecord));
    }

    /**
     * 修改发言总结信息
     *
     * @param summarizationRecord 发言总结信息
     * @return 结果
     */
    @PutMapping
    public R edit(SummarizationRecord summarizationRecord) {
        return R.ok(summarizationRecordService.updateById(summarizationRecord));
    }

    /**
     * 删除发言总结信息
     *
     * @param ids ids
     * @return 发言总结信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(summarizationRecordService.removeByIds(ids));
    }
}
