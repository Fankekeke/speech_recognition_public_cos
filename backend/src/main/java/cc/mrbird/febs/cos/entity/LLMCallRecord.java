package cc.mrbird.febs.cos.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *  大语言模型调用记录
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "llm_call_record")
public class LLMCallRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 语音编号
     */
    private String voiceCode;

    /**
     * 输入内容
     */
    private String inputText;

    /**
     * 输出内容
     */
    private String outputText;

    /**
     * 对话ID
     */
    private String sessionId;

    /**
     * 应用的模型ID
     */
    private String modelId;

    /**
     * 输入消耗TOKENS
     */
    private Integer inputTokens;

    /**
     * 输出消耗TOKENS
     */
    private Integer outputTokens;

    /**
     * 请求时间
     */
    private LocalDateTime requestTime;

    /**
     * 响应时间
     */
    private LocalDateTime responseTime;

    /**
     * 响应码
     */
    private Integer responseCode;

    /**
     * 请求失败异常信息
     */
    private String responseException;
}
