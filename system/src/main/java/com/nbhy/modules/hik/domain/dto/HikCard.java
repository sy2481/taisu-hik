package com.nbhy.modules.hik.domain.dto;

import lombok.Data;

@Data
public class HikCard {
    //卡片ID
    private String cardId;
    //卡号
    private String cardNo;

    //持卡人员id
    private String personId;

    //持卡人名称
    private String personName;

    //使用状态标记
    private int useStatus;

    //生效日期
    private String startDate;

    //失效日期
    private String endDate;

    //挂失时间
    private String lossDate;

    //解除挂失时间
    private String unlossDate;
}
