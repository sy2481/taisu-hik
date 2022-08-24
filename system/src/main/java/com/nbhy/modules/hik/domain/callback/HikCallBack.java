package com.nbhy.modules.hik.domain.callback;

import lombok.Data;

@Data
public class HikCallBack<T> {
    //通知方法名称
    private String method;

    //通知方法参数
    private Params<T> params;
}
