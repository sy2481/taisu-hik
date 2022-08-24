package com.nbhy.modules.system.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.nbhy.annotation.validation.Update;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

@Getter
@Setter
@TableName("sys_menu")
public class Menu  implements Serializable {

    @NotNull(groups = {Update.class})
    @ApiModelProperty(value = "ID", hidden = true)
    @TableId(value = "menu_id",type = IdType.AUTO)
    private Long id;

    @NotBlank
    @ApiModelProperty(value = "菜单名称")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "排序")
    @TableField("sort")
    private Integer sort = 999;

    @ApiModelProperty(value = "路由地址")
    @TableField("path")
    private String path;

    @ApiModelProperty(value = "菜单类型 ，0 菜单、1按钮")
    @TableField("type")
    private Integer type;

    @ApiModelProperty(value = "权限标识")
    @TableField("permission")
    private String permission;

    @ApiModelProperty(value = "菜单图标")
    @TableField("icon")
    private String icon;

    @ApiModelProperty(value = "是否拥有子节点")
    @TableField("parent_node")
    private Boolean parentNode;


    @ApiModelProperty(value = "英文名称")
    @TableField("english_name")
    private String englishName;

    @ApiModelProperty(hidden = true,value = "是否是基础菜单")
    @TableField("basics")
    private Boolean basics = false;

    @ApiModelProperty(value = "上级菜单,当pid等于0的时候。代表是顶级的菜单")
    @TableField("pid")
    private Long pid;

    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private Timestamp createTime;

    @ApiModelProperty(value = "菜单或者按钮的唯一标识")
    @TableField("mark")
    private String mark;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Menu menu = (Menu) o;
        return Objects.equals(id, menu.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
