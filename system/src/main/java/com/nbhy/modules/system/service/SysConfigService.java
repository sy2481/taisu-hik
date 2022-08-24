package com.nbhy.modules.system.service;

import com.nbhy.modules.system.domain.entity.SysConfig;

/**
 * @Author: xcjx
 * @Email: nizhaobudaowo@163.com
 * @Company: nbhy
 * @Date: Created in 15:25 2022/3/17
 * @ClassName: SysConfigService
 * @Description: 系统配置
 * @Version: 1.0
 */
public interface SysConfigService {

    /**
     * 获取系统配置
     * @return
     */
    SysConfig getConfig();


    /**
     * 设置是否启用定位卡
     * @param locationCardEnabled
     */
    void setLocationCardEnabled(Boolean locationCardEnabled);
}
