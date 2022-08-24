package com.nbhy.modules.hik.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;

/**
 * @Author: xcjx
 * @Email: nizhaobudaowo@163.com
 * @Company: nbhy
 * @Date: Created in 15:31 2022/3/11
 * @ClassName: Car
 * @Description: 车辆管理
 * @Version: 1.0
 */
@Getter
@Setter
@TableName("hik_car")
public class Car {

    @NotBlank
    @TableId(value = "car_number",type = IdType.INPUT)
    @ApiModelProperty("车牌号")
    private String carNumber;

    @TableField("car_sn")
    @ApiModelProperty("一般情况下传输人员唯一标识，当车辆类型为2的时候传输工单号")
    private String carSn;

    @TableField("car_type")
    @ApiModelProperty("车辆类型 0代表内部员工车辆，1代表厂商员工车辆")
    private Integer carType;

    @ApiModelProperty(value = "创建时间", hidden = true)
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private Timestamp createTime;
}
