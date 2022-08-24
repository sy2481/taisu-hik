package com.nbhy.modules.hik.domain.callback;

import lombok.Data;

@Data
public class Events<T>{
    //事件详情
    private T data;
    //事件的唯一id
    private String eventId;
    //事件类型码
    private Integer eventType;
    //事件产生时间	采用ISO8601时间格式
    private String happenTime;
    //门禁点唯一接入编码
    private String srcIndex;
    //事件源名称
    private String srcName;

    //控制器设备唯一接入编码
    private String srcParentIndex;
    //资源类型
    private String srcType;

    //事件状态 0-瞬时
    //1-开始
    //2-停止
    //3-事件脉冲
    //4-事件联动结果更新
    //5-异步图片上传
    private Integer status;

    //脉冲超时时间
    private Integer timeout;

}