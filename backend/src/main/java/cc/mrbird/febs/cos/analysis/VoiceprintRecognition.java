package cc.mrbird.febs.cos.analysis;

import cc.mrbird.febs.common.utils.DirUtil;
import cc.mrbird.febs.common.utils.R;
import cc.mrbird.febs.cos.recognition.util.*;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 声纹识别
 */
@RestController()
@Component
@RequestMapping("/test")
@Slf4j
public class VoiceprintRecognition {

    /**
     * 讯飞接口地址
     **/
    private final static String requestUrl = "https://api.xf-yun.com/v1/private/s782b4996";
    /**
     * 讯飞鉴权参数
     **/
    private final static String APPID = "75143dad";
    private final static String apiSecret = "NmFlMzQ4NmFjZDdhZjkxOWRhNDMzODRl";
    private final static String apiKey = "019d848b43cfeacdb876b2d4fe430834";

    /**
     * 声纹库参数
     **/
    private final static String groupId = "voiceprint_complaint_group";
    private final static String groupName = "12345Complaint";
    private final static String groupInfo = "12345投诉声纹库";

    /**
     * 1:N返回结果数 最大为10
     **/
    private final static int topK = 1;

    /**
     * 上传的音频信息 此处不变 音频分割时会适应参数
     **/
    private final static String encoding = "lame";   // 音频编码 固定
    private final static int sampleRate = 16000;          // 采样率
    private final static int channels = 1;                 // 声道数
    private final static int bitDepth = 16;               // 位深度

    public final static Double threshold = 0.65;

    @GetMapping("/Voiceprint")
    public R voiceSplitTest(@RequestParam String filePath) {

        return R.ok(queryVoiceFeature(filePath));
    }

    /**
     * 初始化声纹库
     */
    private static void initAudioGroup() {
        Map<String, Object> params = new HashMap<>();
        params.put("groupId", groupId);
        params.put("groupName", groupName);
        params.put("groupInfo", groupInfo);
        //创建声纹库
        CreateGroup.doCreateGroup(requestUrl, APPID, apiSecret, apiKey, params);
        params.put("encoding", encoding);
        params.put("sampleRate", sampleRate);
        params.put("channels", channels);
        params.put("bitDepth", bitDepth);
        params.put("featureId", "demo");
        params.put("featureInfo", "this is audio demo...");
        params.put("audioFile", DirUtil.projectRootDir + "/audio.mp3");
        //创建声纹库中一条示例声纹
        try {
            CreateFeature.doCreateFeature(requestUrl, APPID, apiSecret, apiKey, params);
        } catch (Exception e) {
            log.error("声纹库初始化失败...");
        }

    }

    /**
     * 声纹库新增
     *
     * @param featureInfo
     * @param filePath
     * @return
     */
    public static Boolean addVoiceFeature(String featureInfo, String filePath) {
        String featureId = "VF-" + System.currentTimeMillis();
        Map<String, Object> params = new HashMap<>();
        params.put("groupId", groupId);
        params.put("encoding", encoding);
        params.put("sampleRate", sampleRate);
        params.put("channels", channels);
        params.put("bitDepth", bitDepth);
        params.put("featureId", featureId);
        params.put("featureInfo", featureInfo);
        params.put("audioFile", DirUtil.projectRootDir + "/" + filePath);
        //创建声纹库中一条示例声纹
        try {
            JSONObject jsonObject = CreateFeature.doCreateFeature(requestUrl, APPID, apiSecret, apiKey, params);
            if (featureId.equals(jsonObject.getString("featureId"))) {
                return true;
            }
        } catch (Exception e) {
            log.error("添加声纹失败：featureId: {%s}  splitFile: {%s} ", featureId, filePath);
        } finally {
            return false;
        }
    }

