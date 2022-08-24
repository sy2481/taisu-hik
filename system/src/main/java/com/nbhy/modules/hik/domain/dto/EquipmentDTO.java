package com.nbhy.modules.hik.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: xcjx
 * @Email: nizhaobudaowo@163.com
 * @Company: nbhy
 * @Date: Created in 15:15 2022/3/12
 * @ClassName: EquipmentDTO
 * @Description: 设备实体类
 * @Version: 1.0
 */
@Data
public class EquipmentDTO {

    @ApiModelProperty("设备名称")
    private String name;

    @ApiModelProperty(value = "1-进 2-出")
    private Integer sign;

    @ApiModelProperty(value = "设备号")
    private String indexCode;


    @ApiModelProperty(value = "当类型为0的时候代表人脸设备，" +
            "1的时候代表车辆设备，默认为0 ")
    private Integer deviceType;


    @ApiModelProperty(value = "字幕机Ip")
    @TableField("subtitle_machine_ip")
    private String subtitleMachineIp;

    @ApiModelProperty(value = "0-普通设备、1-绑定定位卡设备、2-车道绑定的人脸设备")
    private Integer deviceAttribute;


    @ApiModelProperty(value = "车道和人道绑定的设备例如： \n" +
            "1、如果本记录是车道设备，那个这个字段为绑定的人脸设备号\n" +
            "2、如果本记录是被车道设备绑定的人脸设备，那这个字段为绑定的车道设备号")
    private String bindIndexCode;

    @ApiModelProperty(value = "plc指令")
    private String plcCommand;

    @ApiModelProperty(value = "plc设备名称")
    private String plcName;

    @ApiModelProperty(value = "plc设备ip")
    private String plcIp;

    @ApiModelProperty(value = "plc设备端口")
    private Integer plcPort;

    @ApiModelProperty(value = "设备ip")
    private String ip;

    @ApiModelProperty(value = "设备端口")
    private String port;

    @ApiModelProperty(value = "设备对应的校验方式")
    private ControlJSONBo controlJSONBo;
}
