package com.nbhy.modules.upload.config;

import com.nbhy.modules.upload.constant.UploadModeEnum;
import com.nbhy.utils.StringUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Data
//@Configuration
//@ConfigurationProperties(prefix = "aliyun.oss")
@Component
public class OssProperties {

    @Autowired
    private Environment environment;

    @Value("${storage.type}")
    private UploadModeEnum uploadModeEnum;

    private  String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private Long maxSize;
    private String filePrefix;
    private Long expire;
    private String baseUrl;
    private String callback;



    @PostConstruct
    public void init(){
        if(uploadModeEnum == UploadModeEnum.oss){
            this.endpoint = environment.getProperty("aliyun.oss.endpoint");
            this.accessKeyId = environment.getProperty("aliyun.oss.accessKeyId");
            this.accessKeySecret = environment.getProperty("aliyun.oss.accessKeySecret");
            this.bucketName = environment.getProperty("aliyun.oss.bucketName");
            String maxSize = environment.getProperty("aliyun.oss.maxSize");
            if(!StringUtils.isEmpty(maxSize)) {
                this.maxSize = Long.valueOf(maxSize);
            }
            String expire = environment.getProperty("aliyun.oss.policy.expire");
            if(!StringUtils.isEmpty(expire)) {
                this.expire = Long.valueOf(expire);
            }
            this.filePrefix = environment.getProperty("aliyun.oss.dir.prefix");
            this.baseUrl = environment.getProperty("aliyun.oss.baseUrl");
            this.callback = environment.getProperty("aliyun.oss.callback");

        }
    }

}
