package com.nbhy.modules.hik.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.nbhy.exception.BadRequestException;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

/**
 * @Author: yyf
 * @Date: Created in 10:36 2022/1/21
 * @ClassName: HikCallBackUtil
 * @Description:
 */
@Log4j2
public class HikCallBackUtil {
    public static void eventCallback(String backUrl, Object[] eventTypes){
        Map<String, String> path = HiKBaseUtil.getPath("/api/eventService/v1/eventSubscriptionByEventTypes");
        String contentType = "application/json";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("eventDest", backUrl);
        jsonObject.put("eventTypes", eventTypes);
        log.info("参数》》》》》{}",jsonObject.toString());
        String result = ArtemisHttpUtil.doPostStringArtemis(path, jsonObject.toString(), null, null, contentType , null);// post请求application/json类型参数
        log.info("设置回调返回结果》》》》》{}",result);
    }

    public static JSONObject getAccessEvent(JSONObject jsonObject){
        Map<String, String> path = HiKBaseUtil.getPath("/api/acs/v2/door/events");
        String contentType = "application/json";
        log.info("参数》》》》》{}",jsonObject.toString());
        String result = ArtemisHttpUtil.doPostStringArtemis(path, jsonObject.toString(), null, null, contentType , null);// post请求application/json类型参数
        JSONObject object = JSON.parseObject(result);
        if (!"0".equals(object.get("code").toString())){
            throw new BadRequestException("获取数据失败");
        }
        log.info("同步结果》》》》》{}",result);
        return object.getJSONObject("data");
    }


    public static String getEventView(){
        Map<String, String> path = HiKBaseUtil.getPath("/api/eventService/v1/eventSubscriptionView");
        String contentType = "application/json";
        String result = ArtemisHttpUtil.doPostStringArtemis(path, null, null, null, contentType , null);// post请求application/json类型参数
        log.info("设置回调返回结果》》》》》{}",result);
        return result;
    }
}
