package cc.mrbird.febs.cos.analysis;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.stereotype.Component;

/**
 * @author tingwu2023
 * 演示了通过OpenAPI 根据TaskId查询任务状态和结果 的调用方式。
 */
@Component
public class GetTaskInfo {

    /**
     * 根据TaskId查询任务状态和结果
     *
     * @param taskId 任务ID、
     * @return 结果
     * @throws ClientException 异常
     */
    public String getTaskInfo(String taskId) throws ClientException {
        String queryUrl = String.format("/openapi/tingwu/v2/tasks" + "/%s", taskId);

        CommonRequest request = createCommonRequest("tingwu.cn-beijing.aliyuncs.com", "2023-09-30", ProtocolType.HTTPS, MethodType.GET, queryUrl);
        // TODO 请通过环境变量设置您的AccessKeyId、AccessKeySecret
        DefaultProfile profile = DefaultProfile.getProfile("cn-beijing", "xxxx", "xxx");
        IAcsClient client = new DefaultAcsClient(profile);
        CommonResponse response = client.getCommonResponse(request);
        return response.getData();
    }

    /**
     * 根据任务ID获取任务读写信息
     *
     * @param taskId 任务ID
     * @return 结果
     * @throws ClientException 异常
     */
    public String getTaskInfoByTaskId(String taskId) throws ClientException {
        String queryUrl = String.format("/openapi/tingwu/v2/tasks" + "/%s", taskId);

        CommonRequest request = createCommonRequest("tingwu.cn-beijing.aliyuncs.com", "2023-09-30", ProtocolType.HTTPS, MethodType.GET, queryUrl);
        // TODO 请通过环境变量设置您的AccessKeyId、AccessKeySecret
        DefaultProfile profile = DefaultProfile.getProfile("cn-beijing", "xxxx", "xxxx");
        IAcsClient client = new DefaultAcsClient(profile);
        CommonResponse response = client.getCommonResponse(request);
        System.out.println(response.getData());
        return response.getData();
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
