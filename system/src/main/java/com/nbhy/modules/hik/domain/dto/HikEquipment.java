package com.nbhy.modules.hik.domain.dto;

import lombok.Data;

@Data
public class HikEquipment {

    //门禁设备唯一标识
    private String acsDevIndexCode;

    //门禁设备名称
    private String acsDevName;

    //门禁设备类型描述
    private String acsDevTypeDesc;

    //门禁设备类型编号
    private String acsDevTypeCode;
    //门禁设备类型名称
    private String acsDevTypeName;

    //门禁设备IP
    private String acsDevIp;

    //门禁设备port
    private String acsDevPort;

    //门禁设备编号
    private String acsDevCode;

    //设备所属区域唯一标识
    private String regionIndexCode;

    //接入协议
    private String treatyType;

    //创建时间
    private String createTime;

    //更新时间
    private String updateTime;
}
