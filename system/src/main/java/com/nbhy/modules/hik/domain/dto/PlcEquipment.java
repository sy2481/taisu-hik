//package com.nbhy.modules.hik.domain.dto;
//
//import com.baomidou.mybatisplus.annotation.TableField;
//import io.swagger.annotations.ApiModelProperty;
//import lombok.Data;
//
//import java.util.List;
//
///**
// * @Author: xcjx
// * @Email: nizhaobudaowo@163.com
// * @Company: nbhy
// * @Date: Created in 15:03 2022/3/12
// * @ClassName: PlcEquipment
// * @Description: pcl设备
// * @Version: 1.0
// */
//@Data
//public class PlcEquipment {
//
//    @ApiModelProperty(value = "设备名称")
//    private String name;
//
//    @ApiModelProperty(value = "设备ip")
//    private String ip;
//
//    @ApiModelProperty(value = "设备端口")
//    private Integer port;
//
//    @ApiModelProperty(value = "海康设备")
//    private List<HikEquipmentDTO> hikEquipmentDTOS;
//
//}
//
//@Data
//class HikEquipmentDTO{
//
//    @ApiModelProperty("设备名称")
//    private String name;
//
//    @ApiModelProperty(value = "1-进 2-出")
//    private Integer sign;
//
//    @ApiModelProperty(value = "设备号")
//    private String indexCode;
//
//
//    @ApiModelProperty(value = "当类型为0的时候代表人脸设备，" +
//            "1的时候代表车辆设备，默认为0 ")
//    private Integer deviceType;
//
//
//    @ApiModelProperty(value = "字幕机Ip")
//    @TableField("subtitle_machine_ip")
//    private String subtitleMachineIp;
//
//    @ApiModelProperty(value = "0-普通设备、1-绑定定位卡设备、2-车道绑定的人脸设备")
//    private String deviceAttribute;
//
//
//    @ApiModelProperty(value = "车道和人道绑定的设备例如： \n" +
//            "1、如果本记录是车道设备，那个这个字段为绑定的人脸设备号\n" +
//            "2、如果本记录是被车道设备绑定的人脸设备，那这个字段为绑定的车道设备号")
//    private String bindIndexCode;
//
//
//    @ApiModelProperty(value = "plc指令")
//    private String plcCommand;
//
//
//}