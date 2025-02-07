package cc.mrbird.febs.cos.service;

import cc.mrbird.febs.cos.entity.TextPolish;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author FanK
 */
public interface ITextPolishService extends IService<TextPolish> {

    List<TextPolish> queryTextPolishByVoiceId(Integer queryVoiceId);

}
