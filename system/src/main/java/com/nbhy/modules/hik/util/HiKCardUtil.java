package com.nbhy.modules.hik.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.nbhy.modules.hik.domain.dto.HikCard;
import com.nbhy.modules.hik.domain.dto.QuerySingleCardDTO;
import com.nbhy.modules.hik.exception.HikException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class HiKCardUtil {

    /**
     * 查询单个卡片信息
     * @param query
     * @return
     */
    public static HikCard querySingleCard(QuerySingleCardDTO query){
        Map<String, String> path = HiKBaseUtil.getPath("/api/irds/v1/card/cardInfo");
        String contentType = "application/json";
        String body = JSONObject.toJSONString(query);
        log.info("查询单个卡片数据为>>>>>>>>>>>>>>>>>{}",body);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, contentType , null);// post请求application/json类型参数
        log.info("查询单个卡片数据为>>>>>>>>>>>>>>>>>{}",result);
        JSONObject json = JSON.parseObject(result);
        if("0".equals(json.getString("code"))){
            return json.getJSONObject("data").toJavaObject(HikCard.class);
        }else{
            throw new HikException("查询海康数据失败");
        }
    }

    /**
     * 批量开卡
     * @return
     */
    public static Boolean createCard(JSONObject body){
        //// TODO: 2021/12/16 111
        Map<String, String> path = HiKBaseUtil.getPath("/api/cis/v1/card/bindings");
        String contentType = "application/json";
        log.info("开卡入参数据为>>>>>>>>>>>>>>>>>{}",body);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body.toJSONString(), null, null, contentType , null);// post请求application/json类型参数
        log.info("开卡结果数据为>>>>>>>>>>>>>>>>>{}",result);
        JSONObject json = JSON.parseObject(result);
        if("0".equals(json.getString("code"))){
            return true;
        }
        throw new HikException("海康开卡失败");
    }

    /**
     * 批量退卡
     * @return
     */
    public static Boolean deleteCard(JSONObject body){
        Map<String, String> path = HiKBaseUtil.getPath("/api/cis/v1/card/deletion");
        String contentType = "application/json";
        log.info("退卡入参数据为>>>>>>>>>>>>>>>>>{}",body);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body.toJSONString(), null, null, contentType , null);// post请求application/json类型参数
        log.info("退卡结果数据为>>>>>>>>>>>>>>>>>{}",result);
        JSONObject json = JSON.parseObject(result);
        if("0".equals(json.getString("code"))){
            return true;
        }
        throw new HikException("海康退卡失败");
    }
}
