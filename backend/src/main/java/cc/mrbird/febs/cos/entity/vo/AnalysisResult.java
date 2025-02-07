package cc.mrbird.febs.cos.entity.vo;

import lombok.Data;

/**
 * @author FanK
 */
@Data
public class AnalysisResult {

    private String taskId;

    private String askKey;

    private String taskStatus;

    private String outputMp3Path;

    private String message;

    private String requestId;


}
