package cc.mrbird.febs.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:emotion-weights.properties")
public class EmotionWeightsConfig {

    @Autowired
    private Environment env;

    public double getEmotionWeight(String emotion) {
        String confidence = env.getProperty(emotion);
        if(confidence == null){
            return 1.0;
        }
        return Double.parseDouble(confidence);
    }

}
