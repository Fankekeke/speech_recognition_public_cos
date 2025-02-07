package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.MindMapSummary;
import cc.mrbird.febs.cos.service.IMindMapSummaryService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/mind-map-summary")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MindMapSummaryController {

    private final IMindMapSummaryService mindMapSummaryService;

    /**
     * 分页获取思维导图信息
     *
     * @param page           分页对象
     * @param mindMapSummary 思维导图信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<MindMapSummary> page, MindMapSummary mindMapSummary) {
        return R.ok();
    }

    /**
     * 查询思维导图信息详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(mindMapSummaryService.getById(id));
    }

    /**
     * 查询思维导图信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(mindMapSummaryService.list());
    }

    /**
     * 新增思维导图信息
     *
     * @param mindMapSummary 思维导图信息
     * @return 结果
     */
    @PostMapping
    public R save(MindMapSummary mindMapSummary) {
        return R.ok(mindMapSummaryService.save(mindMapSummary));
    }

    /**
     * 修改思维导图信息
     *
     * @param mindMapSummary 思维导图信息
     * @return 结果
     */
    @PutMapping
    public R edit(MindMapSummary mindMapSummary) {
        return R.ok(mindMapSummaryService.updateById(mindMapSummary));
    }

    /**
     * 删除思维导图信息
     *
     * @param ids ids
     * @return 思维导图信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(mindMapSummaryService.removeByIds(ids));
    }
}
