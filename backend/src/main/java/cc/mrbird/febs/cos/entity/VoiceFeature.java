package cc.mrbird.febs.cos.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 声纹库特征
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class VoiceFeature implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 声纹库唯一编号
     */
    private String code;

    /**
     * 类型（0.投诉人 1.接线员 2.其他）
     */
    private String type;

    /**
     * 年龄
     */
    private String ageRate;

    /**
     * 性别（0.未知 1.男 2.女）
     */
    private String sex;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 音频片段
     */
    private String featureVoice;
}
