package com.nbhy.modules.hik.domain.dto;

import lombok.Data;

/**
 * @Author: yyf
 * @Date: Created in 9:47 2021/12/2
 * @ClassName: 海康车辆信息
 * @Description:
 */
@Data
public class HikCar {
    /**
     * 调用方指定Id 1  True
     */
    private Integer clientId;

    /**
     * 车牌号码  True
     */
    private String plateNo;

    /**
     * 人员ID
     */
    private String personId;

    /**
     * 车牌类型
     */
    private String plateType;

    /**
     * 车牌颜色
     */
    private String plateColor;

    /**
     * 车辆类型
     */
    private String vehicleType;

    /**
     * 车辆颜色
     */
    private String vehicleColor;

    /**
     * 车辆描述
     */
    private String description;
}
