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
public class PersonVO {

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

    @NotBlank
    @ApiModelProperty(value = "人脸base64字符串",required = true)
    private String faceBase64Str;

    @NotNull
    @ApiModelProperty(value = "人员类型 0-内部员工、1-厂商员工")
    private Integer personType;


    @ApiModelProperty(value = "厂商员工必填，厂商员工工单号")
    private String orderSn;

    @ApiModelProperty(value = "下发海康设备权限")
    private List<String> deviceNos;

    @ApiModelProperty(value = "是否下发海康所有设备权限")
    private Boolean authIsAll;

}
