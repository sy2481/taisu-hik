package com.nbhy.modules.erp.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.nbhy.modules.erp.config.ERPConfigConstant;
import com.nbhy.modules.hik.domain.vo.CarPerilousVO;
import com.nbhy.utils.BaseHttpUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: xcjx
 * @Email: nizhaobudaowo@163.com
 * @Company: nbhy
 * @Date: Created in 20:01 2022/3/12
 * @ClassName: ManufacturerUtil
 * @Description: 危化品
 * @Version: 1.0
 */
@Slf4j
public class HazardousChemicalsUtil {


    /**
     * 是否能进出
     * @param carPerilousVO 危化门禁
     * @return
     */
//    public static boolean carExists(String carNumber){
////        HttpRequest request = HttpUtil.createGet(ERPConfigConstant.HOST + "/api/inOut/checkDangerPlate");
//        HttpRequest request = HttpUtil.createGet("124.222.171.172:8080" + "/api/inOut/checkDangerPlate");
//        request.form("plateNo",carNumber);
//        BaseHttpUtil.setTimeOut(request);
//        HttpResponse execute = request.execute();
//        JSONObject result = JSONObject.parseObject(execute.body());
//        log.info("工单判断收到的信息为>>>>>>{}",execute.body());
//         if(result.getIntValue("code") == 0){
//             return true;
//         }else{
//             return false;
//         }
//    }
    public static String carExists(CarPerilousVO carPerilousVO){
//        HttpRequest request = HttpUtil.createGet(ERPConfigConstant.HOST + "/api/inOut/checkDangerPlate");
//        HttpRequest request = HttpUtil.createGet("124.222.171.172:8080" + "/api/perilous/checkingPerilous");
        HttpRequest request = HttpUtil.createPost(ERPConfigConstant.HOST + "/api/perilous/checkingPerilous");
//        request.form("carNo",carPerilousVO.getCarNo());
//        request.form("checkingType",carPerilousVO.getCheckingType());
//        request.form("idCard",carPerilousVO.getIdCard());
//        request.form("inOutType",carPerilousVO.getInOutType());
//        request.form("ip",carPerilousVO.getIp());
        JSONObject json = new JSONObject();
        json.put("carNo", carPerilousVO.getCarNo());
        json.put("checkingType", carPerilousVO.getCheckingType());
        json.put("idCard", carPerilousVO.getIdCard());
        json.put("inOutType", carPerilousVO.getInOutType());
        json.put("ip", carPerilousVO.getIp());
        request.body(json.toJSONString());
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
