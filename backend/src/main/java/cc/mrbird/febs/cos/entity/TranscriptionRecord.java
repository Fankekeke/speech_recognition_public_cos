package cc.mrbird.febs.cos.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 语音转写记录
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TranscriptionRecord implements Serializable {

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
     * 段落级别ID
     */
    private String paragraphId;

    /**
     * 发言人id
     */
    private String speakerId;

    /**
     * 序号
     */
    private Integer indexNo;

    /**
     * 句子id，同属于一个SentenceId的word信息可以组装成一句话
     */
    private Integer sentenceId;

    /**
     * 相对于音频起始时间的开始时间，相对时间戳，单位毫秒
     */
    private Integer startTime;

    /**
     * 相对于音频起始时间的结束时间，相对时间戳，单位毫秒
     */
    private Integer endTime;

    /**
     * 文本信息
     */
    private String content;

    public TranscriptionRecord(Integer voiceId, String paragraphId, String speakerId, Integer indexNo, Integer sentenceId, Integer startTime, Integer endTime, String content) {
        this.voiceId = voiceId;
        this.paragraphId = paragraphId;
        this.speakerId = speakerId;
        this.indexNo = indexNo;
        this.sentenceId = sentenceId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.content = content;
    }

    public TranscriptionRecord() {}
}
