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
 * @Date: Created in 20:01 2022/3/12
 * @ClassName: ManufacturerUtil
 * @Description: 厂商请求工具类
 * @Version: 1.0
 */
@Slf4j
public class ManufacturerUtil {


    /**
     * 是否能进出
     * @param inOutType
     * @param personId
     * @return
     */
    public static String isItPossibleToGetInAndOut(String inOutType,String personId){
        HttpRequest request = HttpUtil.createGet(ERPConfigConstant.HOST + "/api/inOut/verification");
        request.form("inOutType",inOutType);
        request.form("idCard",personId);
        BaseHttpUtil.setTimeOut(request);
        HttpResponse execute = request.execute();
        JSONObject result = JSONObject.parseObject(execute.body());
        log.info("工单判断收到的信息为>>>>>>{}",execute.body());
         if(result.getIntValue("code") == 0){
             return null;
         }else{
             return result.getString("msg");
         }
    }

    /**
     * 根据车牌获取大小车
     * @param plateNum
     * @return
     */
    public static String getCarType(String plateNum){
        HttpRequest request = HttpUtil.createGet(ERPConfigConstant.HOST + "/api/inOut/getCarType");
        request.form("plateNo",plateNum);
        BaseHttpUtil.setTimeOut(request);
        HttpResponse execute = request.execute();
        JSONObject result = JSONObject.parseObject(execute.body());
        log.info("工单判断收到的信息为>>>>>>{}",execute.body());
        if(result.getIntValue("code") == 0){
            return null;
        }else{
            return result.getString("msg");
        }
    }

}
