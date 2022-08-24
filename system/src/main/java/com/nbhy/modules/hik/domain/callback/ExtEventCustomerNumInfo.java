package com.nbhy.modules.hik.domain.callback;

import lombok.Data;

@Data
public class ExtEventCustomerNumInfo {
    //通道号
    private Integer AccessChannel;

    //进人数
    private Integer EntryTimes;

    //出人数
    private Integer ExitTimes;

    //总通行人数
    private Integer TotalTimes	;
}
