package cc.mrbird.febs.cos.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 问答回顾摘要结果列表
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class QuestionsAnswerSummary implements Serializable {

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
     * 问题
     */
    private String question;

    /**
     * 提炼出该问题对应的原语音转写的SentenceId列表
     */
    private String sentenceIdsQuestion;

    /**
     * 问题对应的答案
     */
    private String answer;

    /**
     * 总结出该答案对应的原语音转写的SentenceId列表
     */
    private String sentenceIdsAnswer;

    public QuestionsAnswerSummary(Integer voiceId, String question, String sentenceIdsQuestion, String answer, String sentenceIdsAnswer) {
        this.voiceId = voiceId;
        this.question = question;
        this.sentenceIdsQuestion = sentenceIdsQuestion;
        this.answer = answer;
        this.sentenceIdsAnswer = sentenceIdsAnswer;
    }

    public QuestionsAnswerSummary() {}
}
