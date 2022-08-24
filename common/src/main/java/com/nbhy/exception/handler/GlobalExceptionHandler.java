/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.nbhy.exception.handler;

import com.nbhy.exception.BadRequestException;
import com.nbhy.exception.EntityExistException;
import com.nbhy.exception.EntityNotFoundException;
import com.nbhy.result.CommonResult;
import com.nbhy.result.ResultCode;
import com.nbhy.utils.ThrowableUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * @author Zheng Jie
 * @date 2018-11-23
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理所有不可知的异常
     */
    @ExceptionHandler(Throwable.class)
    public CommonResult<ApiError> handleException(Throwable e){
        // 打印堆栈信息
        e.printStackTrace();
        log.error(ThrowableUtil.getStackTrace(e));
        return buildCommonResult(ApiError.error("请求异常，请与管理员联系"));
    }


    /**
     * BadCredentialsException
     */
    @ExceptionHandler(BadCredentialsException.class)
    public CommonResult<ApiError> badCredentialsException(BadCredentialsException e){
        // 打印堆栈信息
        String message = "坏的凭证".equals(e.getMessage()) ? "用户名或密码不正确" : e.getMessage();
        log.error(message);
        return buildCommonResult(ApiError.error(ResultCode.FAILED.getCode(),message));
    }

    /**
     * BadCredentialsException
     * 由于使用PreAuthorize注解发生异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public CommonResult<ApiError> badCredentialsException(AccessDeniedException e){
        // 打印堆栈信息
        String message = "坏的凭证".equals(e.getMessage()) ? "用户名或密码不正确" : e.getMessage();
        log.error(message);
        return buildCommonResult(ApiError.error(ResultCode.FORBIDDEN.getCode(),message));
    }

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(value = BadRequestException.class)
    public CommonResult badRequestException(BadRequestException e) {
        // 打印堆栈信息
        log.error(ThrowableUtil.getStackTrace(e));
        return buildCommonResult(ApiError.error(e.getStatus(),e.getMessage()));
    }

    /**
     * 处理 EntityExist
     */
    @ExceptionHandler(value = EntityExistException.class)
    public CommonResult<ApiError> entityExistException(EntityExistException e) {
        // 打印堆栈信息
        log.error(ThrowableUtil.getStackTrace(e));
        return buildCommonResult(ApiError.error(e.getMessage()));
    }

    /**
     * 处理 EntityNotFound
     */
    @ExceptionHandler(value = EntityNotFoundException.class)
    public CommonResult<ApiError> entityNotFoundException(EntityNotFoundException e) {
        // 打印堆栈信息
        log.error(ThrowableUtil.getStackTrace(e));
        return buildCommonResult(ApiError.error(NOT_FOUND.value(),e.getMessage()));
    }

    /**
     * 处理所有接口数据验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        // 打印堆栈信息
        log.error(ThrowableUtil.getStackTrace(e));
        String[] str = Objects.requireNonNull(e.getBindingResult().getAllErrors().get(0).getCodes())[1].split("\\.");
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        message = str[1] + ":" + message;
        return buildCommonResult(ApiError.error(400,message));
    }


    /**
     * 统一返回
     */
    private CommonResult buildCommonResult(ApiError apiError) {
        return CommonResult.commonResult(apiError.getTimestamp().toString(),apiError.getCode(),apiError.getMessage());
    }
}
