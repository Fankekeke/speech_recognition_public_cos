package cc.mrbird.febs.cos.entity.vo;

import lombok.Data;

/**
 * 解析数据
 * @author FanK
 */
@Data
public class AnalysisData {

    /**
     * PPT提取
     */
    private String pptExtraction;

    /**
     * 要点提炼
     */
    private String meetingAssistance;

    /**
     * 章节速览
     */
    private String autoChapters;

    /**
     * 语音转写
     */
    private String transcription;

    /**
     * 口语书面化
     */
    private String textPolish;

    /**
     * 音视频转换
     */
    private String transcoding;

    /**
     * 摘要
     */
    private String summarization;
}
