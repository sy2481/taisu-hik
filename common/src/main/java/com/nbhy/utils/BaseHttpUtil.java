package com.nbhy.utils;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.nbhy.result.CommonResult;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * 基础网络工具类
 */
@Slf4j
public class BaseHttpUtil {

    /**
     * 设计超时时间
     * @return
     */
    public static HttpRequest setTimeOut(HttpRequest httpRequest){
        return httpRequest.setReadTimeout(5000)
                .setConnectionTimeout(5000);
    }


    /**
     * 设计超时时间
     * @return
     */
    public static HttpRequest setTimeOutAndAuth(HttpRequest httpRequest,String headerName,String token){
        return httpRequest.setReadTimeout(5000)
                .setConnectionTimeout(5000)
                .header(headerName,token);
    }



    /**
     * 惠州新电子设置时间
     * @return
     */
    public static HttpRequest setTimeOutAndHeader(HttpRequest httpRequest, Map<String,String> headerMap){
        return httpRequest.setReadTimeout(5000)
                .setConnectionTimeout(5000)
                .headerMap(headerMap,true);
    }


    /**
     * 发送返回信息
     * @param response
     */
    public static void sendMsg(HttpServletResponse response,int code,String message){
        CommonResult commonResult = CommonResult.commonResult(null, code, message);
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-type", "text/html; charset=utf-8");
        try (PrintWriter writer = response.getWriter()){
            writer.write(JSON.toJSONString(commonResult));
        }catch (IOException e){
            log.error("发送返回消息失败>>>>>>>>>>>>>",e);
        }

    }
}
