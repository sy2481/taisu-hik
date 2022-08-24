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
public class PersonUpdateVO {

    @NotBlank
    @ApiModelProperty(value = "海康人员唯一标识，必传",required = true)
    private String personId;

    @NotBlank
    @ApiModelProperty(value = "人员名称，1~32个字符；不能包含 ' / \\ : * ? \" < > | 这些特殊字符",required = true)
    private String personName;

    @ApiModelProperty(value = "手机号，1-20位数字,不必填写")
    private String phoneNo;

    @ApiModelProperty(value = "工号")
    private String jobNo;

}
