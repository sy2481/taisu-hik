package com.nbhy.modules.system.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.nbhy.annotation.validation.Update;
import com.nbhy.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@TableName("sys_user")
public class User extends BaseEntity implements Serializable {

    @NotNull(groups = Update.class)
    @ApiModelProperty(value = "ID", hidden = true)
    @TableId(value = "user_id",type = IdType.AUTO)
    private Long id;

    @NotBlank
    @ApiModelProperty(value = "用户名称")
    @TableField("username")
    private String username;

    @NotBlank
    @ApiModelProperty(value = "用户昵称")
    @TableField("nick_name")
    private String nickName;

    @Email
    @NotBlank
    @ApiModelProperty(value = "邮箱")
    @TableField("email")
    private String email;

    @NotBlank
    @ApiModelProperty(value = "电话号码")
    @TableField("phone")
    private String phone;

    @ApiModelProperty(value = "用户性别")
    @TableField("gender")
    private String gender;

    @ApiModelProperty(value = "密码")
    @TableField("password")
    private String password;

    @NotNull
    @ApiModelProperty(value = "是否启用")
    @TableField("enabled")
    private Boolean enabled;

    @ApiModelProperty(value = "是否为admin账号", hidden = true)
    @TableField("is_admin")
    private Boolean isAdmin = false;

    @ApiModelProperty(value = "最后修改密码的时间", hidden = true)
    @TableField("pwd_reset_time")
    private Timestamp pwdResetTime;

    @ApiModelProperty(value = "创建时间", hidden = true)
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private Timestamp createTime;

    @ApiModelProperty("用户拥有的角色")
    @TableField(exist = false)
    private Set<Role> roles;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
}