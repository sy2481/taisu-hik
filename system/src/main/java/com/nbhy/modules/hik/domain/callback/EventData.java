package com.nbhy.modules.hik.domain.callback;

import lombok.Data;

/**
 * 门禁事件回调
 */
@Data
public class EventData{
    //人员身份证信息
    private com.nbhy.modules.hik.domain.callback.ExtEventIdentityCardInfo ExtEventIdentityCardInfo;

    //通道事件信息
    private ExtEventCustomerNumInfo ExtEventCustomerNumInfo;

    //人员通道号
    private Integer ExtAccessChannel;

    //报警输入/防区通道
    private Integer ExtEventAlarmInID;

    //报警输出通道
    private Integer ExtEventAlarmOutID;


    //卡号
    private String ExtEventCardNo;


    //事件输入通道
    private Integer ExtEventCaseID;

    //事件类型代码
    private Integer ExtEventCode;


    //门编号
    private Integer ExtEventDoorID;


    //身份证图片URL
    private String ExtEventIDCardPictureURL;


    //进出方向	进出类型
    //1：进
    //0：出
    //-1:未知
    //要求：进门读卡器拨码设置为1，出门读卡器拨码设置为2
    private Integer ExtEventInOut;


    //就地控制器id	 就地控制器编号,0-门禁主机,1-255代表就地控制器
    private Integer ExtEventLocalControllerID;

    //主设备拨码
    private Integer ExtEventMainDevID;

    //人员编号
    private String ExtEventPersonNo;


    //图片的url
    private String ExtEventPictureURL;

    //读卡器id
    private Integer ExtEventReaderID;

    //读卡器类别
    private Integer ExtEventReaderKind;

    //报告上传通道
    private Integer ExtEventReportChannel;

    //群组编号
    private Integer ExtEventRoleID;

    //分控制器硬件ID
    private Integer ExtEventSubDevID;

    //刷卡次数
    private Integer ExtEventSwipNum;

    //多重认证序号
    private Integer ExtEventVerifyID;

    //白名单单号
    private Integer ExtEventWhiteListNo;

    //事件上报驱动的时间
    private String ExtReceiveTime;

    //事件流水号
    private Integer Seq;


    //事件类型	 事件类型，如普通门禁事件为0,身份证信息事件为1，客流量统计为2
    private Integer ExtEventType;

    //用户类型	人员类型：0 未知，1 普通，2 来宾，3 黑名单，4 管理员
    private Integer UserType;

    //图片服务器唯一编码
    private String svrIndexCode;

}