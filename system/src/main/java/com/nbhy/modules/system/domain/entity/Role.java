package com.nbhy.modules.system.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.nbhy.annotation.validation.Update;
import com.nbhy.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@TableName("sys_role")
public class Role extends BaseEntity implements Serializable {

    @NotNull(groups = {Update.class})
    @ApiModelProperty(value = "ID", hidden = true)
    @TableId(value = "role_id",type = IdType.AUTO)
    private Long id;


    @TableField(exist = false)
//    @Valid
//    @NotNull
    @Size(min = 1,message = "菜单的长度最少不能少于一个")
    private Set<Menu> menus;

    @NotBlank(message = "名称不能为空")
    @TableField("name")
    @ApiModelProperty(value = "名称", hidden = true)
    private String name;


    @ApiModelProperty(value = "级别，数值越小，级别越大")
    @TableField("level")
    private Integer level = 3;

    @ApiModelProperty(value = "描述")
    @TableField("description")
    private String description;


    @TableField(value = "create_time",fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间", hidden = true)
    private Timestamp createTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Role role = (Role) o;
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
