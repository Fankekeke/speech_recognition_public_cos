package cc.mrbird.febs.common.utils;

import com.aliyun.bailian20230601.Client;
import com.aliyun.bailian20230601.models.GetPromptRequest;
import com.aliyun.bailian20230601.models.GetPromptResponse;
import com.aliyun.teaopenapi.models.Config;

import java.util.HashMap;
import java.util.Map;

public class BaiLianPromptUtil {

    private static final String accessKeyId =  "xxx";
    private static final String accessKeySecret = "xxxx";
    private static final String endpoint = "xxxx";
    private static final String agentKey = "xxx";
    private static final String promptId = "xxx";


    public static String getTemplatePrompt(String templateContent) throws Exception {
        Config config = new Config().setAccessKeyId(accessKeyId).setAccessKeySecret(accessKeySecret).setEndpoint(endpoint);
        Client client = new Client(config);
        Map<String,String> map = new HashMap<>();
        map.put("AgentKey",agentKey);
        map.put("PromptId",promptId);
        map.put("Vars","{\"templateContent\":\""+templateContent+"\"}");

        GetPromptResponse response = client.getPrompt(GetPromptRequest.build(map));
        if(response.body.getSuccess()){
            return response.getBody().getData().getPromptContent();
        }else{
            throw new RuntimeException("模板prompt获取失败");
        }
    }

}
