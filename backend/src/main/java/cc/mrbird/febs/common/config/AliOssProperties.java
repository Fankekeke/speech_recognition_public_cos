package cc.mrbird.febs.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author fk
 *
 * 阿里云OSS配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class AliOssProperties {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
}
