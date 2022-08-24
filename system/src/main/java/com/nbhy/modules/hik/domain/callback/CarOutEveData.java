package com.nbhy.modules.hik.domain.callback;

import lombok.Data;


/**
 * @Author: xcjx
 * @Email: nizhaobudaowo@163.com
 * @Company: nbhy
 * @Date: Created in 14:31 2022/2/24
 * @ClassName: CarApproachEveData
 * @Description: 车辆出场事件
 * @Version: 1.0
 */
@Data
public class CarOutEveData {
    /**是否黑名单标识（1-黑名单 0-非黑名单）**/
    private Integer alarmCar;

    /**车辆属性名称	**/
    private String carAttributeName;

    /**卡号**/
    private String cardNo;

    /**事件号**/
    private Integer eventCmd;

    /**事件编号	**/
    private String eventIndex;

    /**出入口编号	**/
    private String gateIndex;

    /**出入口名称	**/
    private String gateName;

    /**放行结果数据	**/
    private InResult inResult;

    /**进出场类型，0：进场，1：出场	**/
    private Integer inoutType;

    /**车辆主品牌	**/
    private Integer mainLogo;

    /**停车库编号	**/
    private String parkIndex;

    /**停车库名称	**/
    private String parkName;

    /**车牌置信度	**/
    private Integer plateBelieve;

    /**车牌颜色	**/
    private Integer plateColor;

    /**车牌号	**/
    private String plateNo;

    /**车牌类型	**/
    private Integer plateType;

    /**车道编号		**/
    private String roadwayIndex;

    /**车道名称		**/
    private String roadwayName;

    /**车道类型	**/
    private Integer roadwayType;

    /**车辆子品牌	**/
    private Integer subLogo;

    /**子品牌年款	**/
    private Integer subModel;

    /**时间	**/
    private String time;

    /**车辆分类信息	**/
    private Integer vehicleClass;

    /**车辆颜色	**/
    private Integer vehicleColor;

    /**车辆类型	**/
    private Integer vehicleType;
}

@Data
class OutResult {
    /**账单编号	**/
    private String chargeIndex;

    /**出场实收金额		**/
    private Integer realCost;

    /**放行结果信息		**/
    private OutRlsResult rlsResult;

    /**收费员ID			**/
    private String tollerId;

    /**出场应收金额		**/
    private Integer totalCost;

}

@Data
class OutRlsResult {
    /**放行权限		**/
    private Integer releaseAuth;

    /**放行结果		**/
    private Integer releaseResult;
}

