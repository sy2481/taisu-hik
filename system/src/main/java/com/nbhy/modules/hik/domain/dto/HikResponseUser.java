package com.nbhy.modules.hik.domain.dto;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 查询海康返回返回信息
 */
@Data
public class HikResponseUser {

    private String personId;

    private String personName;

    //性别， 未知 0 男性 1 女性 2
    private Integer gender;

    //所属组织路径
    private String orgPath;

    private String orgPathName;

    //所属组织唯一标识码
    private String orgIndexCode;

    private Timestamp createTime;

    //所属组织名称
    private String orgName;

    //证件类型
    private String certificateType;

    //证件号码
    private String certificateNo;

    //更新时间
    private String updateTime;

    //出生日期
    private String birthday;

    //联系电话
    private String phoneNo;

    private String address;

//    //人员图片信息
//    private PersonPhoto personPhoto;


    private String email;

    //学历
    private String education;

    //民族
    private String nation;
    //工号
    private String jobNo;

    @Data
    class PersonPhoto{
        //图片相对url
        private String picUri;
        //图片服务器唯一标示
        private String serverIndexCode;

    }

}



