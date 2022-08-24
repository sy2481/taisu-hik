
package com.nbhy.modules.hik.util;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.nbhy.exception.BadRequestException;
import com.nbhy.modules.hik.domain.dto.HikDept;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class HikDeptUtil {


    /**
     * 批量新增部门
     * @param depts
     * @return
     */
    public static boolean createList(List<HikDept> depts){
        Map<String, String> path = HiKBaseUtil.getPath("/api/resource/v1/org/batch/add");
        String contentType = "application/json";
        List<JSONObject> jsonList = depts.stream().map(dept -> getDeptJsonObject(dept,true)).collect(Collectors.toList());
        String body = JSONObject.toJSONString(jsonList);
        log.info("新增部门数据为>>>>>>>>>>>>>>>>>个数是{}",depts.size());
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, contentType , null);// post请求application/json类型参数
        log.info("新增部门获取到的信息为>>>>>>>>>>>>>>>>>{}",result);
        JSONObject json = JSON.parseObject(result);
        if("0".equals(json.getString("code"))){
            return true;
        }
        return false;
    }


    /**
     * 批量新增部门
     * @param ids
     * @return
     */
    public static boolean deleteBatch(String[] ids){
        Map<String, String> path = HiKBaseUtil.getPath("/api/resource/v1/org/batch/delete");
        String contentType = "application/json";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("indexCodes",ids);
        String body = jsonBody.toJSONString();
        log.info("删除部门数据为>>>>>>>>>>>>>>>>>个数是{}",ids.length);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, jsonBody.toJSONString(), null, null, contentType , null);// post请求application/json类型参数
        log.info("删除部门获取到的信息为>>>>>>>>>>>>>>>>>{}",result);
        JSONObject json = JSON.parseObject(result);
        if("0".equals(json.getString("code"))){
            return true;
        }
        return false;
    }


    /**
     * 修改组织
     * @param dept
     * @return
     */
    public static boolean update(HikDept dept){
        Map<String, String> path = HiKBaseUtil.getPath("/api/resource/v1/org/single/update");
        String contentType = "application/json";
        JSONObject jsonObject = getDeptJsonObject(dept,false);
        jsonObject.remove("parentIndexCode");
        String body = jsonObject.toJSONString();
        log.info("更新部门数据为>>>>>>>>>>>>>>>>>{}",body);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, contentType , null);// post请求application/json类型参数
        log.info("更新部门获取到的信息为>>>>>>>>>>>>>>>>>{}",result);
        JSONObject json = JSON.parseObject(result);
        if("0".equals(json.getString("code"))){
            return true;
        }
        return false;
    }




    /**
     * 根据父部门的id查询所有子部门的id
     * @param parentId
     * @return
     */
    public static List<HikDept> findByPid(String  parentId){
        Map<String, String> path = HiKBaseUtil.getPath("/api/resource/v1/org/parentOrgIndexCode/subOrgList");
        String contentType = "application/json";
        int length = 1;
        List<HikDept> heDepts = null;
        int pageSize = 1000;
        JSONObject queryCondition = new JSONObject();
        queryCondition.put("pageSize",pageSize);
        queryCondition.put("parentOrgIndexCode",parentId);
        for (int i = 1 ; i <=length ; i++){
            queryCondition.put("pageNo", i);
            String result = ArtemisHttpUtil.doPostStringArtemis(path, queryCondition.toJSONString(), null, null, contentType , null);// post请求application/json类型参数
            log.info("根据父部门的id查询所有子部门的id,获取到的信息为>>>>>>>>>>>>>>>>>{}",result);
            JSONObject json = JSON.parseObject(result);
            if("0".equals(json.getString("code"))){
                if(heDepts == null){
                    heDepts = new ArrayList<>();
                }
                heDepts.addAll(json.getJSONObject("data").getJSONArray("list").toJavaList(HikDept.class));

//                    heDepts.addAll(json.getJSONObject("data").getJSONArray("list").stream().map(dept->{
//                    JSONObject jsonDept = (JSONObject) dept;
//                    HEDept heDept = new HEDept();
//                    heDept.setDeptId(jsonDept.getString("orgIndexCode"));
//                    heDept.setDeptName(jsonDept.getString("orgName"));
//                    heDept.setDeptParentId(jsonDept.getString("parentOrgIndexCode"));
//                    return heDept;
//                }).collect(Collectors.toList()));

            }else{ //查询失败
                break;
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
        return heDepts;
    }




    /**
     * 获取海康上的所有部门
     * @return 如果成功返回数据 失败返回null
     */
    public static List<HikDept> queryAllV2(){
        Map<String, String> path = HiKBaseUtil.getPath("/api/resource/v2/org/advance/orgList");
        String contentType = "application/json";
        int length = 1;
        List<HikDept> heDepts = null;
        int pageSize = 1000;
        JSONObject queryCondition = new JSONObject();
        queryCondition.put("pageSize",pageSize);
        for (int i = 1 ; i <=length ; i++){
            queryCondition.put("pageNo", i);
            String result = ArtemisHttpUtil.doPostStringArtemis(path, queryCondition.toJSONString(), null, null, contentType , null);// post请求application/json类型参数
//            log.info("获取海康上的所有部门,获取到的信息为>>>>>>>>>>>>>>>>>{}",result);
            JSONObject json = JSON.parseObject(result);
            if("0".equals(json.getString("code"))){
                if(heDepts == null){
                    heDepts = new ArrayList<>();
                }
                heDepts.addAll(json.getJSONObject("data").getJSONArray("list").toJavaList(HikDept.class));
//                    heDepts.addAll(json.getJSONObject("data").getJSONArray("list").stream().map(dept->{
//                    JSONObject jsonDept = (JSONObject) dept;
//                    HEDept heDept = new HEDept();
//                    heDept.setDeptId(jsonDept.getString("orgIndexCode"));
//                    heDept.setDeptName(jsonDept.getString("orgName"));
//                    heDept.setDeptParentId(jsonDept.getString("parentOrgIndexCode"));
//                    return heDept;
//                }).collect(Collectors.toList()));

            }else{ //查询失败
                break;
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
        return heDepts;
    }






    /**
     * 获取海康上的所有部门
     * @return 如果成功返回数据 失败返回null
     */
    public static List<HikDept> queryAllV1(){
        Map<String, String> path = HiKBaseUtil.getPath("/api/resource/v1/org/advance/orgList");
        String contentType = "application/json";
        int length = 1;
        List<HikDept> heDepts = null;
        int pageSize = 1000;
        JSONObject queryCondition = new JSONObject();
        queryCondition.put("pageSize",pageSize);
        for (int i = 1 ; i <=length ; i++){
            queryCondition.put("pageNo", i);
            String result = ArtemisHttpUtil.doPostStringArtemis(path, queryCondition.toJSONString(), null, null, contentType , null);// post请求application/json类型参数
            log.info("获取海康上的所有部门,获取到的信息为>>>>>>>>>>>>>>>>>{}",result);
            JSONObject json = JSON.parseObject(result);
            if("0".equals(json.getString("code"))){
                if(heDepts == null){
                    heDepts = new ArrayList<>();
                }
                heDepts.addAll(json.getJSONObject("data").getJSONArray("list").toJavaList(HikDept.class));
//                    heDepts.addAll(json.getJSONObject("data").getJSONArray("list").stream().map(dept->{
//                    JSONObject jsonDept = (JSONObject) dept;
//                    HEDept heDept = new HEDept();
//                    heDept.setDeptId(jsonDept.getString("orgIndexCode"));
//                    heDept.setDeptName(jsonDept.getString("orgName"));
//                    heDept.setDeptParentId(jsonDept.getString("parentOrgIndexCode"));
//                    return heDept;
//                }).collect(Collectors.toList()));

            }else{ //查询失败
                break;
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
        return heDepts;
    }



    /**
     * 获取dept json对象
     * @param dept
     * @return
     */
    private static JSONObject getDeptJsonObject(HikDept dept, boolean isCreate){
        JSONObject jsonBody = new JSONObject();
        if(!isCreate){ //如果是更新
            if(StringUtils.isEmpty(dept.getOrgIndexCode())){
                throw new BadRequestException("orgIndexCode不能为空");
            }
        }
        if(StringUtils.isEmpty(dept.getOrgName())){
            throw new BadRequestException("orgName不能为空");
        }
        if(StringUtils.isEmpty(dept.getParentOrgIndexCode())){
            throw new BadRequestException("parentIndexCode不能为空");
        }
        jsonBody.put("orgIndexCode", dept.getOrgIndexCode());
        jsonBody.put("orgName", dept.getOrgName());
        jsonBody.put("parentIndexCode", dept.getParentOrgIndexCode());
        return  jsonBody;
    }



}

