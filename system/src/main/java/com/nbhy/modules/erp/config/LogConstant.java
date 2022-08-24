package com.nbhy.modules.erp.config;

import com.nbhy.modules.hik.constant.HikDeviceConstant;
import io.swagger.models.auth.In;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: xcjx
 * @Email: nizhaobudaowo@163.com
 * @Company: nbhy
 * @Date: Created in 21:17 2022/3/13
 * @ClassName: LogConstant
 * @Description: 设备常量
 * @Version: 1.0
 */
public class LogConstant {

    /**
     * 进场
     */
    public static final String MARCH_INTO_THE_ARENA = "0";

    /**
     * 离场
     */
    public static final String LEAVE = "1";



    public static final Map<Integer, String> DEVICE_TO_LOG_PASS_IN_AND_OUT = new HashMap<Integer, String>(){{
        put(HikDeviceConstant.GO_OUT,LEAVE);
        put(HikDeviceConstant.ENTER_THE_DOOR,MARCH_INTO_THE_ARENA);
    }};

}
