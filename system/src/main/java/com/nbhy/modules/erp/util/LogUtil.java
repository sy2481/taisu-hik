package com.nbhy.modules.erp.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.nbhy.modules.erp.config.ERPConfigConstant;
import com.nbhy.modules.erp.domain.InAndOutLog;
import com.nbhy.utils.BaseHttpUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: xcjx
 * @Email: nizhaobudaowo@163.com
 * @Company: nbhy
 * @Date: Created in 20:01 2022/3/12
 * @ClassName: ManufacturerUtil
 * @Description: 日志工具类
 * @Version: 1.0
 */
@Slf4j
public class LogUtil {


    /**
     * 是否能进出
     * @param inAndOutLog 进出记录
     * @return
     */
    public static boolean sendLog(InAndOutLog inAndOutLog){
        HttpRequest request = HttpUtil.createGet(ERPConfigConstant.HOST + "/api/log/inOutLogInsert");
        request.form(BeanUtil.beanToMap(inAndOutLog,false,true));
        BaseHttpUtil.setTimeOut(request);
        HttpResponse execute = request.execute();
        JSONObject result = JSONObject.parseObject(execute.body());
        log.info("发送日志收到信息>>>>>>{}",execute.body());
        return result.getIntValue("code") == 0;
    }
}
