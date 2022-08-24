package com.nbhy.modules.security.security;

import com.alibaba.fastjson.JSON;
import com.nbhy.result.CommonResult;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Zheng Jie
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

   @Override
   public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
      //当用户在没有授权的情况下访问受保护的REST资源时，将调用此方法发送403 Forbidden响应
//      throw new BadRequestException(HttpStatus.UNAUTHORIZED,"权限不足");
      CommonResult commonResult = CommonResult.commonResult(null, HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
      response.setCharacterEncoding("utf-8");
      response.setHeader("Content-type", "text/html; charset=utf-8");
      PrintWriter writer = response.getWriter();
      writer.write(JSON.toJSONString(commonResult));
      writer.close();
//      response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
   }
}
