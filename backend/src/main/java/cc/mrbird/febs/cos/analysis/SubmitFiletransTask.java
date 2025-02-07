package cc.mrbird.febs.cos.analysis;

import cc.mrbird.febs.cos.dao.VoiceAnalysisRecordMapper;
import cc.mrbird.febs.cos.entity.TranscriptionRecord;
import cc.mrbird.febs.cos.entity.VoiceAnalysisRecord;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author tingwu2023
 * 演示了通过OpenAPI 创建音视频文件的离线转写任务 的调用方式。
 */
@Component
public class SubmitFiletransTask {

    @Resource
    private GetTaskInfo getTaskInfo;

    @Resource
    private VoiceAnalysisRecordMapper voiceAnalysisRecordMapper;

    @Resource
    private DataAnalysisProcess dataAnalysisProcess;

    /**
     * 创建音视频文件的离线转写任务
     *
     * @param voiceUrl 音频地址
     * @return 任务ID
     * @throws ClientException 异常
     */
    public String summitTask(String voiceUrl) throws ClientException {
        CommonRequest request = createCommonRequest("tingwu.cn-beijing.aliyuncs.com", "2023-09-30", ProtocolType.HTTPS, MethodType.PUT, "/openapi/tingwu/v2/tasks");
        request.putQueryParameter("type", "offline");

        JSONObject root = new JSONObject();
        root.put("AppKey", "J90i3M6zDWCx9Sm6");

        JSONObject input = new JSONObject();
        input.fluentPut("FileUrl", voiceUrl)
                .fluentPut("SourceLanguage", "cn")
                .fluentPut("TaskKey", "task" + System.currentTimeMillis());
        root.put("Input", input);

        JSONObject parameters = initRequestParameters();
        root.put("Parameters", parameters);

        System.out.println(root.toJSONString());
        request.setHttpContent(root.toJSONString().getBytes(), "utf-8", FormatType.JSON);

        // TODO 请通过环境变量设置您的AccessKeyId、AccessKeySecret
        DefaultProfile profile = DefaultProfile.getProfile("cn-beijing", "xxxx", "xxx");
        IAcsClient client = new DefaultAcsClient(profile);
        CommonResponse response = client.getCommonResponse(request);
        System.out.println(response.getData());
        JSONObject body = JSONObject.parseObject(response.getData());
        JSONObject data = (JSONObject) body.get("Data");
        System.out.println("TaskId = " + data.getString("TaskId"));
        return data.getString("TaskId");
    }

    /**
     * 获取任务执行状态
     */
    @Async
    public void getTaskInfo(String taskId) throws ClientException, InterruptedException {
        // 获取任务读写状态
        String result = getTaskInfo.getTaskInfo(taskId);
        cn.hutool.json.JSONObject taskObject = JSONUtil.parseObj(result);
        // 读写记录
        VoiceAnalysisRecord record = voiceAnalysisRecordMapper.selectOne(Wrappers.<VoiceAnalysisRecord>lambdaQuery().eq(VoiceAnalysisRecord::getTaskId, taskId));
        while (true) {
            // 任务状态
            String taskStatus = taskObject.getByPath("Data.TaskStatus", String.class);
            if ("COMPLETED".equals(taskStatus)) {
                System.out.println("读写记录=====>：任务完成...");

                String meetingAssistanceUrl = taskObject.getByPath("Data.Result.MeetingAssistance", String.class);
                String summaryUrl = taskObject.getByPath("Data.Result.Summarization", String.class);
                String autoChaptersUrl = taskObject.getByPath("Data.Result.AutoChapters", String.class);
                String transcriptionUrl = taskObject.getByPath("Data.Result.Transcription", String.class);
                String textPolishUrl = taskObject.getByPath("Data.Result.TextPolish", String.class);

                Map<String, String> transcriptionRecordMap = dataAnalysisProcess.transcriptionHandel(transcriptionUrl, record.getId());
                dataAnalysisProcess.textPolishHandel(textPolishUrl, record.getId(), transcriptionRecordMap);
                dataAnalysisProcess.meetingAssistanceHandel(meetingAssistanceUrl, record.getId());
                dataAnalysisProcess.summaryHandel(summaryUrl, record.getId());
                dataAnalysisProcess.autoChaptersHandel(autoChaptersUrl, record.getId());

                record.setStatus("1");
                voiceAnalysisRecordMapper.updateById(record);
                // 跳出循环
                break;
            } else if ("FAILED".equals(taskStatus)) {
                System.out.println("读写记录=====>：任务失败...");
                record.setStatus("2");
                voiceAnalysisRecordMapper.updateById(record);
                // 跳出循环
                break;
            } else {
                // 30秒查询一次
                Thread.sleep(30000);
                System.out.println("读写记录=====>：任务还在进行中，继续查询...");
                result = getTaskInfo.getTaskInfo(taskId);
                taskObject = JSONUtil.parseObj(result);
            }
        }
    }

    public static JSONObject initRequestParameters() {
        JSONObject parameters = new JSONObject();

        // 音视频转换： 可选
        JSONObject transcoding = new JSONObject();
        transcoding.put("TargetAudioFormat", "mp3");
        transcoding.put("SpectrumEnabled", false);
        parameters.put("Transcoding", transcoding);

        // 语音识别
        JSONObject transcription = new JSONObject();
        // 是否在语音识别过程中开启说话人分离功能。
        transcription.put("DiarizationEnabled", true);
        JSONObject speaker_count = new JSONObject();
        speaker_count.put("SpeakerCount", 2);
        transcription.put("Diarization", speaker_count);
        parameters.put("Transcription", transcription);

        // 翻译： 可选
        JSONObject translation = new JSONObject();
        JSONArray langArry = new JSONArray();
        langArry.add("en");
        translation.put("TargetLanguages", langArry);
        parameters.put("Translation", translation);
        parameters.put("TranslationEnabled", false);

        // 章节速览： 可选
        parameters.put("AutoChaptersEnabled", true);

        // 智能纪要： 可选
        parameters.put("MeetingAssistanceEnabled", true);
        JSONObject meetingAssistance = new JSONObject();
        JSONArray mTypes = new JSONArray().fluentAdd("Actions").fluentAdd("KeyInformation");
        meetingAssistance.put("Types", mTypes);
        parameters.put("MeetingAssistance", meetingAssistance);

        // 摘要相关： 可选
        parameters.put("SummarizationEnabled", true);
        JSONObject summarization = new JSONObject();
        JSONArray sTypes = new JSONArray().fluentAdd("Paragraph").fluentAdd("Conversational").fluentAdd("QuestionsAnswering").fluentAdd("MindMap");
        summarization.put("Types", sTypes);
        parameters.put("Summarization", summarization);

        // PPT抽取： 可选
        parameters.put("PptExtractionEnabled", true);

        // 口语书面化： 可选
        parameters.put("TextPolishEnabled", true);

        return parameters;
    }

    public static CommonRequest createCommonRequest(String domain, String version, ProtocolType protocolType, MethodType method, String uri) {
        // 创建API请求并设置参数
        CommonRequest request = new CommonRequest();
        request.setSysDomain(domain);
        request.setSysVersion(version);
        request.setSysProtocol(protocolType);
        request.setSysMethod(method);
        request.setSysUriPattern(uri);
        request.setHttpContentType(FormatType.JSON);
        return request;
    }
}
