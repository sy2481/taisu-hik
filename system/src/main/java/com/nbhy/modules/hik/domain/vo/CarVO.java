package com.nbhy.modules.hik.domain.vo;

import com.nbhy.modules.hik.domain.entity.Card;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
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
public class CarVO {

    @NotBlank
    @ApiModelProperty(value = "车牌号",required = true)
    private String carNumber;

    @NotNull
    @ApiModelProperty(value = "车辆类型 0代表内部员工车辆，1代表厂商员工车辆",required = true)
    private Integer carType;


    @NotBlank
    @ApiModelProperty(value = "当为内部车辆的时候传输人员唯一标识， 当为厂商车辆的时候传输工单号",required = true)
    private String carSn;

    @ApiModelProperty(value = "车辆权限")
    private Set<String> auths;


    @ApiModelProperty(value = "是否下发海康所有设备权限")
    private Boolean authIsAll;


}
