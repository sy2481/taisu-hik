package com.nbhy.modules.erp.util;

import cn.hutool.core.bean.BeanUtil;
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
 * @Description: 设备管理
 * @Version: 1.0
 */
@Slf4j
public class EquipmentUtil {

    /**
     * 是否能进出
     * @return
     */
    public  static  String  queryEquipment(){
        HttpRequest request = HttpUtil.createGet(ERPConfigConstant.HOST + "/api/equipment/getAll");
        BaseHttpUtil.setTimeOut(request);
        HttpResponse execute = request.execute();
        log.info("获取到的设备信息为>>>>>>>>{}",execute.body());
        return execute.body();
    }

    //type0-上綫，1-下綫
    public  static  boolean  getEquipmentLog(String ip ,String type){
        HttpRequest request = HttpUtil.createGet(ERPConfigConstant.HOST + "/api/equipmentLog/info");
        request.form("ip",ip);
        request.form("type",type);
        BaseHttpUtil.setTimeOut(request);
        HttpResponse execute = request.execute();
        JSONObject result = JSONObject.parseObject(execute.body());
        log.info("保存設備的上下綫記錄>>>>>>>>{}",execute.body());
        return result.getIntValue("code") == 0;
    }
}
