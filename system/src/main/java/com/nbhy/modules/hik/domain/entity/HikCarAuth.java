package com.nbhy.modules.hik.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: xcjx
 * @Email: nizhaobudaowo@163.com
 * @Company: nbhy
 * @Date: Created in 15:44 2022/3/11
 * @ClassName: HikPersonAuth
 * @Description: 海康车辆权限
 * @Version: 1.0
 */
@Getter
@Setter
@TableName("hik_car_auth")
public class HikCarAuth {

    @TableField("car_id")
    @ApiModelProperty("车辆唯一标识,当为车牌的时候，传递的是车牌，当为车卡的时候，表示卡号")
    private String carId;

    @TableField("car_type")
    @ApiModelProperty("车辆类型 0代表车牌，1代表车卡")
    private Integer carType;


    @TableField("device_id")
    @ApiModelProperty("设备唯一标识")
    private String deviceId;
}
