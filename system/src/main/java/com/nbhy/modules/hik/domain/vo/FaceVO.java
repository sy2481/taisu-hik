package com.nbhy.modules.hik.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

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
public class FaceVO {

    @NotBlank
    @ApiModelProperty(value = "海康人员唯一标识，必传")
    private String personId;

    @NotBlank
    @ApiModelProperty(value = "人脸")
    private String face;
}
