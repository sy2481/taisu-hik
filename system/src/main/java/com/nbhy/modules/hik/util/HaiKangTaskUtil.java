package com.nbhy.modules.hik.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.nbhy.exception.BadRequestException;
import com.nbhy.modules.hik.exception.HikException;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.util.*;

@Slf4j
public class HaiKangTaskUtil {

    /**
     * 创建下载任务失败
     * 1	卡片
     * 2	指纹
     * 3	卡片+指纹（组合）
     * 4	人脸
     * 5	卡片+人脸（组合）
     * 6	人脸+指纹（组合）
     * 7	卡片+指纹+人脸（组合）
     * @param taskType
     * @return
     */
    public static String createTasks(Integer taskType){
        Map<String, String> path = HiKBaseUtil.getPath("/api/acps/v1/authDownload/task/addition");
        String contentType = "application/json";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("taskType",taskType);
        log.info("taskType：{}",jsonBody.toString());
        String result = ArtemisHttpUtil.doPostStringArtemis(path, jsonBody.toJSONString(), null, null, contentType , null);// post请求application/json类型参数
        JSONObject json = JSON.parseObject(result);
        if ("0".equals(json.get("code"))){
            String taskId = json.getJSONObject("data").get("taskId").toString();
            return taskId;
        }else {
            log.error("创建下载任务失败>>>>>>>>{}",result);
            throw new HikException("创建下载任务失败");
        }
    }


    /**
     * 填加下载任务
     * @param taskId
     * @param resourceIndexCodes
     * @param personId
     * @param startTime
     * @param endTime
     * @param operatorType 操作类型，0新增；1修改；2删除
     */
    public static void putTaskData(String taskId, Collection<String> resourceIndexCodes, String personId, DateTime startTime, DateTime endTime, Integer operatorType){
        Map<String, String> path = HiKBaseUtil.getPath("/api/acps/v1/authDownload/data/addition");
        String contentType = "application/json";
        JSONObject body = new JSONObject();
        body.put("taskId",taskId);
        JSONArray resourceInfos = new JSONArray();
        resourceIndexCodes.stream().forEach(obj -> {
            JSONObject resourceInfo = new JSONObject();
            resourceInfo.put("resourceIndexCode",obj);
            resourceInfo.put("resourceType","acsDevice");
            JSONArray channelNos = new JSONArray();
            channelNos.add(1);
            resourceInfo.put("channelNos",channelNos);
            resourceInfos.add(resourceInfo);
        });

        body.put("resourceInfos",resourceInfos);
        body.put("personInfos",new Object[]{
                new HashMap<String, Object>(4) {
                    {
                        put("personId", personId);
                        put("operatorType",operatorType);
                        if (startTime !=null && endTime != null) {
                            put("startTime", DateUtil.format(startTime, "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
                            put("endTime", DateUtil.format(endTime, "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
                        }
                    }
                }
        });
        log.info("JSON:>>>>>>>>>>>>>>>>>>>>>{}",body.toJSONString());
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body.toJSONString(), null, null, contentType , null);
        JSONObject object = JSON.parseObject(result);
        if (!"0".equals(object.get("code").toString())){
            throw new BadRequestException("添加下载任务数据失败");
        }
        log.info("添加权限结果》》》》{}",result);
    }

    /**
     * 批量添加下载任务
     * @param taskId
     * @param resourceIndexCodes
     * @param personIdList
     * @param operatorType
     */
    public static void putTaskData(String taskId, List<String> resourceIndexCodes,List<Map<String, Object>> personIdList,Integer operatorType){
        Map<String, String> path = HiKBaseUtil.getPath("/api/acps/v1/authDownload/data/addition");
        String contentType = "application/json";
        JSONObject body = new JSONObject();
        body.put("taskId",taskId);
        JSONArray resourceInfos = new JSONArray();

        resourceIndexCodes.stream().forEach(obj -> {
            JSONObject resourceInfo = new JSONObject();
            resourceInfo.put("resourceIndexCode",obj);
            resourceInfo.put("resourceType","acsDevice");
            JSONArray channelNos = new JSONArray();
            channelNos.add(1);
            resourceInfo.put("channelNos",channelNos);
            resourceInfos.add(resourceInfo);
        });

        body.put("resourceInfos",resourceInfos);
        body.put("personInfos",personIdList);
        log.info("JSON:>>>>>>>>>>>>>>>>>>>>>{}",body.toJSONString());
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body.toJSONString(), null, null, contentType , null);
        JSONObject object = JSON.parseObject(result);
        if (!"0".equals(object.get("code").toString())){
            throw new BadRequestException("添加下载任务数据失败");
        }
        log.info("添加权限结果》》》》{}",result);
    }

    /**
     * 下载任务
     * @param taskId
     */
    public static void downloadTask(String taskId){
        Map<String, String> path = HiKBaseUtil.getPath("/api/acps/v1/authDownload/task/start");
        String contentType = "application/json";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("taskId",taskId);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, jsonBody.toJSONString(), null, null, contentType , null);
        JSONObject object = JSON.parseObject(result);
        if (!"0".equals(object.get("code").toString())){
            throw new BadRequestException("下载任务失败");
        }
        log.info("下载任务结果》》》》》{}",result);
    }

    /**
     * 查询用户权限条目数量
     * @param personId
     * @return
     */
    public static Integer selectAuthTotal(String personId){
        Map<String, String> path = HiKBaseUtil.getPath("/api/acps/v1/auth_item/total/search");
        String contentType = "application/json";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("personIds",new JSONArray(1){
            {
                add(personId);
            }
        });
        jsonBody.put("queryType","acsDevice");
        jsonBody.put("personStatus",new JSONArray(1){
            {
                add(3);
            }
        });
        jsonBody.put("faceStatus",new JSONArray(1){
            {
                add(3);
            }
        });
        String result = ArtemisHttpUtil.doPostStringArtemis(path, jsonBody.toJSONString(), null, null, contentType , null);
        JSONObject object = JSON.parseObject(result);
        if (!"0".equals(object.get("code").toString())){
            throw new BadRequestException("查询权限数量失败");
        }
        log.info("查询用户权限数量{}",result);
        return object.getInteger("data");
    }

    /**
     * 查询正在下载的任务编号列表，只能查询由组件创建的任务.
     * @return
     */
    public static JSONObject getTaskList(){
        Map<String, String> path = HiKBaseUtil.getPath("/api/acps/v1/authDownload/task/list");
        String contentType = "application/json";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("pageNo",1);
        jsonBody.put("pageSize",1000);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, null, null, null, contentType , null);
        JSONObject jsonResult = JSON.parseObject(result);
        if (!"0".equals(jsonResult.get("code"))){
            throw new BadRequestException("查询任务编号列表失败");
        }
        return jsonResult.getJSONObject("data");
    }

