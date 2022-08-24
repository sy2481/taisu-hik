package com.nbhy.modules.hik.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Set;

/**
 * @Author: xcjx
 * @Email: nizhaobudaowo@163.com
 * @Company: nbhy
 * @Date: Created in 10:44 2022/3/11
 * @ClassName: PersonAuthVO
 * @Description: 下发人员权限
 * @Version: 1.0
 */
@Data
public class PersonAuthVO {
    @NotBlank
    @ApiModelProperty(value = "海康人员唯一标识，必传",required = true)
    private String personId;

    @ApiModelProperty(value = "下发海康设备权限")
    private Set<String> deviceNos;

    @ApiModelProperty(value = "是否下发海康所有设备权限")
    private Boolean authIsAll;


    private String jobNo;

    private Integer personType;

}
