package com.nbhy.modules.hik.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class HikUser {

    //唯一标志，为空时平台自动生成唯一标志
    private String personId;

    //人员名称，1~32个字符；不能包含 ' / \ : * ? " < > | 这些特殊字符
    private String personName;
    //性别，1：男；2：女；0：未知
    private String gender;

    //所属组织标识，必须是已存在组织
    private String orgIndexCode;

    //出生日期，举例：1992-09-12
    private String birthday;

    //手机号，1-20位数字,平台上人员信息实名标识选择为手机号码时必填
    private String phoneNo;

    //邮箱，举例：hic@163.com
    private String email;

    //证件类型，111:身份证,414:护照,113:户口簿,335:驾驶证,
    // 131:工作证,133:学生证,990:其他，
    // 平台上人员信息实名标识选择为身份证件时必填
    private String certificateType = "111";

    //证件号码，1-20位数字字母，平台上人员信息实名标识选择为身份证件时必填
    private String certificateNo;

    //工号
    private String jobNo;
    //人脸信息
    private List<String> faces;
}
