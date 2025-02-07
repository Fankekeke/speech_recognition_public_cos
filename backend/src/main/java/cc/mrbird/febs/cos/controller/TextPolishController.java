package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.TextPolish;
import cc.mrbird.febs.cos.service.ITextPolishService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/text-polish")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TextPolishController {

    private final ITextPolishService textPolishService;

    /**
     * 分页获取口语书面化信息
     *
     * @param page       分页对象
     * @param textPolish 口语书面化信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<TextPolish> page, TextPolish textPolish) {
        return R.ok();
    }

    /**
     * 查询口语书面化信息详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(textPolishService.getById(id));
    }

    /**
     * 查询口语书面化信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(textPolishService.list());
    }

    /**
     * 新增口语书面化信息
     *
     * @param textPolish 口语书面化信息
     * @return 结果
     */
    @PostMapping
    public R save(TextPolish textPolish) {
        return R.ok(textPolishService.save(textPolish));
    }

    /**
     * 修改口语书面化信息
     *
     * @param textPolish 口语书面化信息
     * @return 结果
     */
    @PutMapping
    public R edit(TextPolish textPolish) {
        return R.ok(textPolishService.updateById(textPolish));
    }

    /**
     * 删除口语书面化信息
     *
     * @param ids ids
     * @return 口语书面化信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(textPolishService.removeByIds(ids));
    }
}
