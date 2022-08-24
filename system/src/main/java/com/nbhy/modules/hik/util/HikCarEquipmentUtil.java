
package com.nbhy.modules.hik.util;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.nbhy.modules.hik.domain.dto.*;
import com.nbhy.modules.hik.exception.HikException;
import com.nbhy.utils.StringUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 海康停车场工具类
 */
@Slf4j
@UtilityClass
public class HikCarEquipmentUtil {


    /**
     * 参考文档 https://open.hikvision.com/docs/docId?productId=5c67f1e2f05948198c909700&curNodeId=02ca0ec1d3fc48418395dc909f3628da#a598fe91
     * 使用默认配置进行下发文字
     * @param roadwaySyscode 车道设备编码
     * @param message 需要发送的消息
     */
    public void sendCarLedMsgDefaultConfig(String roadwaySyscode,String message){
        //roadwaySyscode = "71cfcb55b1dc48449f8016d6ffaa690b";
        Map<String, String> path = HiKBaseUtil.getPath("/api/pms/v1/device/led/control");
        String contentType = "application/json";
        JSONObject body = new JSONObject();
        body.put("roadwaySyscode",roadwaySyscode);
        String[] messageIndex = StrUtil.split(message, 4);
        List<JSONObject> dataList = new ArrayList<>();
        for (int i = 0; i < messageIndex.length; i++) {
            final int line = i+1;
            dataList.add(new JSONObject(){{
                put("line",line);
                put("fontConfig","[1,1,1]");
                put("showConfig",messageIndex[line-1]);
            }});
        }
        body.put("ledContent",dataList);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body.toJSONString(), null, null, contentType , null);// post请求application/json类型参数
        log.info("发送字幕机信息>>>>>>>>>>>>>>>>>{}",result);
    }

    public static void main(String[] args) {
        sendCarLedMsgDefaultConfig("","test");
    }


}

