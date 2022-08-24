package com.nbhy.modules.hik.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
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
public class CarCardUntieVO {

    @NotBlank
    @ApiModelProperty("车卡卡号")
    private String cardNumber;

//    @ApiModelProperty("一般情况下传输人员唯一标识，当卡片类型为2的时候传输工单号")
//    private String cardNo;
//
//    @NotBlank
//    @ApiModelProperty("卡片类型:1代表内部车卡，2代表外部员工车卡")
//    private Integer cardType;

}
