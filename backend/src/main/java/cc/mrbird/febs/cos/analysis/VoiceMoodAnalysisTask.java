package cc.mrbird.febs.cos.analysis;

import cc.mrbird.febs.common.config.EmotionWeightsConfig;
import cc.mrbird.febs.common.utils.R;
import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.util.*;

/**
 * 声音情绪分析任务
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VoiceMoodAnalysisTask {

    private final String mood_url = "http://47.92.140.181:43981/audioAnalyze/mood";


   private final EmotionWeightsConfig emotionWeightsConfig;

    /**
     * 获取音频情绪
     * @param filePath
     * @return
     */
    public JSONObject getVoiceMood(String filePath){
        HttpResponse httpResponse = HttpRequest.post(mood_url)
                .form("file",new File(filePath)).execute();
        String result = httpResponse.body();
        return JSONObject.parseObject(result);
    }

    /**
     * 情绪分析 加权计算
     */
    public List<String> analyzeEmotions(List<JSONObject> emotionsList) {
        Map<String, Integer> emotionCounts = new HashMap<>();
        Map<String, Double> emotionConfidences = new HashMap<>();

        for (JSONObject mood: emotionsList) {
            String emotion = mood.getString("emotion");
            double confidence = mood.getDouble("confidence");

            // Apply the weight to the confidence if the emotion is in the weight map
            double weightedConfidence = confidence * emotionWeightsConfig.getEmotionWeight(emotion);

            emotionCounts.put(emotion, emotionCounts.getOrDefault(emotion, 0) + 1);
            emotionConfidences.put(emotion, emotionConfidences.getOrDefault(emotion, 0.0) + weightedConfidence);
        }

        // Calculate weighted average confidence for each emotion
        for (String emotion : emotionConfidences.keySet()) {
            emotionConfidences.put(emotion, emotionConfidences.get(emotion) / emotionCounts.get(emotion));
        }

        // Sort emotions by their weighted average confidence
        List<Map.Entry<String, Double>> sortedEmotions = new ArrayList<>(emotionConfidences.entrySet());
        sortedEmotions.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        // Get the top two emotions
        List<String> topTwoEmotions = new ArrayList<>();
        for (int i = 0; i < Math.min(2, sortedEmotions.size()); i++) {
            topTwoEmotions.add(sortedEmotions.get(i).getKey());
        }

        return topTwoEmotions;
    }


    public static void main(String[] args) {
//        VoiceMoodAnalysisTask voiceMoodAnalysisTask = new VoiceMoodAnalysisTask();
//        JSONObject result = voiceMoodAnalysisTask.getVoiceMood("F:\\work\\complaint-platform1\\backend\\tmp\\temp_speaker2_seg22.mp3");
//        System.out.println(result);
//        String json = "[{\"emotion\":\"平静\",\"confidence\":0.85}\n" +
//                "{\"emotion\":\"愤怒\",\"confidence\":0.98}\n" +
//                "{\"emotion\":\"平静\",\"confidence\":1.0}\n" +
//                "{\"emotion\":\"愤怒\",\"confidence\":0.98}\n" +
//                "{\"emotion\":\"平静\",\"confidence\":0.85}\n" +
//                "{\"emotion\":\"困惑\",\"confidence\":0.75}\n" +
//                "{\"emotion\":\"平静\",\"confidence\":1.0}\n" +
//                "{\"emotion\":\"平静\",\"confidence\":0.85}\n" +
//                "{\"emotion\":\"困惑\",\"confidence\":0.85}\n" +
//                "{\"emotion\":\"困惑\",\"confidence\":0.85}]";
//        JSONArray jsonArray = JSONArray.parseArray(json);
//        List<JSONObject> jsonObjects = jsonArray.toJavaList(JSONObject.class);
//        Map<String, Double> emotionWeights = new HashMap<>();
//        emotionWeights.put("愤怒", 2.0);
//        emotionWeights.put("困惑", 1.8);
//        emotionWeights.put("平静", 0.5);
//        List<String> result = analyzeEmotions(jsonObjects, emotionWeights);
////
//        System.out.println(result);

    }

}
