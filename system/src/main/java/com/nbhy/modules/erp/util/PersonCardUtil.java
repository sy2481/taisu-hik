package com.nbhy.modules.erp.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.nbhy.modules.erp.config.ERPConfigConstant;
import com.nbhy.utils.BaseHttpUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: xcjx
 * @Email: nizhaobudaowo@163.com
 * @Company: nbhy
 * @Date: Created in 10:54 2022/3/13
 * @ClassName: PersonCardUtil
 * @Description: 人卡管理
 * @Version: 1.0
 */
@Slf4j
public class PersonCardUtil {
    /**
     * 是否能进出
     * @param idCardNo 身份证编码
     * @param locationCardNo 定位卡卡号
     * @return
     */
    public static boolean locationCardBind(String idCardNo,String locationCardNo){
        HttpRequest request = HttpUtil.createGet(ERPConfigConstant.HOST + "/api/location/bind");
        request.form("idCardNo",idCardNo);
        request.form("locationCardNo",locationCardNo);
        BaseHttpUtil.setTimeOut(request);
        HttpResponse execute = request.execute();
        JSONObject result = JSONObject.parseObject(execute.body());
        log.info("绑定定位卡获取到的信息>>>>>>>>{}",execute.body());
        return result.getIntValue("code") == 0;
    }


    /**
     * 是否能进出
     * @param locationCardNo 定位卡卡号
     * @return
     */
    public static boolean locationCardUnbind(String locationCardNo){
        HttpRequest request = HttpUtil.createGet(ERPConfigConstant.HOST + "/api/location/unbind");
        request.form("locationCardNo",locationCardNo);
        BaseHttpUtil.setTimeOut(request);
        HttpResponse execute = request.execute();
        JSONObject result = JSONObject.parseObject(execute.body());
        log.info("解绑定位卡获取到的信息>>>>>>>>{}",execute.body());
        return result.getIntValue("code") == 0;
    }

    /**
     * 来宾卡开门
     *
     * @return
     */
    public static boolean guestCardCheck(String guestCard, String ip, Integer inOutType) {
        HttpRequest request = HttpUtil.createPost(ERPConfigConstant.HOST + "/api/guestCard/guestCardCheck");
        JSONObject json = new JSONObject();
        json.put("inOutType", inOutType);
        json.put("guestCard", guestCard);
        json.put("ip", ip);
        request.body(json.toJSONString());
        BaseHttpUtil.setTimeOut(request);
        HttpResponse execute = request.execute();
        JSONObject result = JSONObject.parseObject(execute.body());
        log.info("来宾卡开门收到的信息为>>>>>>{}", execute.body());
        if (result.getIntValue("code") == 0) {
            return true;
        }
        return false;
    }
}
