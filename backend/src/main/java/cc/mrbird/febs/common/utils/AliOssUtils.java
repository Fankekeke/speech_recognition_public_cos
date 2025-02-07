package cc.mrbird.febs.common.utils;

import cc.mrbird.febs.common.config.AliOssProperties;
import cn.hutool.core.lang.UUID;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author fk
 *
 * 阿里云OSS工具类
 */
@Component
public class AliOssUtils {

    @Resource
    private AliOssProperties aliOssProperties;

    /**
     * 以下是一个实现上传文件到OSS的方法
     */
    public String upload(MultipartFile file) throws IOException {

        //获取阿里云OSS参数
        String endpoint = aliOssProperties.getEndpoint();
        String accessKeyId = aliOssProperties.getAccessKeyId();
        String accessKeySecret = aliOssProperties.getAccessKeySecret();
        String bucketName = aliOssProperties.getBucketName();

        // 获取上传的文件的输入流
        InputStream inputStream = file.getInputStream();

        // 避免文件覆盖，需要使用UUID将文件重命名
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.fastUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));

        //上传文件到 OSS
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.putObject(bucketName, fileName, inputStream);

        //文件访问路径
        String url = endpoint.split("//")[0] + "//" + bucketName + "." + endpoint.split("//")[1] + "/" + fileName;

        // 关闭ossClient
        ossClient.shutdown();

        // 把上传到oss的路径返回
        return url;

    }

    public String upload(InputStream inputStream, String originalFilename) throws IOException {

        //获取阿里云OSS参数
        String endpoint = aliOssProperties.getEndpoint();
        String accessKeyId = aliOssProperties.getAccessKeyId();
        String accessKeySecret = aliOssProperties.getAccessKeySecret();
        String bucketName = aliOssProperties.getBucketName();

        // 避免文件覆盖，需要使用UUID将文件重命名
        String fileName = UUID.fastUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));

        //上传文件到 OSS
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.putObject(bucketName, fileName, inputStream);

        //文件访问路径
        String url = endpoint.split("//")[0] + "//" + bucketName + "." + endpoint.split("//")[1] + "/" + fileName;

        // 关闭ossClient
        ossClient.shutdown();

        // 把上传到oss的路径返回
        return url;

    }
}
