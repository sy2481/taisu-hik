package com.nbhy.aspect;

import com.alibaba.fastjson.JSONObject;
import com.nbhy.annotation.RepeatSubmit;
import com.nbhy.exception.BadRequestException;
import com.nbhy.utils.HttpParamUtil;
import com.nbhy.utils.RequestHolder;
import com.nbhy.utils.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 防止表单重复提交切面
 */
@Aspect
@Component
public class RepeatSubmitAspect {

    private final String REPEAT_SUBMIT_KEY = "REPEAT_SUBMIT_KEY:";

    private final RedisTemplate<Object,Object> redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(Component.class);

    public RepeatSubmitAspect(RedisTemplate<Object,Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }
    @Pointcut("@annotation(com.nbhy.annotation.RepeatSubmit)")
    public void pointcut() {
    }


    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = RequestHolder.getHttpServletRequest();
        //请求地址,作为Key值的一部分
        String url = request.getRequestURI();
        //获取当前请求参数
        Map<String, Object> param = HttpParamUtil.getParam(request);
        //如果请求参数不存在，则返回
        if(param == null || param.isEmpty()){
            logger.error("参数为空，无法比对>>>>>url:{}",url);
            return joinPoint.proceed();
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method signatureMethod = signature.getMethod();
        RepeatSubmit repeatSubmit = signatureMethod.getAnnotation(RepeatSubmit.class);
        RepeatSubmit.KeyType keyType = repeatSubmit.keyType();
        String key = null;
        switch (keyType){
            case HEAD:
                key = getKeyByHead(repeatSubmit.paramKey(),request);
                break;
            case IP:
                key = getKeyByIp(request);
                break;
            case PARAM:{
                key = StringUtils.getParamKey(repeatSubmit.paramKey(),param,"");
                if(StringUtils.isNotEmpty(key)){
                    key = REPEAT_SUBMIT_KEY+key;
                }
                break;
            }
        }

        if(REPEAT_SUBMIT_KEY.equals(key)){
            logger.error("paramKey不存在>>>>>>>>");
            return joinPoint.proceed();
        }

        key = key + url;
        //旧的请求提交的参数
        String oldParam = (String)redisTemplate.opsForValue().get(key);
        //新的请求参数
        String newParam = JSONObject.toJSONString(param);
        if(oldParam == null || !oldParam.equals(newParam)){ //如果没有旧的请求参数，或者旧的请求参数和新的请求参数不一致
            redisTemplate.opsForValue().set(key,newParam,repeatSubmit.interval(), TimeUnit.MILLISECONDS);
            return joinPoint.proceed();
        }else{
            logger.info("触发表单重复提交，url：{}，请求参数：{}", url, newParam);
            throw new BadRequestException(repeatSubmit.message());
        }
    }


    /**
     * 获取key组合,从请求头获取
     * @param keyParams
     * @param request
     * @return
     */
    private String getKeyByHead(String keyParams,HttpServletRequest request){
        StringBuilder key = new StringBuilder();
        key.append(REPEAT_SUBMIT_KEY);
        String[] keyParamArray = keyParams.split(",");
        for (String paramKey : keyParamArray) {
            String header = request.getHeader(paramKey);
            if(StringUtils.isNotEmpty(header)){
                key.append(header);
            }
        }
        return key.toString();
    }

    /**
     * 获取key组合，从请求参数获取
     * @param keyParams
     * @param request
     * @return
     */
    private String getKeyByRequestParam(String keyParams,HttpServletRequest request){
        StringBuilder key = new StringBuilder();
        key.append(REPEAT_SUBMIT_KEY);
        String[] keyParamArray = keyParams.split(",");
        for (String paramKey : keyParamArray) {
            String param = request.getParameter(paramKey);
            if(StringUtils.isNotEmpty(param)){
                key.append(param);
            }
        }
        return key.toString();
    }



    /**
     * 获取key组合，从IP获取
     * @param request
     * @return
     */
    private String getKeyByIp(HttpServletRequest request){
        StringBuilder key = new StringBuilder();
        key.append(REPEAT_SUBMIT_KEY);
        key.append(StringUtils.getIp(request));
        return key.toString();
    }


    /**
     * 通过request获取排序之后的keys
     * @param request
     * @return
     */
    private String getParam(HttpServletRequest request){
        Enumeration<String> parameterNames = request.getParameterNames();
        if(parameterNames == null){
            return null;
        }
        List<String> paramKeys = new ArrayList<>();
        while (parameterNames.hasMoreElements()){
            paramKeys.add(parameterNames.nextElement());
        }
        paramKeys.sort((a1,b1)->a1.compareTo(b1));
        StringBuilder paramValue = new StringBuilder();
        paramKeys.stream().forEach(key->{
            paramValue.append(key);
            paramValue.append(request.getParameter(key));
        });
        return paramValue.toString();
    }



    /**
     * 通过request获取排序之后的keys
     * @param request
     * @return
     */
    private JSONObject getParamMap(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();

        System.out.println(parameterMap);
//        if (request instanceof HttpServletRequest
//                && StringUtils.startsWithIgnoreCase(request.getContentType(), MediaType.APPLICATION_JSON_VALUE)) { //如果是json请求
//
//        }else{//如果是普通请求
//            re request.get
//        }
        Enumeration<String> parameterNames = request.getParameterNames();
        if(parameterNames == null){
            return null;
        }
        List<String> paramKeys = new ArrayList<>();
        while (parameterNames.hasMoreElements()){
            paramKeys.add(parameterNames.nextElement());
        }
        paramKeys.sort((a1,b1)->a1.compareTo(b1));
        StringBuilder paramValue = new StringBuilder();
        paramKeys.stream().forEach(key->{
            paramValue.append(key);
            paramValue.append(request.getParameter(key));
        });
        return null;
    }



}
