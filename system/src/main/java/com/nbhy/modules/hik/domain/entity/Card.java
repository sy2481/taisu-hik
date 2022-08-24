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
 * @Date: Created in 15:46 2022/3/10
 * @ClassName: HikPerson
 * @Description: 海康人员管理类
 * @Version: 1.0
 */
@Getter
@Setter
@TableName("hik_card")
public class Card {
    @NotBlank
    @TableId(value = "card_number",type = IdType.INPUT)
    @ApiModelProperty("人员卡号")
    private String cardNumber;

    @TableField("card_no")
    @ApiModelProperty("一般情况下传输人员唯一标识，当卡片类型为2的时候传输工单号")
    private String cardNo;

    @NotBlank
    @TableField("card_type")
    @ApiModelProperty("卡片类型 0-定位卡，1代表内部车卡，2代表外部员工车卡")
    private Integer cardType;

    @ApiModelProperty(value = "创建时间", hidden = true)
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private Timestamp createTime;

}
