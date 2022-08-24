package com.nbhy.modules.hik.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: xcjx
 * @Email: nizhaobudaowo@163.com
 * @Company: nbhy
 * @Date: Created in 15:40 2022/3/11
 * @ClassName: HikDevice
 * @Description: 海康设备
 * @Version: 1.0
 */
@Getter
@Setter
@TableName("hik_device")
public class HikDeviceDTO {

    @TableId(value = "index_code",type = IdType.INPUT)
    @ApiModelProperty("海康设备唯一编码")
    private String indexCode;

    @ApiModelProperty("设备名称")
    private String deviceName;

    @ApiModelProperty("资源类型 0代表人脸设备，1代表车辆设备")
    private Integer deviceType;

    @ApiModelProperty("设备ip地址")
    private String ip;

    @ApiModelProperty("设备端口")
    private String port;

}
