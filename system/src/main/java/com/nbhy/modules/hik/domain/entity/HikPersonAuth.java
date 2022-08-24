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
 * @Description: 海康人员权限
 * @Version: 1.0
 */
@Getter
@Setter
@TableName("hik_person_auth")
public class HikPersonAuth {

    @TableField("person_id")
    @ApiModelProperty("人员唯一标识")
    private String personId;

    @TableField("device_id")
    @ApiModelProperty("设备唯一标识")
    private String deviceId;
}
