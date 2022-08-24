package com.nbhy.modules.system.domain.dto;

import com.nbhy.base.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Set;

@Data
public class RoleDTO extends BaseDTO implements Serializable {

    @ApiModelProperty("角色ID")
    private Long id;

    @ApiModelProperty("角色包含的菜单")
    private Set<MenuSmallDTO> menus;

    @ApiModelProperty("角色名称")
    private String name;

    @ApiModelProperty("角色级别")
    private Integer level;

    @ApiModelProperty("角色详情")
    private String description;

    @ApiModelProperty("角色创建时间")
    private Timestamp createTime;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RoleDTO roleDto = (RoleDTO) o;
        return Objects.equals(id, roleDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
