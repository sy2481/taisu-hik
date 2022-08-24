
package com.nbhy.modules.hik.util;
import cn.hutool.core.collection.CollectionUtil;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 海康设备列表
 */
@Slf4j
@UtilityClass
public class HikEquipmentUtil {

    /**
     * 查询海康设备列表
     * @return
     */
    public static List<HikAcsDevEquipment> deviceResource(Integer index, Integer size, String resourceType) {
        Map<String, String> path = HiKBaseUtil.getPath("/api/irds/v2/deviceResource/resources");
        String contentType = "application/json";
        JSONObject body = new JSONObject();
        body.put("pageNo",index);
        body.put("pageSize",size);
        body.put("resourceType",resourceType);
        List<HikAcsDevEquipment> hikEquipments = new ArrayList<>();
        log.info("查询海康设备入参》》》》{}",body.toString());
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body.toJSONString(), null, null, contentType , null);// post请求application/json类型参数
        JSONObject json = JSON.parseObject(result);
        log.info("查询海康设备结果》》》》{}",result);
        if("0".equals(json.getString("code"))){
            hikEquipments.addAll(json.getJSONObject("data").getJSONArray("list").toJavaList(HikAcsDevEquipment.class));
            return hikEquipments;
        }else{ //查询失败
            log.error("查询设备失败得到的信息>>>>>>>{}",result);
            throw new HikException("查询设备失败");
        }


    }

    /**
     * 查询海康设备详情
     * @return
     */
    public static JSONArray search(String resourceType,List<String> resourceIndexCodes) {
        Map<String, String> path = HiKBaseUtil.getPath("/api/resource/v1/resource/indexCodes/search");
        String contentType = "application/json";
        JSONObject body = new JSONObject();
        body.put("resourceIndexCodes",resourceIndexCodes);
        body.put("resourceType",resourceType);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body.toJSONString(), null, null, contentType , null);// post请求application/json类型参数
        JSONObject json = JSON.parseObject(result);
        if("0".equals(json.getString("code"))){
            JSONArray jsonArray = json.getJSONObject("data").getJSONArray("list");
            return jsonArray;
        }else{ //查询失败
            log.error("查询设备失败得到的信息>>>>>>>{}",result);
            throw new HikException("查询设备失败");
        }


    }

    /**
     * 查询海康设备列表
     * @return
     */
    public static List<HikEquipment> queryAllV1(){
        Map<String, String> path = HiKBaseUtil.getPath("/api/resource/v1/acsDevice/acsDeviceList");
        String contentType = "application/json";
        JSONObject queryCondition = new JSONObject();
        int length = 1  ;
        int pageSize = 100;
        queryCondition.put("pageNo",1);
        queryCondition.put("pageSize",100);
        List<HikEquipment> hikEquipments = new ArrayList<>();
        for (int i = 1 ; i <=length ; i++){
            queryCondition.put("pageNo",i);
            String result = ArtemisHttpUtil.doPostStringArtemis(path, queryCondition.toJSONString(), null, null, contentType , null);// post请求application/json类型参数
//            log.info("获取海康上的所有设备,获取到的信息为>>>>>>>>>>>>>>>>>{}",result);
            JSONObject json = JSON.parseObject(result);
            if("0".equals(json.getString("code"))){
                 hikEquipments.addAll(json.getJSONObject("data").getJSONArray("list").toJavaList(HikEquipment.class));
            }else{ //查询失败
                log.error("查询设备失败得到的信息>>>>>>>{}",result);
                throw new HikException("查询设备失败");
            }
            //给length赋值
            if(length == 1){
                int total = json.getJSONObject("data").getIntValue("total");
                if(total == 0){
                    break;
                }
                if(total%pageSize == 0){
                    length = total/pageSize;
                }else {
                    length = total/pageSize + 1;
                }
            }
        }
       return hikEquipments;
    }



    /**
     * 查询海康设备列表
     * @return
     */
    public static List<HikEquipmentChannel> queryAllChannel(){
        Map<String, String> path = HiKBaseUtil.getPath("/api/resource/v1/acsDoor/acsDoorList");
        String contentType = "application/json";
        JSONObject queryCondition = new JSONObject();
        int length = 1;
        int pageSize = 100;
        queryCondition.put("pageNo",1);
        queryCondition.put("pageSize",100);
        List<HikEquipmentChannel> equipmentChannels = new ArrayList<>();
        for (int i = 1 ; i <=length ; i++){
            queryCondition.put("pageNo",i);
            String result = ArtemisHttpUtil.doPostStringArtemis(path, queryCondition.toJSONString(), null, null, contentType , null);// post请求application/json类型参数
//            log.info("获取海康上的所有设备,获取到的信息为>>>>>>>>>>>>>>>>>{}",result);
            JSONObject json = JSON.parseObject(result);
            if("0".equals(json.getString("code"))){
                equipmentChannels.addAll(json.getJSONObject("data").getJSONArray("list").toJavaList(HikEquipmentChannel.class));
            }else{ //查询失败
                log.error("查询设备失败得到的信息>>>>>>>{}",result);
                throw new HikException("查询设备失败");
            }
            //给length赋值
            if(length == 1){
                int total = json.getJSONObject("data").getIntValue("total");
                if(total == 0){
                    break;
                }
                if(total%pageSize == 0){
                    length = total/pageSize;
                }else {
                    length = total/pageSize + 1;
                }
            }
        }
        return equipmentChannels;
    }



    /**
     * 查询海康设备列表
     * @return
     */
    public static HikPage<HikDoorRecord> queryRecordList(DoorRecordQueryCondition condition){
        Map<String, String> path = HiKBaseUtil.getPath("/api/acs/v1/door/events");
        String contentType = "application/json";
        String body = JSONObject.toJSONString(condition);
        log.info("查询条件为>>>>>>>>>>{}",body);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, contentType , null);// post请求application/json类型参数
