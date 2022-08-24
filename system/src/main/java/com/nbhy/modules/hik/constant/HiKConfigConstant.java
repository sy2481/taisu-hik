package com.nbhy.modules.hik.constant;

import com.hikvision.artemis.sdk.config.ArtemisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 海康
 */
@Component
public class HiKConfigConstant {
    @Autowired
    private Environment environment;


    public static final String ARTEMIS_PATH = "/artemis";

    public static String HOST;
    public static String APP_KEY;
    public static String APP_SECRET;

    public static String BASE_URL;

    public static String HTTP_PREFIX;


    @PostConstruct
    public void init(){
        HOST = this.environment.getProperty("hik.host");
        APP_KEY = this.environment.getProperty("hik.appKey");
        APP_SECRET = this.environment.getProperty("hik.appSecret");
        Boolean ssl = this.environment.getProperty("hik.ssl",Boolean.class);


        ArtemisConfig.host = HOST;
        ArtemisConfig.appKey = APP_KEY;
        ArtemisConfig.appSecret = APP_SECRET;

        if(ssl != null && ssl){
            HTTP_PREFIX = "https://";
        }else{
            HTTP_PREFIX = "http://";
        }

        BASE_URL = HTTP_PREFIX+ HOST;
    }
}
