package com.nbhy.modules.hik.domain.callback;

import lombok.Data;

/**
 * 车辆进出事件
 */
@Data
public class CarEveData {

    private Integer alarmCar;
    private String carAttributeName;
    private String cardNo;
    private Integer eventCmd;
    private String eventIndex;
    private String gateIndex;
    private String gateName;
    private InResult inResult;
    private Integer inoutType;
    private Integer mainLogo;
    private String parkIndex;
    private String parkName;
    private Integer plateBelieve;
    private Integer plateColor;
    //车牌
    private String plateNo;
    private Integer plateType;
    //车道号
    private String roadwayIndex;
    //车道名称
    private String roadwayName;
    private Integer roadwayType;
    private Integer subLogo;
    private Integer subModel;
    private String time;
    private Integer vehicleClass;
    private Integer vehicleColor;
    private Integer vehicleType;
}
