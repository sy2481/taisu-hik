package com.nbhy.modules.hik.domain.callback;

import lombok.Data;


/**
 * 海康门禁回调
 */
@Data
public class HikAccessControlCallback {
    //通知方法名称
    private String method;

    //通知方法参数
    private Params<EventData> params;

}