    /**
     * 声纹库新增（手动）
     *
     * @param featureId   特征唯一标识
     * @param featureInfo 特征信息
     * @param inputStream 录音文件
     * @return
     */
    public static Boolean addVoiceFeatureManual(String featureId, String featureInfo, InputStream inputStream) {
        Map<String, Object> params = new HashMap<>();
        params.put("groupId", groupId);
        params.put("encoding", encoding);
        params.put("sampleRate", sampleRate);
        params.put("channels", channels);
        params.put("bitDepth", bitDepth);
        params.put("featureId", featureId);
        params.put("featureInfo", featureInfo);
        params.put("audioFile", inputStream);
        //创建声纹库中一条示例声纹
        try {
            JSONObject jsonObject = CreateFeature.doCreateFeature(requestUrl, APPID, apiSecret, apiKey, params);
            if (featureId.equals(jsonObject.getString("featureId"))) {
                log.info(featureId + "添加声纹特征成功");
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("添加声纹失败：featureId:  " + featureId);
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }


    /**
     * 获取声纹库中声纹数量
     * 如果为0表示声纹库有问题 因为声纹库在初始化时会默认插入一条声纹
     *
     * @return
     */
    public static int getVoiceFeaturesLength() {
        Map<String, String> params = new HashMap<>();
        params.put("groupId", groupId);

        try {
            JSONArray result = QueryFeatureList.doQueryFeatureList(requestUrl, APPID, apiSecret, apiKey, params);
            log.info(JSONUtil.toJsonStr(result));
            return result.size();
        } catch (Exception e) {
            log.error("获取声纹库声纹列表失败：", e.getMessage());
//            initAudioGroup();
        } finally {
            return 0;
        }
    }

    /**
     * 1:N 声纹识别
     *
     * @param filePath 切割过的音频
     */
    public static Map<String,List> queryVoiceFeature(String filePath) {
        JSONObject result = null;
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("groupId", groupId);
            params.put("topK", topK);
            params.put("encoding", encoding);
            params.put("sampleRate", sampleRate);
            params.put("channels", channels);
            params.put("bitDepth", bitDepth);
            params.put("audioFile", new FileInputStream(filePath));
            result = SearchFeature.doSearchFeature(requestUrl, APPID, apiSecret, apiKey, params);
        } catch (Exception e) {
            log.error("1:N 声纹查询失败");
            e.printStackTrace();
        }
        return thresholdCompare(result);
    }

    /**
     * 1:N 声纹识别
     *
     * @param inputStream 录音文件
     */
    public static Map<String,List> queryVoiceFeatureManual(InputStream inputStream) {
        Map<String, Object> params = new HashMap<>();
        params.put("groupId", groupId);
        params.put("topK", topK);
        params.put("encoding", encoding);
        params.put("sampleRate", sampleRate);
        params.put("channels", channels);
        params.put("bitDepth", bitDepth);
        params.put("audioFile", inputStream);
        JSONObject result = null;
        try {
            result = SearchFeature.doSearchFeature(requestUrl, APPID, apiSecret, apiKey, params);
        } catch (Exception e) {
            log.error("1:N 声纹查询失败");
        }
        return thresholdCompare(result);
    }

    /**
     * 批量删除声纹特征信息
     *
     * @param featureIds 特征唯一ID
     */
    @Async
    public void batchDelCVoiceFeature(List<Integer> featureIds) {
        for (Integer featureId : featureIds) {
            delCVoiceFeature(featureId.toString());
        }
    }

    /**
     * 删除声纹特征信息
     *
     * @param featureId 特征唯一ID
     */
    public void delCVoiceFeature(String featureId) {
        Map<String, Object> params = new HashMap<String, Object>(16) {
            {
                put("appId", APPID);
                put("groupId", groupId);
                put("featureId", featureId);
            }
        };

        try {
            DeleteFeature.doDeleteFeature(requestUrl, APPID, apiSecret, apiKey, params);
        } catch (Exception e) {
            log.error("删除声纹特征信息失败");
        }
    }



    private static Map<String,List> thresholdCompare(JSONObject featureResult){
        Map<String,List> result = new HashMap();
//        result.put("scoreList", Collections.emptyList());
        List scoreList = new ArrayList();
        JSONArray scoreJSONList = featureResult.getJSONArray("scoreList");
        scoreJSONList.forEach(item ->{
            JSONObject scoreItemJSON = (JSONObject) item;
            if(scoreItemJSON.getDouble("score") >= VoiceprintRecognition.threshold){
                scoreList.add(scoreItemJSON);
            }
        });
        result.put("scoreList",scoreList);
        return result;
    }

}
