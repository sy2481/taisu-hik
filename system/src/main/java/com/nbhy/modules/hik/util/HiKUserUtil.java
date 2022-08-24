
package com.nbhy.modules.hik.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.nbhy.exception.BadRequestException;
import com.nbhy.modules.hik.constant.HikDeptConstant;
import com.nbhy.modules.hik.constant.HikPersonConstant;
import com.nbhy.modules.hik.domain.dto.HEEmpDto;
import com.nbhy.modules.hik.domain.dto.HikPage;
import com.nbhy.modules.hik.domain.dto.HikResponseUser;
import com.nbhy.modules.hik.domain.dto.HikUser;
import com.nbhy.modules.hik.domain.entity.HikPerson;
import com.nbhy.modules.hik.exception.HikException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class HiKUserUtil {


//    /**
//     * @param user
//     * @return 成功返回true 失败返回false
//     */
//    public boolean create(HEEmp user){
//        Map<String, String> path = HiKBaseUtil.getPath("/api/resource/v2/person/single/add");
//        String contentType = "application/json";
//        String body = getJsonBody(user,true);
//        log.info("新增用户传输的参数为>>>>>>>>>>ID:{},name:{},phone:{}",user.getEmpId(), user.getEmpName(), user.getEmpPhone());
//        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, contentType , null);// post请求application/json类型参数
//        log.info("新增用户获取到的信息为>>>>>>>>>>>>>>>>>{}",result);
//        JSONObject json = JSON.parseObject(result);
//        if("0".equals(json.getString("code"))){
//            return true;
//        }
//        return false;
//    }

    /**
     * @param user
     * @return 成功返回true 失败返回false
     */
    public static HEEmpDto create(HikUser user){
        Map<String, String> path = HiKBaseUtil.getPath("/api/resource/v2/person/single/add");
        String contentType = "application/json";
        //log.info("传入信息>>>>>>>>hikUser:{}",user.toString());
        String body = getJsonBody(user,true);
        //log.info("新增用户传输的参数为>>>>>>>>>>body:{}",body);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, contentType , null);// post请求application/json类型参数
        JSONObject json = JSON.parseObject(result);
        if("0".equals(json.getString("code"))){
            String personId = json.getJSONObject("data").getString("personId");
            String faceId = json.getJSONObject("data").getString("faceId");
            HEEmpDto hEEmpDto = new HEEmpDto();
            hEEmpDto.setPersonId(personId);
            hEEmpDto.setFaceId(faceId);
            return hEEmpDto;
        }else {
            log.error("新增用户信息失败>>>>>>>>>>>>>>>>>{}",result);
            throw new HikException("新增海康人员失败");
        }
//        HEEmpDto hEEmpDto = new HEEmpDto();
//        hEEmpDto.setPersonId("111");
//        hEEmpDto.setFaceId("faceId");
//        return hEEmpDto;
    }

//    /**
//     * 新增用户
//     * @param users
//     * @return 成功返回true 失败返回false
//     */
//    public static boolean createList(List<HEEmp> users){
//        Map<String, String> path = HiKBaseUtil.getPath("/api/resource/v1/person/batch/add");
//        String contentType = "application/json";
//        users.stream().map(user->{
//            getJsonBody(user,);
//        })
//        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, contentType , null);// post请求application/json类型参数
//        JSONObject json = JSON.parseObject(result);
//        if(json.getIntValue("code") == 0){
//            return true;
//        }
//        return false;
//    }


    /**
     * 新增用户
     * @param user
     * @return 成功返回true 失败返回false
     */
    public static boolean update(HikUser user){
        Map<String, String> path = HiKBaseUtil.getPath("/api/resource/v1/person/single/update");
        String contentType = "application/json";
        String body = getJsonBody(user,false);
        log.info("更新用户传输的参数为>>>>>>>>>>ID=>{}",user.getPersonId());
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, contentType , null);// post请求application/json类型参数
        log.info("更新用户获取到的信息为>>>>>>>>>>>>>>>>>{}",result);
        JSONObject json = JSON.parseObject(result);
        if("0".equals(json.getString("code"))){
            return true;
        }
        return false;
    }


    /**
     * 删除用户
     * @param personIds
     * @return 成功返回true 失败返回false
     */
    public static boolean delete(String[] personIds){
        JSONObject jsonObject = queryById(personIds);
        if(jsonObject==null){
            log.error("查询人员详细信息失败");
            return false;
        }
        //删除人脸
        if(jsonObject.getJSONObject("data").getIntValue("total")>0){
            jsonObject.getJSONObject("data").getJSONArray("list").stream().forEach(obj->{
                JSONObject json = (JSONObject) obj;
                JSONArray personPhotos = json.getJSONArray("personPhoto");
                if(!personPhotos.isEmpty()) { // 如果人脸不为空
                    JSONObject personPhoto = (JSONObject) personPhotos.get(0);
                    String faceId = personPhoto.getString("personPhotoIndexCode");
                    deleteFace(faceId);
                }
            });
        }
        //删除人员
        Map<String, String> path = HiKBaseUtil.getPath("/api/resource/v1/person/batch/delete");
        String contentType = "application/json";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("personIds",personIds);
        log.info("删除用户传输的参数为>>>>>>>>>>需要删除的人员个数{}",personIds.length);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, jsonBody.toJSONString(), null, null, contentType , null);// post请求application/json类型参数
        log.info("删除用户获取到的信息为>>>>>>>>>>>>>>>>>{}",result);
        JSONObject json = JSON.parseObject(result);
        if("0".equals(json.getString("code"))){
            return true;
        }
        return false;
    }


    /**
     * 删除人脸id
     * @param faceId
     * @return
     */
    public static boolean deleteFace(String faceId){
        Map<String, String> path = HiKBaseUtil.getPath("/api/resource/v1/face/single/delete");
        String contentType = "application/json";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("faceId",faceId);
        log.info("删除用户人脸传输的参数为>>>>>>>>>>{}",jsonBody.toJSONString());
        String result = ArtemisHttpUtil.doPostStringArtemis(path, jsonBody.toJSONString(), null, null, contentType , null);// post请求application/json类型参数
        log.info("删除用户人脸获取到的信息为>>>>>>>>>>>>>>>>>{}",result);
        JSONObject json = JSON.parseObject(result);
        if("0".equals(json.getString("code"))){
            return true;
        }
        return false;
    }

    /**
     * 根据deptId查询
     * @return
     */
    public static List<HikResponseUser> queryAll(){
        Map<String, String> path = HiKBaseUtil.getPath("/api/resource/v2/person/personList");
        String contentType = "application/json";
        int length = 1;
        int pageSize = 1000;
        List<HikResponseUser> responseUsers = null;
        JSONObject queryCondition = new JSONObject();
        queryCondition.put("pageSize",pageSize);
        for (int i = 1 ; i <=length ; i++){
            queryCondition.put("pageNo", i);
            String result = ArtemisHttpUtil.doPostStringArtemis(path, queryCondition.toJSONString(), null, null, contentType , null);// post请求application/json类型参数
//            log.info("查询所有员工,获到的信取息为>>>>>>>>>>>>>>>>>{}",result);
            log.info("查询所有员工正常执行。。。");
            JSONObject json = JSON.parseObject(result);
            if("0".equals(json.getString("code"))){
                if(responseUsers == null){
                    responseUsers = new ArrayList<>();
                }
                responseUsers.addAll(json.getJSONObject("data").getJSONArray("list").toJavaList(HikResponseUser.class));
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
        return responseUsers;
    }



    /**
     * 查询用户
     * @return
     */
    public static HikPage<HikResponseUser> queryAll(int pageSize, int pageNo){
        Map<String, String> path = HiKBaseUtil.getPath("/api/resource/v2/person/personList");
        String contentType = "application/json";
        JSONObject queryCondition = new JSONObject();
        queryCondition.put("pageSize",pageSize);
        queryCondition.put("pageNo", pageNo);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, queryCondition.toJSONString(), null, null, contentType , null);// post请求application/json类型参数
//            log.info("查询所有员工,获到的信取息为>>>>>>>>>>>>>>>>>{}",result);
            log.info("查询所有员工正常执行。。。");
            JSONObject json = JSON.parseObject(result);
            if("0".equals(json.getString("code"))){
                List<HikResponseUser> responseUsers = json.getJSONObject("data").getJSONArray("list").toJavaList(HikResponseUser.class);
                return HikPage.
                        <HikResponseUser>builder().
                        list(responseUsers).
                        totalPage(json.getJSONObject("data").getIntValue("total")).
                        build();
            }else{ //查询失败
                log.error("查询数据失败>>>>>>>>>{}",result);
                throw new HikException("查询数据失败");
            }
    }




    /**
     * 根据deptId查询
     * @param deptId
     * @return
     */
    public static List<HikResponseUser> queryByDept(String deptId){
        Map<String, String> path = HiKBaseUtil.getPath("/api/resource/v2/person/orgIndexCode/personList");
        String contentType = "application/json";
        int length = 1;
        List<HikResponseUser> heEmps = null;
        int pageSize = 1000;
        JSONObject queryCondition = new JSONObject();
        queryCondition.put("pageSize",pageSize);
        queryCondition.put("orgIndexCode",deptId);
        for (int i = 1 ; i <=length ; i++){
            queryCondition.put("pageNo", i);
            String result = ArtemisHttpUtil.doPostStringArtemis(path, queryCondition.toJSONString(), null, null, contentType , null);// post请求application/json类型参数
            log.info("根据deptId查询,获取到的信息为>>>>>>>>>>>>>>>>>{}",result);
            JSONObject json = JSON.parseObject(result);
            if("0".equals(json.getString("code"))){
                if(heEmps == null){
                    heEmps = new ArrayList<>();
                }
                heEmps.addAll(json.getJSONObject("data").getJSONArray("list").toJavaList(HikResponseUser.class));
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
        return heEmps;
    }


    /**
     * 根据id查询
     * @param personIds
     * @return
     */
    public static JSONObject queryById(String[] personIds){
        Map<String, String> path = HiKBaseUtil.getPath("/api/resource/v1/person/condition/personInfo");
        String contentType = "application/json";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("paramName","personId");
        jsonBody.put("paramValue",personIds);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, jsonBody.toJSONString(), null, null, contentType , null);// post请求application/json类型参数
        log.info("根据id查询,获取到的信息为>>>>>>>>>>>>>>>>>{}",result);
        JSONObject json = JSON.parseObject(result);
        if("0".equals(json.getString("code"))){
            return json;
        }
        return null;
    }

    /**
     * 批量新增用户
     * @param users
     * @return 成功返回true 失败返回false
     */
    public static boolean createList(List<HikUser> users){
        Map<String, String> path = HiKBaseUtil.getPath("/api/resource/v1/person/batch/add");
        String contentType = "application/json";
        List<String> msgs  = users.stream().map(user -> getJsonBody(user, false)).collect(Collectors.toList());
        String body = CollectionUtil.join(msgs, ",");
        body = "["+body+"]";
        log.info("批量新增人员，发送的信息为>>>>>>>>>>>>>>>>>>个数{}",users);
        String result = ArtemisHttpUtil.doPostStringArtemis(path,body , null, null, contentType , null);// post请求application/json类型参数
        log.info("批量新增人员，收到的信息为>>>>>>>>>>{}",result);
        JSONObject json = JSON.parseObject(result);
        if(json.getIntValue("code") == 0){
            return true;
        }
        return false;
    }

    /**
     * 新增人脸
     * @param personId 人员的id
     * @param faceData 人脸base64字符串
     * @return
     */
    public static  JSONObject createFace(String personId,String faceData){
        Map<String, String> path = HiKBaseUtil.getPath("/api/resource/v1/face/single/add");
        String contentType = "application/json";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("personId",personId);
        jsonBody.put("faceData",faceData);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, jsonBody.toJSONString(), null, null, contentType , null);// post请求application/json类型参数
        log.info("创建人脸,获取到的信息为>>>>>>>>>>>>>>>>>{}",result);
        JSONObject json = JSON.parseObject(result);
        if("0".equals(json.getString("code"))){
            return json.getJSONObject("data");
        }
        return null;
    }


    /**
     * 修改人脸
     * @param faceId 人员的id
     * @param faceData 人脸base64字符串
     * @return
     */
    public static  JSONObject updateFace(String faceId, String faceData){
        Map<String, String> path = HiKBaseUtil.getPath("/api/resource/v1/face/single/update");
        String contentType = "application/json";
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("faceId",faceId);
        jsonBody.put("faceData",faceData);
        String result = ArtemisHttpUtil.doPostStringArtemis(path, jsonBody.toJSONString(), null, null, contentType , null);// post请求application/json类型参数
        log.info("根据id查询,获取到的信息为>>>>>>>>>>>>>>>>>{}",result);
        JSONObject json = JSON.parseObject(result);
        if("0".equals(json.getString("code"))){
            return json.getJSONObject("data");
        }
        throw new HikException("修改人脸失败");
    }


    /**
     * 把User转换为json字符串
     * @param user
     * @param isCreate 是否是创建
     * @return
     */
    private static String getJsonBody(HikUser user, boolean isCreate){
        /**
         * STEP5：组装请求参数
         */
        JSONObject jsonBody = new JSONObject();

        if(!isCreate){
            if(StringUtils.isEmpty(user.getPersonId())){
                throw new HikException("personId不能为空");
            }
        }

        if(!StringUtils.isEmpty(user.getPersonId())){
            jsonBody.put("personId", user.getPersonId());
        }
        if (StringUtils.isEmpty(user.getPersonName())) {
            throw new HikException("员工名称不能为空");
        } else {
            jsonBody.put("personName", user.getPersonName());
        }

        if(!StringUtils.isEmpty(user.getGender())){
            jsonBody.put("gender", user.getGender());
        }else{
            throw new HikException("性别不能为空");
        }

        if(StringUtils.isEmpty(user.getOrgIndexCode())){
            throw new HikException("orgIndexCode不能为空");
        }else{
            jsonBody.put("orgIndexCode", user.getOrgIndexCode());
        }

        if(!StringUtils.isEmpty(user.getBirthday())){
            jsonBody.put("birthday", user.getBirthday());
        }

        if(!StringUtils.isEmpty(user.getPhoneNo())){
            jsonBody.put("phoneNo", user.getPhoneNo());
        }

        if(!StringUtils.isEmpty(user.getEmail())){
            jsonBody.put("email", user.getEmail());
        }

        if(!StringUtils.isEmpty(user.getCertificateType())){
            jsonBody.put("certificateType", user.getCertificateType());
        }


        if(!StringUtils.isEmpty(user.getCertificateNo())){
            jsonBody.put("certificateNo", user.getCertificateNo());
        }

        if(!StringUtils.isEmpty(user.getJobNo())){
            jsonBody.put("jobNo", user.getJobNo());
        }


        if(isCreate) { //如果是创建才上传人脸。
            if(user.getFaces() != null && !user.getFaces().isEmpty()) {
                List<Map<String, String>> faces = user.getFaces().stream().map(face -> {
                    Map<String, String> faceData = new HashMap<>(2);
                    faceData.put("faceData", face);
                    return faceData;
                }).collect(Collectors.toList());

                jsonBody.put("faces", faces);
            }

        }
        return  jsonBody.toJSONString();
    }

    /**
     * 根据hikUser获取到HikPerson
     * @param hikPerson 海康人员信息
     * @param faceBase64Str 海康人脸
     */
    public static HikUser getHikUserByHikPerson(HikPerson hikPerson, String faceBase64Str) {
        HikUser hikUser = new HikUser();
        BeanUtil.copyProperties(hikPerson,hikUser);
        hikUser.setFaces(Arrays.asList(faceBase64Str));
        hikUser.setJobNo(null);
        hikUser.setEmail(null);
        hikUser.setPhoneNo(null);
        if(hikPerson.getPersonType() == HikPersonConstant.INTERNAL_STAFF){
            hikUser.setOrgIndexCode(HikDeptConstant.INTERNAL_STAFF);
        }else if(hikPerson.getPersonType() == HikPersonConstant.VENDOR_EMPLOYEES){
            hikUser.setOrgIndexCode(HikDeptConstant.MANUFACTURER_EMPLOYEES);
        }else{
            throw new HikException("新增人员部门不能为空");
        }
        return hikUser;
    }


//    /**
//     * 获取人员图片
//     * @return
//     */
//    public static byte[] getPersonPic(String picUri,String serverIndexCode){
//        Map<String, String> path = HiKBaseUtil.getPath("/api/resource/v1/person/picture");
//        String contentType = "application/json";
//        JSONObject jsonBody = new JSONObject();
//        jsonBody.put("picUri",picUri);
//        jsonBody.put("serverIndexCode",serverIndexCode);
//        log.info(jsonBody.toString());
////        String result = ArtemisHttpUtil.doPostStringArtemis(path, jsonBody.toJSONString(), null, null, contentType , null);// post请求application/json类型参数
//        HttpResponse response = ArtemisHttpUtil.doPostStringImgArtemis(path, jsonBody.toJSONString(), null, null, contentType, null);
//        try {
//            InputStream content = response.getEntity().getContent();
//            byte[] bytes = new byte[content.available()];
//            log.info("获取到的字节数组为>>>>>{}",Arrays.toString(bytes));
//            content.read(bytes);
//            String imageStr = FileUtil.getImageStr(bytes);
//            log.info("获取到的输出流为>>>>>>>>>>>{},长度为{}",imageStr,content.available());
//            return bytes;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }






}

