package com.nbhy.modules.security.security;

import com.alibaba.fastjson.JSON;
import com.nbhy.result.CommonResult;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Zheng Jie
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // 当用户尝试访问安全的REST资源而不提供任何凭据时，将调用此方法发送401 响应
//        throw new BadRequestException(HttpStatus.UNAUTHORIZED,"您还未登录，请先登录");
//        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException==null?"Unauthorized":authException.getMessage());
        CommonResult commonResult = CommonResult.commonResult(null, HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-type", "text/html; charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(JSON.toJSONString(commonResult));
        writer.close();
    }
}
