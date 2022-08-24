package com.nbhy.result;

/**
 * 枚举了一些常用API操作码
 * Created by macro on 2019/4/19.
 */
public enum
ResultCode implements IErrorCode {
    SUCCESS(200, "操作成功"),
    INSERT_SUCCESS(200, "新增成功"),
    UPDATE_SUCCESS(200, "修改成功"),
    DELETE_SUCCESS(200, "删除成功"),
    QUERY_SUCCESS(200, "查询成功"),


    FAILED(500, "操作失败"),
    VALIDATE_FAILED(400, "参数检验失败"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限"),
    NO_FIND(404, "没有查找到对应的资源");

    private int code;
    private String message;

    private ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
