package com.nbhy.modules.erp.config;

import com.hikvision.artemis.sdk.config.ArtemisConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 海康
 */
@Slf4j
@Component
public class ERPConfigConstant {
    @Autowired
    private Environment environment;

    public static String HOST;


    @PostConstruct
    public void init() {
        HOST = this.environment.getProperty("erp.host");
        log.info("初始化erp参数");
    }
}

