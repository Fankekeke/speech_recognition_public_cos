package cc.mrbird.febs.cos.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 口语书面化
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TextPolish implements Serializable {

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
     * 口语书面化的文本结果
     */
    private String formalParagraphText;

    /**
     * 改写内容对应的SentenceId列表
     */
    private String sentenceIds;

    /**
     * 语音转写结果中的段落id对应
     */
    private String paragraphId;

    /**
     * 文本结果在原音频中的开始时间，相对时间戳，单位为毫秒
     */
    private Integer startTime;

    /**
     * 文本结果在原音频中的结束时间，相对时间戳，单位为毫秒
     */
    private Integer endTime;

    /**
     * 说话人
     */
    private Integer speakerId;

    public TextPolish(Integer voiceId, String formalParagraphText, String sentenceIds, String paragraphId, Integer startTime, Integer endTime, Integer speakerId) {
        this.voiceId = voiceId;
        this.formalParagraphText = formalParagraphText;
        this.sentenceIds = sentenceIds;
        this.paragraphId = paragraphId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.speakerId = speakerId;
    }

    public TextPolish() {}
}
