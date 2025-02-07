package cc.mrbird.febs.cos.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 思维导图
 *
 * @author FanK
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MindMapSummary implements Serializable {

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
     * 导图编号
     */
    private String code;

    /**
     * 标题
     */
    private String title;

    /**
     * 父级编号【最顶层为0】
     */
    private String parentCode;

    /**
     * 子集
     */
    @TableField(exist = false)
    List<MindMapSummary> subList;
}
