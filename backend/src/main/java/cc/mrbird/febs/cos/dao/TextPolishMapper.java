package cc.mrbird.febs.cos.dao;

import cc.mrbird.febs.cos.entity.TextPolish;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author FanK
 */
public interface TextPolishMapper extends BaseMapper<TextPolish> {

    List<TextPolish> queryTextPolishByVoiceId(@Param("voiceId")Integer voiceId);

}
