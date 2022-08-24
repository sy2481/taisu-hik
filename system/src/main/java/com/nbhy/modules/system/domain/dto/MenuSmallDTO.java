package com.nbhy.modules.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MenuSmallDTO {
    @ApiModelProperty(value = "菜单ID")
    private Long id;

    public MenuSmallDTO(Long id, String name, Boolean parentNode, String englishName, Long pid) {
        this.id = id;
        this.name = name;
        this.parentNode = parentNode;
        this.englishName = englishName;
        this.pid = pid;
    }

    @ApiModelProperty(value = "菜单名称")
    private String name;

    @ApiModelProperty(value = "是否拥有子节点")
    private Boolean parentNode;

    @ApiModelProperty(value = "菜单英文名称")
    private String englishName;

    @ApiModelProperty(value = "菜单父节点")
    private Long pid;


    @ApiModelProperty(value = "子菜单")
    private List<MenuSmallDTO> children;

}
