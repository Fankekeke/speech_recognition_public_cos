package cc.mrbird.febs.cos.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 语音解析记录
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class VoiceAnalysisRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 语音编号
     */
    private String code;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 总时常（秒）
     */
    private Integer totalTime;

    /**
     * 文件地址
     */
    private String fileUrl;

    /**
     * 任务状态（0.处理中 1.已完成 2.处理错误）
     */
    private String status;

    /**
     * AI处理任务状态（0.处理中 1.已完成 2.处理错误）
     */
    private String dealStatus;

    /**
     * 上传时间
     */
    private String createDate;

    /**
     * 解析完成时间
     */
    private String resolveDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 关键词
     */
    private String keyWord;

    /**
     * 删除标识（0.正常 1.删除）
     */
    private String delFlag;

    /**
     * 音频大小
     */
    private Integer voiceSize;

    /**
     * 音频时长，单位：毫秒
     */
    private Integer voiceDuration;

    /**
     * 音频采样率
     */
    private Integer sampleRate;

    /**
     * 音频语种
     */
    private String language;

    /**
     * 摘要
     */
    private String paragraphSummary;

    /**
     * 摘要标题
     */
    private String paragraphTitle;

    /**
     * 处理状态（0.未处理 1.已处理 2.暂不处理）
     */
    private String processStatus;

    /**
     * 投诉人电话号码
     */
    private String phone;

    /**
     * 投诉人声纹库编号
     */
    private String complaintPeopleFeature;

    /**
     * 接线员声纹库编号
     */
    private String operatorPeopleFeature;

    /**
     * 投诉人对应发言人ID
     */
    private Integer complaintSpeakId;

    /**
     * 接线员对应发言人ID
     */
    private Integer operatorSpeakId;

    /**
     * 投诉人情绪
     */
    private String complaintMood;

    /**
     * 接线员情绪
     */
    private String operatorMood;

    /**
     * 接线时间
     */
    private String connectionTime;

    /**
     * 年份
     */
    @TableField(exist = false)
    private int year;
}
