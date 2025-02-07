package cc.mrbird.febs.common.voice;

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
import org.junit.Test;

/**
 * @author tingwu2023
 * @desc 演示了通过OpenAPI 创建音视频文件的离线转写任务 的调用方式。
 */
public class SubmitFiletransTaskTest {

    @Test
    public void summitTask() throws ClientException {
        CommonRequest request = createCommonRequest("xxx", "2023-09-30", ProtocolType.HTTPS, MethodType.PUT, "/openapi/tingwu/v2/tasks");
        request.putQueryParameter("type", "offline");

        JSONObject root = new JSONObject();
        root.put("AppKey", "xxx");

        String path = "https://xzhi-app.oss-cn-beijing.aliyuncs.com/20240407161222-17691059528-2-0001Admin.Mp3";
        JSONObject input = new JSONObject();
        input.fluentPut("FileUrl", path)
                .fluentPut("SourceLanguage", "cn")
                .fluentPut("TaskKey", "task" + System.currentTimeMillis());
        root.put("Input", input);

        JSONObject parameters = initRequestParameters();
        root.put("Parameters", parameters);

        System.out.println(root.toJSONString());
        request.setHttpContent(root.toJSONString().getBytes(), "utf-8", FormatType.JSON);

        // TODO 请通过环境变量设置您的AccessKeyId、AccessKeySecret
        DefaultProfile profile = DefaultProfile.getProfile("cn-beijing", "xx", "xxx");
        IAcsClient client = new DefaultAcsClient(profile);
        CommonResponse response = client.getCommonResponse(request);
        System.out.println(response.getData());
        JSONObject body = JSONObject.parseObject(response.getData());
        JSONObject data = (JSONObject) body.get("Data");
        System.out.println("TaskId = " + data.getString("TaskId"));
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
