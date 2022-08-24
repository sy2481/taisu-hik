package com.nbhy.modules.email.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nbhy.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 邮件配置类，数据存覆盖式存入数据存
 * @date 2018-12-26
 */
@Data
@TableName("tool_email_config")
public class EmailConfig extends BaseEntity implements Serializable {

    @ApiModelProperty(value = "ID", hidden = true)
    @TableId(value = "user_id",type = IdType.AUTO)
    private Long id;

    @NotBlank
    @ApiModelProperty(value = "邮件服务器SMTP地址")
    @TableField("host")
    private String host;

    @NotBlank
    @ApiModelProperty(value = "邮件服务器 SMTP 端口")
    @TableField("port")
    private String port;

    @NotBlank
    @ApiModelProperty(value = "发件者用户名")
    @TableField("user")
    private String user;

    @NotBlank
    @ApiModelProperty(value = "密码")
    @TableField("pass")
    private String pass;

    @NotBlank
    @ApiModelProperty(value = "收件人")
    @TableField("fromu_ser")
    private String fromUser;
}
