package com.nbhy.modules.hik.domain.dto;

import lombok.Data;

@Data
public class HikEquipmentChannel {
    //门禁点唯一标识
    private String doorIndexCode;
    //门禁点名称
    private String doorName;

    //门禁点序号
    private String doorNo;

    //所属门禁设备唯一标识
    private String acsDevIndexCode;

    //所属区域唯一标识
    private String regionIndexCode;

    //通道类型
    private String channelType;

    //通道号
    private String channelNo;
    //安装位置
    private String installLocation;

    //备注
    private String remark;
}
