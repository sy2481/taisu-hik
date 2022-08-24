package com.nbhy.modules.hik.domain.callback;

import lombok.Data;

@Data
 public class ExtEventIdentityCardInfo{
    //身份证id
    private String IdNum;

    //姓名
    private String Name;

    //住址
    private String Address;

    //出生日期
    private String Birth;

    //有效日期结束时间
    private String EndDate;

    //签发机关
    private String IssuingAuthority;

    //
    private Integer Nation;

    //性别
    private String Sex;

    //有效日期开始时间
    private String StartDate;

    //是否长期有效
    private Integer TermOfValidity;
}