package com.nbhy.modules.ssm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;

@Configuration
public class SMSConstant {
    @Autowired
    private Environment environment;

    public static String  AccessKeyID;
    public static String  AccessKeySecret;
    public static String  SignName;
    public static String  TemplateCode;

    @PostConstruct
    public void init(){
        SMSConstant.AccessKeyID = this.environment.getProperty("aliyun.sms.AccessKeyID");
        SMSConstant.AccessKeySecret = this.environment.getProperty("aliyun.sms.AccessKeySecret");
        SMSConstant.SignName = this.environment.getProperty("aliyun.sms.SignName");
        SMSConstant.TemplateCode = this.environment.getProperty("aliyun.sms.TemplateCode");
    }

}
