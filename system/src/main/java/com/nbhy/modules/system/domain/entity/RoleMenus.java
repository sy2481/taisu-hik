package com.nbhy.modules.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@TableName("sys_roles_menus")
@Data
@ApiModel(value="角色菜单对应表",description="角色菜单对应表")
public class RoleMenus {

    @TableField("role_id")
    private Long roleId;

    @TableField("menu_id")
    private Long menuId;
}
