package com.nbhy.modules.hik.domain.callback;

import lombok.Data;

@Data
public class VisitEveData {
    //访客id
    private String visitorId;

    //姓名
    private String personName;

    //性别
    private int sex;


    private String nationality;

    //证件类型
    private int idType;

    //证件号码
    private String idNo;

    //被访问人姓名
    private String beVisitedPersonName;

    //被访问人所属组织
    private String beVisitedPersonOrg;

    //来访单位
    private String visitorWorkUint;

    //访客验证码
    private String visitorCode;

    //来访事由
    private String purpose;

    //证件签发机关
    private String signOrg;

    //来访时间
    private String startTime;

    //离开时间
    private String endTime;

    //手机号码
    private String phone;

    //车牌号
    private String carNo;

    //照片uri
    private String photoUrl;

    //抓拍图片uri
    private String captureUrl;

    //图片存储服务器唯一标识
    private String svrIndexCode;

    //被访问人Id
    private String beVisitedPersonId;

    //被访问人所属组织Id
    private String beVisitedPersonOrgId;
}
