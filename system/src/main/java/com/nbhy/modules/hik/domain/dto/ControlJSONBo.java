package com.nbhy.modules.hik.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @Author: xcjx
 * @Email: nizhaobudaowo@163.com
 * @Company: nbhy
 * @Date: Created in 15:15 2022/3/12
 * @ClassName: EquipmentDTO
 * @Description: 设备的验证方式实体类
 * @Version: 1.0
 */
@Data
public class ControlJSONBo {

    @ApiModelProperty(value = "人道:人脸(face),定位卡(locationCard)")
    private String humane;

    @ApiModelProperty(value = "- 车道:人脸(face),车牌(plateNo),车卡(carCard)")
    private String lane;

    @ApiModelProperty(value = "- 危车:人脸(face),车牌(plateNo)")
    private String danger;
}
