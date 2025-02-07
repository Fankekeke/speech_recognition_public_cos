package cc.mrbird.febs.cos.controller;


import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.QuestionsAnswerSummary;
import cc.mrbird.febs.cos.service.IQuestionsAnswerSummaryService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/cos/questions-answer-summary")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class QuestionsAnswerSummaryController {

    private final IQuestionsAnswerSummaryService questionsAnswerSummaryService;

    /**
     * 分页获取问答回顾摘要结果列表信息
     *
     * @param page                   分页对象
     * @param questionsAnswerSummary 问答回顾摘要结果列表信息
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<QuestionsAnswerSummary> page, QuestionsAnswerSummary questionsAnswerSummary) {
        return R.ok();
    }

    /**
     * 查询问答回顾摘要结果列表信息详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(questionsAnswerSummaryService.getById(id));
    }

    /**
     * 查询问答回顾摘要结果列表信息列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(questionsAnswerSummaryService.list());
    }

    /**
     * 新增问答回顾摘要结果列表信息
     *
     * @param questionsAnswerSummary 问答回顾摘要结果列表信息
     * @return 结果
     */
    @PostMapping
    public R save(QuestionsAnswerSummary questionsAnswerSummary) {
        return R.ok(questionsAnswerSummaryService.save(questionsAnswerSummary));
    }

    /**
     * 修改问答回顾摘要结果列表信息
     *
     * @param questionsAnswerSummary 问答回顾摘要结果列表信息
     * @return 结果
     */
    @PutMapping
    public R edit(QuestionsAnswerSummary questionsAnswerSummary) {
        return R.ok(questionsAnswerSummaryService.updateById(questionsAnswerSummary));
    }

    /**
     * 删除问答回顾摘要结果列表信息
     *
     * @param ids ids
     * @return 问答回顾摘要结果列表信息
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(questionsAnswerSummaryService.removeByIds(ids));
    }
}
