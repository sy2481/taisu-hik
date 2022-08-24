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
 * @Description: 危化门禁
 * @Version: 1.0
 */
@Data
public class CarPerilousVO {

    @NotBlank
    @ApiModelProperty(value = "车牌号",required = true)
    private String carNo;

    @NotNull
    @ApiModelProperty(value = "门禁方式face,plateNo",required = true)
    private String checkingType;


    @NotBlank
    @ApiModelProperty(value = "身份证号",required = true)
    private String idCard;

    @ApiModelProperty(value = "进出状态（1-进、2-出）")
    private Integer inOutType;


    @ApiModelProperty(value = "设备IP")
    private String ip;


}