//        log.info("获取海康上的门禁记录,获取到的信息为>>>>>>>>>>>>>>>>>{}",result);
        JSONObject json = JSON.parseObject(result);
        if("0".equals(json.getString("code"))){
            List<HikDoorRecord> hikDoorRecords = json.getJSONObject("data").getJSONArray("list").toJavaList(HikDoorRecord.class);
            return HikPage.<HikDoorRecord>builder().
                    list(hikDoorRecords).
                    totalPage(json.getJSONObject("data").getIntValue("total")).build();
        }else{ //查询失败
            log.error("查询门禁记录失败得到的信息>>>>>>>{}",result);
            throw new HikException("查询门禁记录失败");
        }
    }




    /**
     * 查询海康车辆设备列表
     * @return
     */
    public static List<CarEquipment> queryAllCarChannel(){
        Map<String, String> path = HiKBaseUtil.getPath("/api/resource/v1/park/parkList");
        String contentType = "application/json";
        String result = ArtemisHttpUtil.doPostStringArtemis(path, "{}", null, null, contentType , null);// post请求application/json类型参数
        log.info("查询到的车库信息为>>>>>>>>>>>>{}",result);
        if(StringUtils.isEmpty(result)){
            return null;
        }

        JSONObject resultJson = JSON.parseObject(result);
        if(!("0".equals(resultJson.getString("code")))) {
            log.error("查询车库失败>>>>>>>{}",result);
            throw new HikException("查询设备失败");
        }

        List<String> parkIndexCodes = resultJson
                .getJSONArray("data")
                .stream()
                .map(obj->{
                    JSONObject jsonObject = (JSONObject)obj;
                    return jsonObject.getString("parkIndexCode");
                }).collect(Collectors.toList());

        if(CollectionUtil.isEmpty(parkIndexCodes)) {
            return null;
        }

        //单次查询传入的最大数据量
        int step = 1000;
        List<String> entranceIndexCodes = new ArrayList<>();
        //元素分段，一千个为一个单位
        for (List<String> tempParkIndexCodes : CollectionUtil.split(parkIndexCodes, step)) {
            path = HiKBaseUtil.getPath("/api/resource/v1/entrance/entranceList");
            String parkIndexCodeStr = CollectionUtil.join(tempParkIndexCodes, ",");

            result = ArtemisHttpUtil.doPostStringArtemis(path, new JSONObject(){{
                put("parkIndexCodes",parkIndexCodeStr);
            }}.toJSONString(), null, null, contentType , null);// post请求application/json类型参数

            if(StringUtils.isEmpty(result)){
                continue;
            }

            resultJson = JSON.parseObject(result);
            entranceIndexCodes.addAll(resultJson
                    .getJSONArray("data")
                    .stream()
                    .map(obj->{
                        JSONObject jsonObject = (JSONObject)obj;
                        return jsonObject.getString("entranceIndexCode");
                    }).collect(Collectors.toList()));

        }

        if(CollectionUtil.isEmpty(entranceIndexCodes)){
            return null;
        }

        List<CarEquipment> carEquipments = new ArrayList<>();
        //根据size进行分组，1000个一组
        for (List<String> tempEntranceIndexCodes :  CollectionUtil.split(entranceIndexCodes, step)) {
            path = HiKBaseUtil.getPath("/api/resource/v1/roadway/roadwayList");
            String entranceIndexCodeStr = CollectionUtil.join(tempEntranceIndexCodes, ",");

            result = ArtemisHttpUtil.doPostStringArtemis(path, new JSONObject(){{
                put("entranceIndexCodes",entranceIndexCodeStr);
            }}.toJSONString(), null, null, contentType , null);// post请求application/json类型参数


            if(StringUtils.isEmpty(result)){
                continue;
            }

            resultJson = JSON.parseObject(result);
            carEquipments.addAll(resultJson
                    .getJSONArray("data").toJavaList(CarEquipment.class));

        }

        return carEquipments;
    }



//    /**
//     * 查询设备在线状态
//     * @return
//     */
//    public static JSONArray searchOnline(String resourceType,List<String> resourceIndexCodes) {
//        Map<String, String> path = HiKBaseUtil.getPath("/api/resource/v1/resource/indexCodes/search");
//        String contentType = "application/json";
//        JSONObject body = new JSONObject();
//        body.put("resourceIndexCodes",resourceIndexCodes);
//        body.put("resourceType",resourceType);
//        String result = ArtemisHttpUtil.doPostStringArtemis(path, body.toJSONString(), null, null, contentType , null);// post请求application/json类型参数
//        JSONObject json = JSON.parseObject(result);
//        if("0".equals(json.getString("code"))){
//            JSONArray jsonArray = json.getJSONObject("data").getJSONArray("list");
//            return jsonArray;
//        }else{ //查询失败
//            log.error("查询设备失败得到的信息>>>>>>>{}",result);
//            throw new HikException("查询设备失败");
//        }
//
//
//    }


}

