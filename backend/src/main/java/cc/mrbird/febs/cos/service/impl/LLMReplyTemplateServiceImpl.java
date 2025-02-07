package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.dao.LLMReplyTemplateMapper;
import cc.mrbird.febs.cos.entity.LLMReplyTemplate;
import cc.mrbird.febs.cos.service.ILLMReplyTemplateService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LLMReplyTemplateServiceImpl extends ServiceImpl<LLMReplyTemplateMapper, LLMReplyTemplate> implements ILLMReplyTemplateService {
    @Override
    public IPage<LLMReplyTemplate> queryReplyTemplatePage(Page<LLMReplyTemplate> page, LLMReplyTemplate llmReplyTemplate) {
        QueryWrapper<LLMReplyTemplate> queryWrapper = new QueryWrapper<>();
        if(llmReplyTemplate.getTemplateId() != null && !llmReplyTemplate.getTemplateId().equals("")){
            queryWrapper.like("template_id", llmReplyTemplate.getTemplateId());
        }
        if(llmReplyTemplate.getTemplateName() != null && !llmReplyTemplate.getTemplateName().equals("")){
            queryWrapper.like("template_name", llmReplyTemplate.getTemplateName());
        }

        return this.baseMapper.selectPage(page,queryWrapper);
    }

    @Override
    public List<LLMReplyTemplate> queryReplyTemplateList(String keyword) {
        QueryWrapper<LLMReplyTemplate> queryWrapper = new QueryWrapper<>();
        if(keyword != null && !keyword.equals("")){
            queryWrapper.like("template_id", keyword)
                    .or()
                    .like("template_name", keyword);
        }

        return this.baseMapper.selectList(queryWrapper);
    }
}
