package com.nbhy.utils;

import com.alibaba.fastjson.JSONObject;
import com.nbhy.filter.RepeatedlyRequestWrapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.MediaType;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 请求参数工具类
 * @author 蔡世杰
 */
@Slf4j
@UtilityClass
public class HttpParamUtil {


    /**
     * 获取请求参数
     * @param request
     * @return
     */
    public Map<String,Object> getParam(HttpServletRequest request){
        if (request instanceof RepeatedlyRequestWrapper) { //如果是json请求
            String bodyString = getBodyString(request);
            try {
                return JSONObject.parseObject(bodyString);
            }catch (Exception e){
                return null;
            }
        }else{//如果是普通请求
            return getFormDataParam(request);
        }
    }



    /**
     * 获取http的请求参数
     * @param request
     * @return
     */
    public Map<String,Object>  getFormDataParam(HttpServletRequest request){
        Map<String,Object> returnMap = new HashMap<>();
        List<String> paramKeyList = getParamKey(request);
        paramKeyList.stream().forEach(paramKeys->{
            String[] paramKeyArray = paramKeys.split("\\.");
            if(paramKeyArray.length == 1){
                returnMap.put(paramKeyArray[0],request.getParameter(paramKeyArray[0]));
            }else{
                Map<String, Object> map = getMap(returnMap, paramKeyArray, 0);
                map.put(paramKeyArray[paramKeyArray.length-1],request.getParameter(paramKeys));
            }
        });
        return returnMap;
    }

    /**
     * 获取request中的body数据
     * @param request
     * @return
     */
    public static String getBodyString(ServletRequest request)
    {
        StringBuilder  sb     = new StringBuilder();
        BufferedReader reader = null;
        try (InputStream inputStream = request.getInputStream()) {
            reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            log.warn("getBodyString出现问题！");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.warn(ExceptionUtils.getMessage(e));
                }
            }
        }
        return sb.toString();
    }


    /**
     * 获取body参数,如果没有参数返回null
     * @param request
     * @return
     */
    public Map<String,Object> getBodyMsg(HttpServletRequest request){
        StringBuilder  sb  = new StringBuilder();
        try {
            BufferedReader reader = request.getReader();
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            log.error("getBodyString出现问题！");
        }
        if(sb.length() >1){
           try {
               return JSONObject.parseObject(sb.toString());
           }catch (Exception e){
               log.error("转json失败>>>>>>>>{}",sb.toString());
               return null;
           }
        }else{
            return null;
        }
    }



    /**
     * 获取所有的请求参数key
     * @param request
     * @return
     */
    public List<String> getParamKey(HttpServletRequest request){
        Enumeration<String> parameterNames = request.getParameterNames();
        if(parameterNames == null){
            return null;
        }
        List<String> paramKeys = new ArrayList<>();
        while (parameterNames.hasMoreElements()){
            paramKeys.add(parameterNames.nextElement());
        }
        return paramKeys;
    }


    /**
     * 根据paramKey获取该参数节点对应的Map
     * @param map  对应的节点Map，初次传过来的为根节点
     * @param paramKeyArray 参数数据
     * @param index 数组索引
     */
    private Map<String,Object> getMap(Map<String,Object> map,String[] paramKeyArray,int index){
        if(paramKeyArray.length == (index+1))
            return map;
        if(map.containsKey(paramKeyArray[index])){ //如果包含这个对象
            return getMap((Map<String,Object>)map.get(paramKeyArray[index]),paramKeyArray,++index);
        }else{
            Map<String,Object> tempMap = new HashMap<>();
            map.put(paramKeyArray[index],tempMap);
            return getMap(tempMap,paramKeyArray,++index);
        }
    }
}
