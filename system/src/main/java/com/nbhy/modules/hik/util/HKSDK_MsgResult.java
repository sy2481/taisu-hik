package com.nbhy.modules.hik.util;

/**
 * 返回消息业务模型
 */
public class HKSDK_MsgResult<T> {
    public HKSDK_MsgResult() {

    }

    public HKSDK_MsgResult(boolean isSuccess,
                           String msg) {
        isSuccess = isSuccess;
        msg = msg;
    }

    /**
     * 是否成功
     */
    public boolean isSuccess = false;
    /**
     * 返回信息(文本)
     */
    public String msg = "";

    /**
     * 数据列表
     */
    public T data;

}
