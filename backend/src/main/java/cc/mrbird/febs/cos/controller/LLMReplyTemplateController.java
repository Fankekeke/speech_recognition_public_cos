package cc.mrbird.febs.cos.controller;

import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.entity.LLMReplyTemplate;
import cc.mrbird.febs.cos.service.ILLMReplyTemplateService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/cos/LLMReplyTemplate")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LLMReplyTemplateController {

    private final ILLMReplyTemplateService illmReplyTemplateService;


    /**
     * 分页获取模板
     * @param page
     * @param replyTemplate
     * @return
     */
    @GetMapping("/page")
    public R page(Page<LLMReplyTemplate> page, LLMReplyTemplate replyTemplate) {
        return R.ok(illmReplyTemplateService.queryReplyTemplatePage(page, replyTemplate));
    }

    /**
     * 模板ID或模板Id筛选列表
     * @param keyword
     * @return
     */
    @GetMapping("/list")
    public R keywordSearch(@RequestParam("keyword") String keyword){
        return R.ok(illmReplyTemplateService.queryReplyTemplateList(keyword));
    }

    /**
     * 新增回复模板
     *
     * @param replyTemplate 模板信息
     * @return 结果
     */
    @PostMapping
    public R save(LLMReplyTemplate replyTemplate) {
        String templateId = "template_" + System.currentTimeMillis();
        replyTemplate.setTemplateId(templateId);

        LocalDateTime time = LocalDateTime.now();
        replyTemplate.setCreateTime(time);
        replyTemplate.setUpdateTime(time);
        return R.ok(illmReplyTemplateService.save(replyTemplate));
    }

    /**
     * 批量删除模板
     *
     * @param ids ids
     * @return
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(illmReplyTemplateService.removeByIds(ids));
    }

}
