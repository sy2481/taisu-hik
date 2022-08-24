package com.nbhy.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageBean<T> {

    @ApiModelProperty(value = "返回值")
    private List<T> list;

    @ApiModelProperty(value = "页标")
    private Long pageIndex;

    @ApiModelProperty(value = "数据总量")
    private Long total;

    @ApiModelProperty(value = "每页数据量")
    private Long pageSize;
}
