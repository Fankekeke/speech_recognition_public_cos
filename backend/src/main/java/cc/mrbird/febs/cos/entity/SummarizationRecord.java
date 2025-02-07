package cc.mrbird.febs.cos.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 发言总结
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SummarizationRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 所属记录
     */
    private Integer voiceId;

    /**
     * 发言人ID
     */
    private Integer speakerId;

    /**
     * 发言人名称
     */
    private String speakerName;

    /**
     * 该发言人对应的总结
     */
    private String summary;

    public SummarizationRecord(Integer voiceId, Integer speakerId, String speakerName, String summary) {
        this.voiceId = voiceId;
        this.speakerId = speakerId;
        this.speakerName = speakerName;
        this.summary = summary;
    }

    public SummarizationRecord() {}
}
