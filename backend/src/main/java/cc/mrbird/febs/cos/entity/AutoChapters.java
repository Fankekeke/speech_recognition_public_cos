package cc.mrbird.febs.cos.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 章节速览
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AutoChapters implements Serializable {

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
     * 序号
     */
    private Integer indexNo;

    /**
     * 相对于音频起始时间的开始时间，相对时间戳，单位毫秒
     */
    private Integer startTime;

    /**
     * 相对于音频起始时间的结束时间，相对时间戳，单位毫秒
     */
    private Integer endTime;

    /**
     * 章节的一句话标题
     */
    private String headline;

    /**
     * 章节总结
     */
    private String summary;

    public AutoChapters(Integer voiceId, Integer indexNo, Integer startTime, Integer endTime, String headline, String summary) {
        this.voiceId = voiceId;
        this.indexNo = indexNo;
        this.startTime = startTime;
        this.endTime = endTime;
        this.headline = headline;
        this.summary = summary;
    }

    public AutoChapters() {}
}
