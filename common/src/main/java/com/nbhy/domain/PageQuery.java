package com.nbhy.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class PageQuery {
    @ApiModelProperty("页码 (0..N)")
    private Long pageIndex = 1L;

    @ApiModelProperty("每页显示的数目")
    private Long pageSize = 20L;
}