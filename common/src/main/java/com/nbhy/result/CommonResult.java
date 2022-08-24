package com.nbhy.result;

/**
 * 通用返回对象
 * Created by macro on 2019/4/19.
 */
public class CommonResult<T> {
    private int code;
    private String message;
    private T data;

    protected CommonResult() {
    }

    protected CommonResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }


    public static <T> CommonResult<T> commonResult(T data, int code, String message) {
        return new CommonResult<T>(code, message, data);
    }
    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<T>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }


    /**
     * 成功返回结果
     *
     */
    public static <T> CommonResult<T> success() {
        return new CommonResult<T>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }



    /**
     * 成功返回结果
     *
     */
    public static <T> CommonResult<T> updateSuccess() {
        return new CommonResult<T>(ResultCode.UPDATE_SUCCESS.getCode(), ResultCode.UPDATE_SUCCESS.getMessage(), null);
    }



    /**
     * 成功返回结果
     *
     */
    public static <T> CommonResult<T> updateSuccess(String entity) {
        return new CommonResult<T>(ResultCode.UPDATE_SUCCESS.getCode(), entity + ResultCode.UPDATE_SUCCESS.getMessage(), null);
    }

    /**
     * 成功返回结果
     *
     */
    public static <T> CommonResult<T> updateSuccess(T data,String entity) {
        return new CommonResult<T>(ResultCode.UPDATE_SUCCESS.getCode(), entity + ResultCode.UPDATE_SUCCESS.getMessage(), data);
    }
    /**
     * 成功返回结果
     *
     */
    public static <T> CommonResult<T> querySuccess() {
        return new CommonResult<T>(ResultCode.QUERY_SUCCESS.getCode(), ResultCode.QUERY_SUCCESS.getMessage(), null);
    }

    /**
     * 成功返回结果
     *
     */
    public static <T> CommonResult<T> querySuccess(String entity) {
        return new CommonResult<T>(ResultCode.QUERY_SUCCESS.getCode(), entity + ResultCode.QUERY_SUCCESS.getMessage(), null);
    }

    /**
     * 成功返回结果
     *
     */
    public static <T> CommonResult<T> querySuccess(T data,String entity) {
        return new CommonResult<T>(ResultCode.QUERY_SUCCESS.getCode(), entity + ResultCode.QUERY_SUCCESS.getMessage(), data);
    }



    /**
     * 成功返回结果
     *
     */
    public static <T> CommonResult<T> deleteSuccess() {
        return new CommonResult<T>(ResultCode.DELETE_SUCCESS.getCode(), ResultCode.DELETE_SUCCESS.getMessage(), null);
    }

    /**
     * 成功返回结果
     *
     */
    public static <T> CommonResult<T> deleteSuccess(String entity) {
        return new CommonResult<T>(ResultCode.DELETE_SUCCESS.getCode(), entity + ResultCode.DELETE_SUCCESS.getMessage(), null);
    }

    /**
     * 成功返回结果
     *
     */
    public static <T> CommonResult<T> deleteSuccess(T data,String entity) {
        return new CommonResult<T>(ResultCode.DELETE_SUCCESS.getCode(), entity + ResultCode.DELETE_SUCCESS.getMessage(), data);
    }


    /**
     * 成功返回结果
     *
     */
    public static <T> CommonResult<T> insertSuccess() {
        return new CommonResult<T>(ResultCode.INSERT_SUCCESS.getCode(), ResultCode.INSERT_SUCCESS.getMessage(), null);
    }



    /**
     * 成功返回结果
     *
     */
    public static <T> CommonResult<T> insertSuccess(String entity) {
        return new CommonResult<T>(ResultCode.INSERT_SUCCESS.getCode(), entity + ResultCode.INSERT_SUCCESS.getMessage(), null);
    }


    /**
     * 成功返回结果
     *
     */
    public static <T> CommonResult<T> insertSuccess(T data,String entity) {
        return new CommonResult<T>(ResultCode.INSERT_SUCCESS.getCode(), entity + ResultCode.INSERT_SUCCESS.getMessage(), data);
    }


    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     * @param  message 提示信息
     */
    public static <T> CommonResult<T> success(T data, String message) {
        return new CommonResult<T>(ResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败返回结果
     * @param errorCode 错误码
     */
    public static <T> CommonResult<T> failed(IErrorCode errorCode) {
        return new CommonResult<T>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    /**
     * 失败返回结果
     * @param errorCode 错误码
     * @param message 错误信息
     */
    public static <T> CommonResult<T> failed(IErrorCode errorCode, String message) {
        return new CommonResult<T>(errorCode.getCode(), message, null);
    }

    /**
     * 失败返回结果
     * @param message 提示信息
     */
    public static <T> CommonResult<T> failed(String message) {
        return new CommonResult<T>(ResultCode.FAILED.getCode(), message, null);
    }

    /**
     * 失败返回结果
     */
    public static <T> CommonResult<T> failed() {
        return failed(ResultCode.FAILED);
    }

    /**
     * 参数验证失败返回结果
     */
    public static <T> CommonResult<T> validateFailed() {
        return failed(ResultCode.VALIDATE_FAILED);
    }

    /**
     * 参数验证失败返回结果
     * @param message 提示信息
     */
    public static <T> CommonResult<T> validateFailed(String message) {
        return new CommonResult<T>(ResultCode.VALIDATE_FAILED.getCode(), message, null);
    }

    /**
     * 未登录返回结果
     */
    public static <T> CommonResult<T> unauthorized(T data) {
        return new CommonResult<T>(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage(), data);
    }

    /**
     * 未授权返回结果
     */
    public static <T> CommonResult<T> forbidden(T data) {
        return new CommonResult<T>(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMessage(), data);
    }

    public long getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
