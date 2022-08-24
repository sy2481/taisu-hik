package com.nbhy.modules.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: xcjx
 * @Email: nizhaobudaowo@163.com
 * @Company: nbhy
 * @Date: Created in 15:22 2022/3/17
 * @ClassName: SysConfig
 * @Description: 系统配置
 * @Version: 1.0
 */
@TableName("sys_config")
@Data
public class SysConfig {

    private Long id;

    @ApiModelProperty("是否使用定位卡 true代表使用，false代表不使用")
    @TableField(value = "location_card_enabled")
    private Boolean locationCardEnabled;

}
