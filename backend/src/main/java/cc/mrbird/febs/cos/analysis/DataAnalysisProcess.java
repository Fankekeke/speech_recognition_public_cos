package cc.mrbird.febs.cos.analysis;

import cc.mrbird.febs.cos.entity.*;
import cc.mrbird.febs.cos.entity.vo.MindResult;
import cc.mrbird.febs.cos.service.*;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 语音解析数据处理
 *
 * @author FanK
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DataAnalysisProcess {

    private final LLMAnalysisProcess llmAnalysisProcess;

    private final IAutoChaptersService autoChaptersService;

    private final IMeetingAssistanceService meetingAssistanceService;

    private final IMindMapSummaryService mindMapSummaryService;

    private final IQuestionsAnswerSummaryService questionsAnswerSummaryService;

    private final ISummarizationRecordService summarizationRecordService;

    private final ITextPolishService textPolishService;

    private final ITranscriptionRecordService transcriptionRecordService;

    private final IVoiceAnalysisRecordService voiceAnalysisRecordService;

    private final ILLMCallRecordService illmCallRecordService;

    private final IVoiceFeatureService voiceFeatureService;

    private final VoiceSplitFilterTask voiceSplitFilterTask;

    private final IgrRecogntion igrRecogntion;

    private final VoiceMoodAnalysisTask voiceMoodAnalysisTask;

    /**
     * 章节速览数据解析
     *
     * @param url     解析地址
     * @param voiceId 所属记录
     */
    public void meetingAssistanceHandel(String url, Integer voiceId) {
        if (StrUtil.isEmpty(url)) {
            return;
        }

        // 获取返回数据
        String resultData = HttpUtil.get(url);
        if (StrUtil.isEmpty(resultData)) {
            return;
        }

        // 数据解析
        JSONObject resultJson = JSONUtil.parseObj(resultData);
        // 关键词
        List<String> keywords = resultJson.getByPath("MeetingAssistance.Keywords", List.class);
        if (CollectionUtil.isNotEmpty(keywords)) {
            // 获取语音解析信息
            VoiceAnalysisRecord analysisRecord = voiceAnalysisRecordService.getById(voiceId);
            analysisRecord.setKeyWord(StrUtil.join(",", keywords));
            voiceAnalysisRecordService.updateById(analysisRecord);
        }

        // 演讲场景置信度得分
        BigDecimal lecture = resultJson.getByPath("MeetingAssistance.Classifications.Lecture", BigDecimal.class);
        // 会议场景置信度得分
        BigDecimal meeting = resultJson.getByPath("MeetingAssistance.Classifications.Meeting", BigDecimal.class);
        // 面试场景置信度得分
        BigDecimal interview = resultJson.getByPath("MeetingAssistance.Classifications.Interview", BigDecimal.class);

        // 关键句
        String keySentences = resultJson.getByPath("MeetingAssistance.KeySentences", String.class);

        // 待办内容、待办摘要的集合
        String actions = resultJson.getByPath("MeetingAssistance.Actions", String.class);

        // 章节速览准备入库
        MeetingAssistance meetingAssistance = new MeetingAssistance(voiceId, StrUtil.join(",", keywords), keySentences, actions, interview, lecture, meeting);
        meetingAssistanceService.save(meetingAssistance);

        System.out.println("解析完成-------------------------");
    }

    /**
     * 摘要总结（全文摘要、发言总结、问答回顾、思维导图数据解析
     *
     * @param url     解析地址
     * @param voiceId 所属记录
     */
    public void summaryHandel(String url, Integer voiceId) {
        if (StrUtil.isEmpty(url)) {
            return;
        }

        // 获取语音解析信息
        VoiceAnalysisRecord analysisRecord = voiceAnalysisRecordService.getById(voiceId);

        // 获取返回数据
        String resultData = HttpUtil.get(url);
        if (StrUtil.isEmpty(resultData) || null == analysisRecord) {
            return;
        }

        // 数据解析
        JSONObject resultJson = JSONUtil.parseObj(resultData);

        // 摘要
        String paragraphSummary = resultJson.getByPath("Summarization.ParagraphSummary", String.class);
        String paragraphTitle = resultJson.getByPath("Summarization.ParagraphTitle", String.class);
        if (StrUtil.isNotEmpty(paragraphSummary) || StrUtil.isNotEmpty(paragraphTitle)) {
            analysisRecord.setParagraphSummary(paragraphSummary);
            analysisRecord.setParagraphTitle(paragraphTitle);
            // 更新语音摘要
            voiceAnalysisRecordService.updateById(analysisRecord);
        }

        // 会话摘要
        JSONArray conversationalSummary = resultJson.getByPath("Summarization.ConversationalSummary", JSONArray.class);
        List<SummarizationRecord> summarizationRecordList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(conversationalSummary)) {
            for (Object item : conversationalSummary) {
                JSONObject summarizationRecord = JSONUtil.parseObj(item);
                // 发言人ID
                Integer speakerId = summarizationRecord.get("SpeakerId", Integer.class);
                // 发言人名称
                String speakerName = summarizationRecord.get("SpeakerName", String.class);
                // 内容
                String summary = summarizationRecord.get("Summary", String.class);
                summarizationRecordList.add(new SummarizationRecord(
                        voiceId,
                        speakerId,
                        speakerName,
                        summary
                ));
            }
            summarizationRecordService.saveBatch(summarizationRecordList);
        }

        // 问答回顾摘要结果
        JSONArray questionsAnsweringSummary = resultJson.getByPath("Summarization.QuestionsAnsweringSummary", JSONArray.class);
        if (CollectionUtil.isNotEmpty(questionsAnsweringSummary)) {
            List<QuestionsAnswerSummary> questionsAnswerSummaryList = new ArrayList<>();
            questionsAnsweringSummary.forEach(item -> {
                JSONObject questionJson = JSONUtil.parseObj(item);
                String question = questionJson.get("Question", String.class);
                String answer = questionJson.get("Answer", String.class);
                List<String> sentenceIdsQuestion = questionJson.get("SentenceIdsOfQuestion", List.class);
                List<String> sentenceIdsAnswer = questionJson.get("SentenceIdsOfAnswer", List.class);
                questionsAnswerSummaryList.add(new QuestionsAnswerSummary(voiceId,
                        question,
                        StrUtil.join(",", sentenceIdsQuestion),
                        answer,
                        StrUtil.join(",", sentenceIdsAnswer)));
            });
            questionsAnswerSummaryService.saveBatch(questionsAnswerSummaryList);
        }

        // 思维导图
        JSONArray mindMapSummary = resultJson.getByPath("Summarization.MindMapSummary", JSONArray.class);
        if (CollectionUtil.isNotEmpty(mindMapSummary)) {
            List<MindMapSummary> mapSummaryList = new ArrayList<>();

            mindMapSummary.forEach(item -> {
                JSONObject jsonObject = JSONUtil.parseObj(item);
                this.mindMapSummaryCheck(voiceId, new MindMapSummary(), jsonObject, "0", mapSummaryList);
            });
            mindMapSummaryService.saveBatch(mapSummaryList);
        }



    }

    /**
     * 思维导图递归保存数据
     *
     * @param voiceId        所属记录
     * @param mindMapSummary 待添加导图数据
     * @param jsonObject     导图数据
     * @param parentCode     父级编号
     */
    public void mindMapSummaryCheck(Integer voiceId, MindMapSummary mindMapSummary, JSONObject jsonObject, String parentCode, List<MindMapSummary> parentList) {
        mindMapSummary.setVoiceId(voiceId);
        // 设置父级编号
        mindMapSummary.setParentCode(parentCode);
        // 设置自身编号
        mindMapSummary.setCode("MMS-" + UUID.fastUUID());
        // 标题
        mindMapSummary.setTitle(jsonObject.get("Title", String.class));
        // 判断是否有子集
        JSONArray subJsonArray = jsonObject.get("Topic", JSONArray.class);
        if (CollectionUtil.isNotEmpty(subJsonArray)) {
            subJsonArray.forEach(item -> {
                JSONObject subJson = JSONUtil.parseObj(item);
                this.mindMapSummaryCheck(voiceId, new MindMapSummary(), subJson, mindMapSummary.getCode(), parentList);
            });
        }
        parentList.add(mindMapSummary);
    }

    /**
     * 章节速览数据解析
     *
     * @param url     解析地址
     * @param voiceId 所属记录
     */
    public void autoChaptersHandel(String url, Integer voiceId) {
        if (StrUtil.isEmpty(url)) {
            return;
        }
        // 获取返回数据
        String resultData = HttpUtil.get(url);
        if (StrUtil.isEmpty(resultData)) {
            return;
        }

        // 数据解析
        JSONObject resultJson = JSONUtil.parseObj(resultData);
        JSONArray autoChapters = resultJson.getByPath("AutoChapters", JSONArray.class);
        if (CollectionUtil.isNotEmpty(autoChapters)) {
            List<AutoChapters> autoChaptersList = new ArrayList<>();
            autoChapters.forEach(item -> {
                JSONObject jsonObject = JSONUtil.parseObj(item);
                autoChaptersList.add(new AutoChapters(voiceId,
                        jsonObject.get("Id", Integer.class),
                        jsonObject.get("Start", Integer.class),
                        jsonObject.get("End", Integer.class),
                        jsonObject.get("Headline", String.class),
                        jsonObject.get("Summary", String.class)
                ));
            });
            autoChaptersService.saveBatch(autoChaptersList);
        }
    }

    /**
     * 语音转写数据解析
     *
     * @param url     解析地址
     * @param voiceId 所属记录
     */
    public Map<String, String> transcriptionHandel(String url, Integer voiceId) {
        // 获取语音解析信息
        VoiceAnalysisRecord analysisRecord = voiceAnalysisRecordService.getById(voiceId);

        if (StrUtil.isEmpty(url)) {
            return Collections.emptyMap();
        }

        // 获取返回数据
        String resultData = HttpUtil.get(url);
        if (StrUtil.isEmpty(resultData) || null == analysisRecord) {
            return Collections.emptyMap();
        }

        // 数据解析
        JSONObject resultJson = JSONUtil.parseObj(resultData);

        // 音频信息
        Integer size = resultJson.getByPath("Transcription.AudioInfo.Size", Integer.class);
        Integer voiceDuration = resultJson.getByPath("Transcription.AudioInfo.Duration", Integer.class);
        Integer sampleRate = resultJson.getByPath("Transcription.AudioInfo.SampleRate", Integer.class);
        String language = resultJson.getByPath("Transcription.AudioInfo.Language", String.class);

        analysisRecord.setVoiceSize(size);
        analysisRecord.setVoiceDuration(voiceDuration);
        analysisRecord.setSampleRate(sampleRate);
        analysisRecord.setLanguage(language);
        // 更新语音摘要
        voiceAnalysisRecordService.updateById(analysisRecord);

        // 语音转写记录
        List<TranscriptionRecord> transcriptionRecord = new ArrayList<>();
        JSONArray paragraphs = resultJson.getByPath("Transcription.Paragraphs", JSONArray.class);
        if (CollectionUtil.isNotEmpty(paragraphs)) {
            for (Object paragraphItem : paragraphs) {
                JSONObject paragraphItemObject = JSONUtil.parseObj(paragraphItem);
                // 段落级别id
                String paragraphId = paragraphItemObject.get("ParagraphId", String.class);
                //发言人id
                String speakerId = JSONUtil.parseObj(paragraphItem).get("SpeakerId", String.class);
                JSONArray words = paragraphItemObject.get("Words", JSONArray.class);
                if (CollectionUtil.isEmpty(words)) {
                    continue;
                }
                // 设置句子
                for (Object wordItem : words) {
                    JSONObject wordItemObject = JSONUtil.parseObj(wordItem);
                    // 序号
                    Integer indexNo = wordItemObject.get("Id", Integer.class);
                    // 句子id
                    Integer sentenceId = wordItemObject.get("SentenceId", Integer.class);
                    // 句子开始时间
                    Integer startTime = wordItemObject.get("Start", Integer.class);
                    // 句子结束时间
                    Integer endTime = wordItemObject.get("End", Integer.class);
                    // 文本内容
                    String content = wordItemObject.get("Text", String.class);
                    transcriptionRecord.add(new TranscriptionRecord(
                            voiceId,
                            paragraphId,
                            speakerId,
                            indexNo,
                            sentenceId,
                            startTime,
                            endTime,
                            content
                    ));
                }
            }
            transcriptionRecordService.saveBatch(transcriptionRecord);
            Map<String, List<TranscriptionRecord>> transcriptionRecordMap = transcriptionRecord.stream().collect(Collectors.groupingBy(TranscriptionRecord::getParagraphId));
            // 返回数据
            Map<String, String> result = new LinkedHashMap<>();
            transcriptionRecordMap.forEach((key, value) -> {
                result.put(key, value.get(0).getSpeakerId());
            });
            return result;
        }
        return Collections.emptyMap();
    }

    /**
     * 口语书面化数据解析
     *
     * @param url     解析地址
     * @param voiceId 所属记录
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void textPolishHandel(String url, Integer voiceId, Map<String, String> transcriptionRecordMap) {
        if (StrUtil.isEmpty(url)) {
            return;
        }
        // 获取返回数据
        String resultData = HttpUtil.get(url);
        if (StrUtil.isEmpty(resultData)) {
            return;
        }

        // 数据解析
        JSONObject resultJson = JSONUtil.parseObj(resultData);

        boolean isValid = CollectionUtil.isNotEmpty(transcriptionRecordMap);
        JSONArray textPolish = resultJson.get("TextPolish", JSONArray.class);
        if (CollectionUtil.isNotEmpty(textPolish)) {
            List<TextPolish> textPolishList = new ArrayList<>();
            for (Object polishItem : textPolish) {
                JSONObject textPolishItemObject = JSONUtil.parseObj(polishItem);
                // 口语书面化的文本结果
                String formalParagraphText = textPolishItemObject.get("FormalParagraphText", String.class);
                // 语音转写结果中的段落id对应
                String paragraphId = textPolishItemObject.get("ParagraphId", String.class);
                // 文本结果在原音频中的开始时间，相对时间戳，单位为毫秒
                Integer startTime = textPolishItemObject.get("Start", Integer.class);
                // 文本结果在原音频中的结束时间，相对时间戳，单位为毫秒
                Integer endTime = textPolishItemObject.get("End", Integer.class);
                List<String> sentenceIds = textPolishItemObject.get("SentenceIds", List.class);

                // 设置发言人ID
                String speakerId = null;
                if (StrUtil.isNotEmpty(paragraphId) && isValid) {
                    speakerId = transcriptionRecordMap.get(paragraphId);
                }

                textPolishList.add(new TextPolish(
                        voiceId,
                        formalParagraphText,
                        CollectionUtil.isEmpty(sentenceIds) ? null : StrUtil.join(",", sentenceIds),
                        paragraphId,
                        startTime,
                        endTime,
                        StrUtil.isEmpty(speakerId) ? null : Integer.valueOf(speakerId)
                ));
            }

            textPolishService.saveBatch(textPolishList);
        }


        VoiceAnalysisRecord voiceAnalysisRecord = voiceAnalysisRecordService.getById(voiceId);
        //音频分割
        Map<Integer, List<Map<String, Object>>> speakerMap = voiceSplitFilterTask.getVoiceSplitFile(voiceId);
        Set<Integer> speakerSet = speakerMap.keySet();
        speakerMap.forEach((speaker, voiceList) ->{
            String featureType = "2";
            List<com.alibaba.fastjson.JSONObject> moodList = new ArrayList<>();
            for (Map<String, Object> voiceMap : voiceList) {
                String voicePath = voiceMap.get("path").toString();
                try {
                    com.alibaba.fastjson.JSONObject moodResult = voiceMoodAnalysisTask.getVoiceMood(voicePath);
                    moodList.add(moodResult);
                } catch (Exception e) {
                    System.out.println("情绪 ------》" + e);
                }

                //一个说话人只执行一次1：N查找
                if(featureType.equals("2")){
                    Map<String,List> voiceFeatureResult = VoiceprintRecognition.queryVoiceFeature(voicePath);
                    System.out.println(voiceFeatureResult);
                    List scoreList = voiceFeatureResult.get("scoreList");
                    if(scoreList.size() > 0){
                        //识别到人物
                        com.alibaba.fastjson.JSONObject scoreItem = (com.alibaba.fastjson.JSONObject) scoreList.get(0);
                        int featureId = scoreItem.getIntValue("featureId");
                        VoiceFeature voiceFeature = voiceFeatureService.getById(featureId);
                        //0投诉人 1接线员
                        if(voiceFeature.getType().equals("0")){
                            featureType = "0";
                            voiceAnalysisRecord.setComplaintPeopleFeature(String.valueOf(featureId));
                        }else if(voiceFeature.getType().equals("1")){
                            featureType = "1";
                            voiceAnalysisRecord.setOperatorPeopleFeature(String.valueOf(featureId));
                            voiceAnalysisRecord.setOperatorSpeakId(speaker);
                            // 设置投诉人
                            List<Integer> complaintPeopleList = speakerSet.stream().filter(item -> !item.equals(speaker)).collect(Collectors.toList());
                            voiceAnalysisRecord.setComplaintSpeakId(CollectionUtil.isEmpty(complaintPeopleList) ? null : complaintPeopleList.get(0));
                        }
                    }else{
                        //未识别到人物

                        //自动添加的默认为投诉人
                        featureType = "0";
                        // 添加声纹库特征
                        try {
                            VoiceFeature voiceFeature = voiceFeatureService.addFeature(new File(voiceMap.get("path").toString()), featureType);
                            //重用输入流
                            VoiceprintRecognition.addVoiceFeatureManual(voiceFeature.getId().toString(), (voiceFeature.getCode() + ("0".equals(featureType) ? "投诉人" : "接线员")), new FileInputStream(voiceMap.get("path").toString()));
                            voiceAnalysisRecord.setComplaintPeopleFeature(String.valueOf(voiceFeature.getId()));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

            // 情绪分析
            List<String> emotions = voiceMoodAnalysisTask.analyzeEmotions(moodList);
            String emotionDes = String.join(",", emotions);
            if(featureType.equals("0")){
                voiceAnalysisRecord.setComplaintMood(emotionDes);
            }else if(featureType.equals("1")){
                voiceAnalysisRecord.setOperatorMood(emotionDes);
            }

        });
        voiceAnalysisRecordService.updateById(voiceAnalysisRecord);

        voiceSplitFilterTask.destory();

    }

    /**
     * 获取语音解析详情
     *
     * @param voiceId 解析记录ID
     * @return 结果
     */
    public LinkedHashMap<String, Object> selectVoiceDetail(Integer voiceId) {
        // 返回数据
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>() {
            {
                put("voiceAnalysis", null);
                put("transcriptionRecord", Collections.emptyList());
                put("textPolish", Collections.emptyList());
                put("summarizationRecord", Collections.emptyList());
                put("questionsAnswerSummary", Collections.emptyList());
                put("mindMapSummary", Collections.emptyList());
                put("meetingAssistance", Collections.emptyList());
                put("autoChapters", Collections.emptyList());
                put("recordList", Collections.emptyList());
                put("yearRecordRate", Collections.emptyList());
                put("callRecord", Collections.emptyList());
                put("voiceFeature", null);
            }
        };
        // 语音解析记录
        VoiceAnalysisRecord voiceAnalysis = voiceAnalysisRecordService.getById(voiceId);
        // 语音转写记录
        List<TranscriptionRecord> transcriptionRecord = transcriptionRecordService.list(Wrappers.<TranscriptionRecord>lambdaQuery().eq(TranscriptionRecord::getVoiceId, voiceId));
        // 口语书面化
        List<TextPolish> textPolish = textPolishService.list(Wrappers.<TextPolish>lambdaQuery().eq(TextPolish::getVoiceId, voiceId));
        // 发言总结
        List<SummarizationRecord> summarizationRecord = summarizationRecordService.list(Wrappers.<SummarizationRecord>lambdaQuery().eq(SummarizationRecord::getVoiceId, voiceId));
        // 问答回顾
        List<QuestionsAnswerSummary> questionsAnswerSummary = questionsAnswerSummaryService.list(Wrappers.<QuestionsAnswerSummary>lambdaQuery().eq(QuestionsAnswerSummary::getVoiceId, voiceId));
        // 思维导图
        List<MindMapSummary> mindMapSummaryList = mindMapSummaryService.list(Wrappers.<MindMapSummary>lambdaQuery().eq(MindMapSummary::getVoiceId, voiceId));
        if (CollectionUtil.isNotEmpty(mindMapSummaryList)) {
            MindResult mindMapSummary = new MindResult("0", "对话信息");
            this.setMindData(mindMapSummary, mindMapSummaryList);
            result.put("mindMapSummary", mindMapSummary);
        } else {
            result.put("mindMapSummary", null);
        }

        // 要点提炼
        List<MeetingAssistance> meetingAssistance = meetingAssistanceService.list(Wrappers.<MeetingAssistance>lambdaQuery().eq(MeetingAssistance::getVoiceId, voiceId));
        // 章节速览
        List<AutoChapters> autoChapters = autoChaptersService.list(Wrappers.<AutoChapters>lambdaQuery().eq(AutoChapters::getVoiceId, voiceId));

        result.put("voiceAnalysis", voiceAnalysis);
        result.put("transcriptionRecord", transcriptionRecord);
        result.put("textPolish", textPolish);
        result.put("summarizationRecord", summarizationRecord);
        result.put("questionsAnswerSummary", questionsAnswerSummary);
        result.put("meetingAssistance", meetingAssistance);
        result.put("autoChapters", autoChapters);

        // 投诉记录
        List<VoiceAnalysisRecord> recordList = new ArrayList<>();
        if (StrUtil.isNotEmpty(voiceAnalysis.getComplaintPeopleFeature())) {
            recordList = voiceAnalysisRecordService.list(Wrappers.<VoiceAnalysisRecord>lambdaQuery().eq(VoiceAnalysisRecord::getComplaintPeopleFeature, voiceAnalysis.getComplaintPeopleFeature()).eq(VoiceAnalysisRecord::getStatus, "1"));
            result.put("recordList", recordList);
        }
        result.put("yearRecordRate", setYearRecordRate(recordList));
        // 投诉人声纹特征
        if (StrUtil.isNotEmpty(voiceAnalysis.getComplaintPeopleFeature())) {
            result.put("voiceFeature", voiceFeatureService.getOne(Wrappers.<VoiceFeature>lambdaQuery().eq(VoiceFeature::getId, voiceAnalysis.getComplaintPeopleFeature())));
        }
        result.put("callRecord", illmCallRecordService.list(Wrappers.<LLMCallRecord>lambdaQuery().eq(LLMCallRecord::getVoiceCode, voiceAnalysis.getCode())));
        return result;
    }

    /**
     * 获取年份投诉统计
     *
     * @param recordList 记录
     * @return 结果
     */
    public List<LinkedHashMap<String, Object>> setYearRecordRate(List<VoiceAnalysisRecord> recordList) {
        // 返回数据
        List<LinkedHashMap<String, Object>> result = new ArrayList<>();
        // 本年年份
        int year = DateUtil.year(new Date());

        if (CollectionUtil.isNotEmpty(recordList)) {
            recordList.forEach(record -> record.setYear(DateUtil.year(DateUtil.parseDate(record.getCreateDate()))));
        }
        Map<Integer, List<VoiceAnalysisRecord>> recordMap = recordList.stream().collect(Collectors.groupingBy(VoiceAnalysisRecord::getYear));

        for (int i = year - 8; i <= year; i++) {
            int index = i;
            result.add(new LinkedHashMap<String, Object>() {
                {
                    put("name", index + "年");
                    put("value", (CollectionUtil.isEmpty(recordMap) || CollectionUtil.isEmpty(recordMap.get(index))) ? 0 : recordMap.get(index).size());
                }
            });
        }
        return result;
    }

    /**
     * 递归设置思维导图
     *
     * @param mindMapSummary     父级
     * @param mindMapSummaryList 导图列表
     */
    public void setMindData(MindResult mindMapSummary, List<MindMapSummary> mindMapSummaryList) {
        // 子集数据
        List<MindResult> children = new ArrayList<>();
        // 过滤
        List<MindMapSummary> result = mindMapSummaryList.stream().filter(e -> e.getParentCode().equals(mindMapSummary.getId())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(result)) {
            for (MindMapSummary mapSummary : result) {
                MindResult mmResult = new MindResult(mapSummary.getCode(), mapSummary.getTitle());
                mindMapSummary.getChildren().add(mmResult);
                children.add(mmResult);
                this.setMindData(mmResult, mindMapSummaryList);
            }
        } else {
            mindMapSummary.setChildren(children);
        }
    }

    public static void main(String[] args) {
        // 获取返回数据
        String resultData = HttpUtil.get("https://prod-tingwu-paas-common-beijing.oss-cn-beijing.aliyuncs.com/tingwu/output/1596181557257961/82d86db8347744cca10ad711be7c69a1/82d86db8347744cca10ad711be7c69a1_MeetingAssistance_20240725095355.json?Expires=1724464516&OSSAccessKeyId=LTAI5tMzZ1D4o1drkJN1TfCr&Signature=voDtm5OIVJS8eoFXeLoO76amkOY%3D");
        if (StrUtil.isEmpty(resultData)) {
            return;
        }

        // 数据解析
        Map<String, Objects> resultMap = JSONUtil.toBean(resultData, Map.class);
        System.out.println(JSONUtil.toJsonStr(resultMap));
    }

}
