package com.nbhy.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nbhy.domain.Log;
import com.nbhy.domain.PageQuery;
import com.nbhy.mapper.LogMapper;
import com.nbhy.service.LogService;
import com.nbhy.service.dto.LogErrorDTO;
import com.nbhy.service.dto.LogQueryCriteria;
import com.nbhy.service.mapstruct.LogErrorConverter;
import com.nbhy.service.mapstruct.LogSmallConverter;
import com.nbhy.utils.*;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {
    private static final Logger log = LoggerFactory.getLogger(LogServiceImpl.class);
    private final LogMapper logMapper;
    private final LogErrorConverter logErrorConverter;
    private final LogSmallConverter logSmallConverter;

    @Override
    public Object queryAll(LogQueryCriteria criteria, PageQuery pageable) {
        Page<Log> page = logMapper.selectPage(QueryHelp.getPage(pageable),QueryHelp.getWrappers(criteria,Log.class));
        String status = "ERROR";
        if (status.equals(criteria.getLogType())) {
            List<LogErrorDTO> logErrorDTOS = logErrorConverter.toDto(page.getRecords());
            return PageUtil.toPage(logErrorDTOS,page.getTotal(),pageable);
        }
        return PageUtil.toPage(logSmallConverter.toDto(page.getRecords()),page.getTotal(),pageable);
    }

    @Override
    public List<Log> queryAll(LogQueryCriteria criteria) {
        return logMapper.selectList(QueryHelp.getWrappers(criteria,Log.class));
    }

    @Override
    public Object queryAllByUser(LogQueryCriteria criteria, PageQuery pageable) {
        Page<Log> page = logMapper.selectPage(QueryHelp.getPage(pageable),QueryHelp.getWrappers(criteria,Log.class));
        return PageUtil.toPage(logSmallConverter.toDto(page.getRecords()),page.getTotal(),pageable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(String username, String browser, String ip, ProceedingJoinPoint joinPoint, Log log) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        com.nbhy.annotation.Log aopLog = method.getAnnotation(com.nbhy.annotation.Log.class);

        // 方法路径
        String methodName = joinPoint.getTarget().getClass().getName() + "." + signature.getName() + "()";

        StringBuilder params = new StringBuilder("{");
        //参数值
        List<Object> argValues = new ArrayList<>(Arrays.asList(joinPoint.getArgs()));
        //参数名称
        for (Object argValue : argValues) {
            params.append(argValue).append(" ");
        }
        // 描述
        if (log != null) {
            log.setDescription(aopLog.value());
        }
        assert log != null;
        log.setRequestIp(ip);

        String loginPath = "login";
        if (loginPath.equals(signature.getName())) {
            try {
                username = new JSONObject(argValues.get(0)).get("username").toString();
            } catch (Exception e) {
                LogServiceImpl.log.error(e.getMessage(), e);
            }
        }
        log.setAddress(StringUtils.getCityInfo(log.getRequestIp()));
        log.setMethod(methodName);
        log.setUsername(username);
        log.setParams(params.toString() + " }");
        log.setBrowser(browser);
        logMapper.insert(log);
    }

    @Override
    public Object findByErrDetail(Long id) {
        Log log = logMapper.selectById(id);
        ValidationUtil.isNull(log, "Log", "id", id);
        byte[] details = log.getExceptionDetail();
        return Dict.create().set("exception", new String(ObjectUtil.isNotNull(details) ? details : "".getBytes()));
    }

    @Override
    public void download(List<Log> logs, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Log log : logs) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("用户名", log.getUsername());
            map.put("IP", log.getRequestIp());
            map.put("IP来源", log.getAddress());
            map.put("描述", log.getDescription());
            map.put("浏览器", log.getBrowser());
            map.put("请求耗时/毫秒", log.getTime());
            map.put("异常详情", new String(ObjectUtil.isNotNull(log.getExceptionDetail()) ? log.getExceptionDetail() : "".getBytes()));
            map.put("创建日期", log.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delAllByError() {
        logMapper.delete(Wrappers.<Log>lambdaUpdate().eq(Log::getLogType,"ERROR"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delAllByInfo() {
        logMapper.delete(Wrappers.<Log>lambdaUpdate().eq(Log::getLogType,"INFO"));
    }
}
