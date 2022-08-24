package com.nbhy.modules.hik.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @Author: xcjx
 * @Email: nizhaobudaowo@163.com
 * @Company: nbhy
 * @Date: Created in 10:28 2022/3/11
 * @ClassName: PersonVO
 * @Description: 海康人员接收类
 * @Version: 1.0
 */
@Data
public class CarCardVO {

    @NotBlank
    @ApiModelProperty("车卡卡号")
    private String cardNumber;


    @ApiModelProperty("一般情况下传输人员唯一标识，当卡片类型为2的时候传输工单号")
    private String cardNo;

    @NotNull
    @ApiModelProperty("卡片类型:1代表内部车卡，2代表外部员工车卡")
    private Integer cardType;

    @ApiModelProperty(value = "下发车卡的权限")
    private Set<String> deviceNos;

    @ApiModelProperty(value = "是否下发海康所有车辆道闸设备权限")
    private Boolean authIsAll;



}
