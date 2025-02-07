package cc.mrbird.febs.cos.service.impl;

import cc.mrbird.febs.cos.dao.TextPolishMapper;
import cc.mrbird.febs.cos.entity.TextPolish;
import cc.mrbird.febs.cos.service.ITextPolishService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FanK
 */
@Service
public class TextPolishServiceImpl extends ServiceImpl<TextPolishMapper, TextPolish> implements ITextPolishService {

    @Override
    public List<TextPolish> queryTextPolishByVoiceId(Integer queryVoiceId) {
        if(queryVoiceId == null && queryVoiceId.equals("")){
            return new ArrayList<>();
        }
        return this.baseMapper.queryTextPolishByVoiceId(queryVoiceId);
    }
}
