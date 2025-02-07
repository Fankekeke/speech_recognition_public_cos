package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.LLMReplyTemplate;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ILLMReplyTemplateService extends IService<LLMReplyTemplate> {


    /**
     * 分页获取回复模板信息
     *
     * @param page         分页对象
     * @param llmReplyTemplate 模板查询信息
     * @return 结果
     */
    IPage<LLMReplyTemplate> queryReplyTemplatePage(Page<LLMReplyTemplate> page, LLMReplyTemplate llmReplyTemplate);

    /**
     * 通过templateId和templateName 关键词 查询回复模板列表
     * @param keyword 关键词
     * @return
     */
    List<LLMReplyTemplate> queryReplyTemplateList(String keyword);

}
