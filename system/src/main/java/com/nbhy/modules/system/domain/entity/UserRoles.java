package com.nbhy.modules.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@TableName("sys_users_roles")
@Data
@ApiModel(value="角色菜单对应表",description="角色菜单对应表")
public class UserRoles {

    @TableField("role_id")
    private Long roleId;

    @TableField("user_id")
    private Long userId;
}
