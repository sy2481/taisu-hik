package com.nbhy.modules.upload.config.oss;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.nbhy.modules.upload.config.FileProperties;
import com.nbhy.modules.upload.config.OssProperties;
import com.nbhy.modules.upload.constant.UploadModeEnum;
import com.nbhy.modules.upload.util.OssBootUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
//@ConditionalOnExpression("${storage.type} == 'oss'?true:false")
public class OssBootConfiguration {

    @Autowired
    private OssProperties ossProperties;

    @Autowired
    private FileProperties fileProperties;


    @Bean
//    @ConditionalOnExpression("#{myFileProperties.isOss()}")
    public OSSClient ossClient(){
        if(fileProperties.getUploadModeEnum() == UploadModeEnum.oss) {
            return new OSSClient(ossProperties.getEndpoint(),
                    new DefaultCredentialProvider(ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret()),
                    new ClientConfiguration());
        }else{
            return null;
        }
    }



    @PostConstruct
    public void initOssBootConfiguration() {
        OssBootUtil.setEndPoint(ossProperties.getEndpoint());
        OssBootUtil.setAccessKeyId(ossProperties.getAccessKeyId());
        OssBootUtil.setAccessKeySecret(ossProperties.getAccessKeySecret());
        OssBootUtil.setBucketName(ossProperties.getBucketName());
        OssBootUtil.setMaxSize(ossProperties.getMaxSize());
        OssBootUtil.setBaseUrl(ossProperties.getBaseUrl());
    }


}