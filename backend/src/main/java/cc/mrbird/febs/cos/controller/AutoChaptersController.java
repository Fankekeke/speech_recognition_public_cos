package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.AutoChapters;
import cc.mrbird.febs.cos.service.IAutoChaptersService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/auto-chapters")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AutoChaptersController {

    private final IAutoChaptersService autoChaptersService;

    /**
     * 分页获取章节速览信息
     *
     * @param page         分页对象
     * @param autoChapters 章节速览信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<AutoChapters> page, AutoChapters autoChapters) {
        return R.ok();
    }

    /**
     * 查询章节速览信息详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(autoChaptersService.getById(id));
    }

    /**
     * 查询章节速览信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(autoChaptersService.list());
    }

    /**
     * 新增章节速览信息
     *
     * @param autoChapters 章节速览信息
     * @return 结果
     */
    @PostMapping
    public R save(AutoChapters autoChapters) {
        return R.ok(autoChaptersService.save(autoChapters));
    }

    /**
     * 修改章节速览信息
     *
     * @param autoChapters 章节速览信息
     * @return 结果
     */
    @PutMapping
    public R edit(AutoChapters autoChapters) {
        return R.ok(autoChaptersService.updateById(autoChapters));
    }

    /**
     * 删除章节速览信息
     *
     * @param ids ids
     * @return 章节速览信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(autoChaptersService.removeByIds(ids));
    }
}
