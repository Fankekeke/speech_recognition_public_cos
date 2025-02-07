package cc.mrbird.febs.cos.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 要点提炼（待办事项、关键词、重点内容）
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MeetingAssistance implements Serializable {

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
     * 关键词提取结果
     */
    private String keyWords;

    /**
     * 关键句提取结果，也称为重点内容
     */
    private String keySentences;

    /**
     * 待办内容、待办摘要的集合
     */
    private String actions;

    /**
     * 面试场景置信度得分
     */
    private BigDecimal interviewPoint;

    /**
     * 演讲场景置信度得分
     */
    private BigDecimal lecturePoint;

    /**
     * 会议场景置信度得分
     */
    private BigDecimal meeting;

    public MeetingAssistance(Integer voiceId, String keyWords, String keySentences, String actions, BigDecimal interviewPoint, BigDecimal lecturePoint, BigDecimal meeting) {
        this.voiceId = voiceId;
        this.keyWords = keyWords;
        this.keySentences = keySentences;
        this.actions = actions;
        this.interviewPoint = interviewPoint;
        this.lecturePoint = lecturePoint;
        this.meeting = meeting;
    }

    public MeetingAssistance() {}
}
