package cc.mrbird.febs.cos.analysis;

import cc.mrbird.febs.common.utils.BaiLianPromptUtil;
import cc.mrbird.febs.cos.entity.LLMCallRecord;
import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.dashscope.app.ApplicationUsage;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 大语言模型LLM调用 (阿里云百炼https://bailian.console.aliyun.com/)
 */
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LLMAnalysisProcess {


    // 阿里云百炼平台API KEY
    private String API_KEY = "xxxxx";

    // 阿里云百炼 应用的APPID
    private String APP_ID = "xxxx";


    /**
     * 调用LLM接口
     *
     * @param prompt    问题提示词
     * @param sessionId 历史问题SessionId null代表新创建一个会话
     * @return
     * @throws ApiException
     * @throws NoApiKeyException
     * @throws InputRequiredException
     */
    private void callAgentApp(String prompt, String sessionId, TaskCallBack taskCallBack)
            throws ApiException, NoApiKeyException, InputRequiredException {

        LLMCallRecord llmCallRecord = new LLMCallRecord();
        llmCallRecord.setInputText(prompt);
        try {
            ApplicationParam.ApplicationParamBuilder paramBuilder = ApplicationParam.builder()
                    .apiKey(API_KEY)
                    .appId(APP_ID)
                    .prompt(prompt);

            // 携带历史sessionId表示同一对话
            if (sessionId != null && sessionId.length() > 0) {
                paramBuilder.sessionId(sessionId);
            }

            ApplicationParam param = paramBuilder.build();
            Application application = new Application();
            llmCallRecord.setRequestTime(LocalDateTime.now());
            ApplicationResult result = application.call(param);
            llmCallRecord.setResponseTime(LocalDateTime.now());

            System.out.printf("requestId: %s, text: %s, finishReason: %s\n",
                    result.getRequestId(), result.getOutput().getText(), result.getOutput().getFinishReason());
            llmCallRecord.setResponseCode(200);
            llmCallRecord.setOutputText(result.getOutput().getText());
            llmCallRecord.setSessionId(result.getOutput().getSessionId());

            if (result.getUsage() != null && result.getUsage().getModels() != null) {
                List<ApplicationUsage.ModelUsage> usageModels = result.getUsage().getModels();
                if (usageModels.size() > 0) {
                    llmCallRecord.setModelId(usageModels.get(0).getModelId());
                    llmCallRecord.setInputTokens(usageModels.get(0).getInputTokens());
                    llmCallRecord.setOutputTokens(usageModels.get(0).getOutputTokens());
                }
            }

        } catch (ApiException apiException) {
            llmCallRecord.setResponseCode(500);
            llmCallRecord.setResponseException(apiException.getMessage());
        } catch (NoApiKeyException noApiKeyException) {
            llmCallRecord.setResponseCode(500);
            llmCallRecord.setResponseException(noApiKeyException.getMessage());
        } catch (InputRequiredException inputRequiredException) {
            llmCallRecord.setResponseCode(500);
            llmCallRecord.setResponseException(inputRequiredException.getMessage());
        } finally {
            taskCallBack.onComplete(llmCallRecord);
        }

    }


    /**
     * 新建会话调用
     *
     * @param content 内容
     */
    public Boolean startLLMScheduling(String content, String templateContent, TaskCallBack taskCallBack) {
        Boolean result = Boolean.FALSE;
        try {
            StringBuffer stringBuffer = new StringBuffer();
            String templateText = BaiLianPromptUtil.getTemplatePrompt(templateContent);
            stringBuffer.append(templateText);
            stringBuffer.append("\n\n");
            stringBuffer.append("# 正文");
            stringBuffer.append(content);
            log.info("输入 prompt：{%s}", stringBuffer);
            callAgentApp(content, null, taskCallBack);
            result = Boolean.TRUE;
            return result;
        } catch (NoApiKeyException e) {
            throw new RuntimeException(e);
        } catch (InputRequiredException e) {
            throw new RuntimeException(e);
        } finally {
            return result;
        }
    }

    /**
     * 历史会话调用
     *
     * @param content   内容
     * @param sessionId 会话id
     */
    public Boolean sessionLLMScheduling(String content, String sessionId, TaskCallBack taskCallBack) {
        Boolean result = Boolean.FALSE;

        try {
            callAgentApp(content, sessionId, taskCallBack);
            result = Boolean.TRUE;
            return result;
        } catch (NoApiKeyException e) {
            throw new RuntimeException(e);
        } catch (InputRequiredException e) {
            throw new RuntimeException(e);
        } finally {
            return result;
        }
    }

}
