package com.nbhy.modules.hik.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.sql.Timestamp;


@Data
public class CarEquipment {

    @ApiModelProperty("车道唯一标识")
    private String roadwayIndexCode;

    @ApiModelProperty("出入口唯一标识")
    private String entranceIndexCode;

    @ApiModelProperty("车道名称")
    private String roadwayName;

    @ApiModelProperty("车道类型")
    private String roadwayType;

    @ApiModelProperty(value = "创建时间", hidden = true)
    private Timestamp createTime;

    @ApiModelProperty(value = "是否推送,默认是未推送", hidden = true)
    private Boolean push = false;
}