    public static JSONObject getTaskList(Integer pageSize){
        Map<String, String> path = HiKBaseUtil.getPath("/api/acps/v1/authDownload/task/list");
        String contentType = "application/json";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("pageNo",1);
        jsonBody.put("pageSize",pageSize);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, null, null, null, contentType , null);
        JSONObject jsonResult = JSON.parseObject(result);
        if (!"0".equals(jsonResult.get("code"))){
            throw new BadRequestException("查询任务编号列表失败");
        }
        return jsonResult.getJSONObject("data");
    }

    /**
     * 终止正在下载的任务
     * @param taskId
     */
    public static void deleteTask(String taskId){
        Map<String, String> path = HiKBaseUtil.getPath("/api/acps/v1/authDownload/task/stop");
        String contentType = "application/json";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("taskId",taskId);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, jsonBody.toJSONString(), null, null, contentType , null);
        JSONObject jsonResult = JSON.parseObject(result);
        if (!"0".equals(jsonResult.get("code"))){
            throw new BadRequestException("终止正在下载的任务失败");
        }
        log.info("终止正在下载的任务成功");
    }

    /**
     * 添加权限配置
     * @param personList
     * @param resourceIndexCodes
     * @param startTime
     * @param endTime
     */
    public static String addConfig(List<String> personList,List<String> resourceIndexCodes,Timestamp startTime, Timestamp endTime){
        Map<String, String> path = HiKBaseUtil.getPath("/api/acps/v1/auth_config/add");
        String contentType = "application/json";
        JSONObject body = new JSONObject();
        JSONArray channelNos = new JSONArray();
        channelNos.add(1);
        JSONArray resourceInfos = new JSONArray();
        List<Map<String, Object>> personDatas = new ArrayList<>();

        resourceIndexCodes.stream().forEach(obj -> {
            JSONObject resourceInfo = new JSONObject();
            resourceInfo.put("resourceIndexCode",obj);
            resourceInfo.put("resourceType","acsDevice");
            resourceInfo.put("channelNos",channelNos);
            resourceInfos.add(resourceInfo);
        });

        Map<String, Object> personData = new HashMap<>(2);
        personData.put("personDataType","person");
        personData.put("indexCodes",personList);
        personDatas.add(personData);

        body.put("resourceInfos",resourceInfos);
        body.put("personDatas",personDatas);
        body.put("startTime", DateUtil.format(startTime, "yyyy-MM-dd'T'HH:mm:sss.SSSXXX"));
        body.put("endTime", DateUtil.format(endTime, "yyyy-MM-dd'T'HH:mm:sss.SSSXXX"));

        String result = ArtemisHttpUtil.doPostStringArtemis(path, body.toJSONString(), null, null, contentType , null);
        JSONObject jsonResult = JSON.parseObject(result);
        if (!"0".equals(jsonResult.get("code"))){
            throw new BadRequestException("添加权限配置失败");
        }
        log.info("添加权限配置body的信息：》》》{}",body.toJSONString());

        return jsonResult.getJSONObject("data").getString("taskId");
    }

    /**
     * 删除权限配置
     * @param personList
     * @param resourceIndexCodes
     */
    public static String deleteConfig(List<String> personList,List<String> resourceIndexCodes){
        Map<String, String> path = HiKBaseUtil.getPath("/api/acps/v1/auth_config/delete");
        String contentType = "application/json";
        JSONObject body = new JSONObject();
        JSONArray channelNos = new JSONArray();
        channelNos.add(1);
        JSONArray resourceInfos = new JSONArray();
        List<Map<String, Object>> personDatas = new ArrayList<>();

        resourceIndexCodes.stream().forEach(obj -> {
            JSONObject resourceInfo = new JSONObject();
            resourceInfo.put("resourceIndexCode",obj);
            resourceInfo.put("resourceType","acsDevice");
            resourceInfo.put("channelNos",channelNos);
            resourceInfos.add(resourceInfo);
        });

        Map<String, Object> personData = new HashMap<>(2);
        personData.put("personDataType","person");
        personData.put("indexCodes",personList);
        personDatas.add(personData);

        body.put("resourceInfos",resourceInfos);
        body.put("personDatas",personDatas);


        String result = ArtemisHttpUtil.doPostStringArtemis(path, body.toJSONString(), null, null, contentType , null);
        JSONObject jsonResult = JSON.parseObject(result);
        if (!"0".equals(jsonResult.get("code"))){
            throw new BadRequestException("删除权限配置失败");
        }
        log.info("删除权限配置body的信息：》》》{}",body.toJSONString());

        return jsonResult.getJSONObject("data").getString("taskId");
    }

    /**
     * 根据出入权限配置快捷下载
     * @param taskType
     */
    public static void authDownload(Integer taskType,List<String> resourceIndexCodes){
        Map<String, String> path = HiKBaseUtil.getPath("/api/acps/v1/authDownload/configuration/shortcut");
        String contentType = "application/json";
        JSONObject body = new JSONObject();
        body.put("taskType",taskType);

        JSONArray resourceInfos = new JSONArray();
        JSONArray channelNos = new JSONArray();
        channelNos.add(1);
        resourceIndexCodes.stream().forEach(obj -> {
            JSONObject resourceInfo = new JSONObject();
            resourceInfo.put("resourceIndexCode",obj);
            resourceInfo.put("resourceType","acsDevice");
            resourceInfo.put("channelNos",channelNos);
            resourceInfos.add(resourceInfo);
        });
        body.put("resourceInfos",resourceInfos);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body.toJSONString(), null, null, contentType , null);
        JSONObject jsonResult = JSON.parseObject(result);
        if (!"0".equals(jsonResult.get("code"))){
            throw new BadRequestException("根据出入权限配置快捷下载失败");
        }
        log.info("根据出入权限配置快捷下载body的信息：》》》{}",body.toJSONString());
    }
    /**
     * 根据出入权限配置快捷下载
     * @param taskType
     */
    public static void authDownload(Integer taskType){
        Map<String, String> path = HiKBaseUtil.getPath("/api/acps/v1/authDownload/configuration/shortcut");
        String contentType = "application/json";
        JSONObject body = new JSONObject();
        body.put("taskType",taskType);

        String result = ArtemisHttpUtil.doPostStringArtemis(path, body.toJSONString(), null, null, contentType , null);
        JSONObject jsonResult = JSON.parseObject(result);
        if (!"0".equals(jsonResult.get("code"))){
            throw new BadRequestException("根据出入权限配置快捷下载失败");
        }
        log.info("根据出入权限配置快捷下载body的信息：》》》{}",body.toJSONString());
    }

    /**
     * 查询权限配置单进度
     * @param taskId
     */
    public static JSONObject rateSearch(String taskId){
        Map<String, String> path = HiKBaseUtil.getPath("/api/acps/v1/auth_config/rate/search");
        String contentType = "application/json";
        JSONObject body = new JSONObject();
        body.put("taskId",taskId);

        String result = ArtemisHttpUtil.doPostStringArtemis(path, body.toJSONString(), null, null, contentType , null);
        JSONObject jsonResult = JSON.parseObject(result);
        if (!"0".equals(jsonResult.get("code"))){
            throw new BadRequestException("查询权限配置单失败");
        }

        return jsonResult.getJSONObject("data");
    }
}
