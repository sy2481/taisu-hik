package com.nbhy.modules.hik.constant;

import io.swagger.models.auth.In;

import java.util.HashMap;
import java.util.Map;

/**
 * 海康设备常量
 */
public class HikDeviceConstant {

    /**
     * 人脸设备
     */
    public static final int FACE_DEVICE = 0;

    /**
     * 车辆设备
     */
    public static final int CAR_DEVICE = 1;


    /**
     * 普通设备
     */
    public static final int ORDINARY_EQUIPMENT = 0;

    /**
     * 绑定定位卡设备
     */
    public static final int BIND_THE_POSITIONING_CARD_DEVICE = 1;

    /**
     * 车道绑定的人脸设备
     */
    public static final int LANE_BOUND_FACE_DEVICE = 2;


    /**
     * 1-进 2-出
     * 进门
     */
    public static final int ENTER_THE_DOOR = 1;

    /**
     * 出门
     */
    public static final int GO_OUT = 2;


    public static final Map<Integer,String> IN_OR_OUT_IDENTIFICATION = new HashMap<Integer, String>(){{
       put(ENTER_THE_DOOR,"ENTER");
        put(GO_OUT,"OUT");
    }};


}
