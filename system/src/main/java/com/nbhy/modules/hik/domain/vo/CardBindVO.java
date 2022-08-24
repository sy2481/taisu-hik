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
 * @Date: Created in 10:39 2022/3/11
 * @ClassName: CardBindVO
 * @Description: 海康绑定
 * @Version: 1.0
 */
@Data
public class CardBindVO {

    @NotBlank
    @ApiModelProperty("人员卡号")
    private String cardNumber;

    @NotNull
    @ApiModelProperty("海康人员ID")
    private String personId;

//    @ApiModelProperty("一般情况下传输人员唯一标识，当卡片类型为2的时候传输工单号")
//    private String cardNo;
//
//    @NotBlank
//    @ApiModelProperty("卡片类型 0-定位卡，1代表内部车卡，2代表外部员工车卡")
//    private Integer cardType;
//
//    @ApiModelProperty(value = "下发海康设备权限")
//    private Set<String> deviceNos;
//
//    @ApiModelProperty(value = "是否下发海康所有设备权限")
//    private Boolean authIsAll;


}
