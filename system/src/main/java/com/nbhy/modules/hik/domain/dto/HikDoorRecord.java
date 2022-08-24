package com.nbhy.modules.hik.domain.dto;

import lombok.Data;

/**
 * 海康门禁记录
 */
@Data
public class HikDoorRecord {
    //卡号
    private String cardNo;

    //门禁点编码
    private String doorIndexCode;

    //门禁点名称
    private String doorName;

    //门禁点所在区域编码
    private String doorRegionIndexCode;

    //事件ID，唯一标识这个事件
    private String eventId;

    //事件名称
    private String eventName;

    //事件产生时间
    private String eventTime;

    //事件类型，参考附录D.1
    private long eventType;

    //身份证图片uri，它是一个相对地址，可以通过“获取门禁事件抓拍的图片”的接口，获取到图片的数据
    private String identityCardUri;

    //进出类型(1：进 0：出 -1:未知 要求：进门读卡器拨码设置为1，出门读卡器拨码设置为2 )
    private int inAndOutType;

    //组织编码
    private String orgIndexCode;

    //组织名称
    private String orgName;

//    //人员信息详情
//    private DoorRecordPersonDetail personDetail;

    //人员唯一编码
    private String personId;
    //人员名称
    private String personName;

    //抓拍图片地址
    private String picUri;

    //图片存储服务的唯一标识
    private String svrIndexCode;

}
