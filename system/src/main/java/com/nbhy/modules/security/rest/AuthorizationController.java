//package com.nbhy.modules.security.rest;
//
//import cn.hutool.core.util.IdUtil;
//import com.nbhy.annotation.Log;
//import com.nbhy.annotation.RepeatSubmit;
//import com.nbhy.annotation.rest.AnonymousDeleteMapping;
//import com.nbhy.annotation.rest.AnonymousGetMapping;
//import com.nbhy.annotation.rest.AnonymousPostMapping;
//import com.nbhy.config.RsaProperties;
//import com.nbhy.exception.BadRequestException;
//import com.nbhy.modules.security.config.bean.LoginProperties;
//import com.nbhy.modules.security.config.bean.SecurityProperties;
//import com.nbhy.modules.security.security.TokenProvider;
//import com.nbhy.modules.security.service.OnlineUserService;
//import com.nbhy.modules.security.service.dto.AuthUserDto;
//import com.nbhy.modules.security.service.dto.JwtUserDto;
//import com.nbhy.result.CommonResult;
//import com.nbhy.utils.RedisUtils;
//import com.nbhy.utils.RsaUtils;
//import com.nbhy.utils.SecurityUtils;
//import com.nbhy.utils.StringUtils;
//import com.wf.captcha.base.Captcha;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
//@Slf4j
//@RestController
//@RequestMapping("/auth")
//@RequiredArgsConstructor
//@Api(tags = "系统：系统授权接口")
//public class AuthorizationController {
//    private final SecurityProperties properties;
//    private final RedisUtils redisUtils;
//    private final OnlineUserService onlineUserService;
//    private final TokenProvider tokenProvider;
//    private final AuthenticationManagerBuilder authenticationManagerBuilder;
//    @Resource
//    private LoginProperties loginProperties;
//
//    @Log("用户登录")
//    @ApiOperation("验证码登录授权")
//    @AnonymousPostMapping(value = "/login/code")
//    public CommonResult<Object> loginByCode(@Validated @RequestBody AuthUserDto authUser, HttpServletRequest request) throws Exception {
//        // 查询验证码
//        String code = (String) redisUtils.get(authUser.getUuid());
//        // 清除验证码
//        redisUtils.del(authUser.getUuid());
//        if (StringUtils.isBlank(code)) {
//            throw new BadRequestException("验证码不存在或已过期");
//        }
//        if (StringUtils.isBlank(authUser.getCode()) || !authUser.getCode().equalsIgnoreCase(code)) {
//            throw new BadRequestException("验证码错误");
//        }
//        return login(authUser,request);
//    }
//
//
//
//
//
//    @Log("用户登录")
//    @ApiOperation("登录授权")
//    @AnonymousPostMapping(value = "/login")
//    public CommonResult<Object> login(@Validated @RequestBody AuthUserDto authUser, HttpServletRequest request) throws Exception {
//        // 密码解密
//        String password = null;
//        try {
//            password = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, authUser.getPassword());
//        }catch (Exception e){
//            log.error("密码解密失败>>>>>>>>>>>>>>>");
//            return CommonResult.failed("用户名密码错误");
//        }
//        UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(authUser.getUsername(), password);
//        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        // 生成令牌
//        String token = tokenProvider.createToken(authentication);
//        final JwtUserDto jwtUserDto = (JwtUserDto) authentication.getPrincipal();
//        // 保存在线信息
//        onlineUserService.save(jwtUserDto, token, request);
//        // 返回 token 与 用户信息
//        Map<String, Object> authInfo = new HashMap<String, Object>(2) {{
//            put("token", properties.getTokenStartWith() + token);
//            put("user", jwtUserDto);
//        }};
//        if (loginProperties.isSingleLogin()) {
//            //踢掉之前已经登录的token
//            onlineUserService.checkLoginOnUser(authUser.getUsername(), token);
//        }
//        return CommonResult.success(authInfo);
//    }
//
//
//    @ApiOperation("测试登录授权")
//    @AnonymousPostMapping(value = "test/login")
//    public CommonResult<Object> testLogin(@Validated @RequestBody AuthUserDto authUser, HttpServletRequest request) throws Exception {
//        UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(authUser.getUsername(), authUser.getPassword());
//        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        // 生成令牌
//        String token = tokenProvider.createToken(authentication);
//        final JwtUserDto jwtUserDto = (JwtUserDto) authentication.getPrincipal();
//        // 保存在线信息
//        onlineUserService.save(jwtUserDto, token, request);
//        // 返回 token 与 用户信息
//        Map<String, Object> authInfo = new HashMap<String, Object>(2) {{
//            put("token", properties.getTokenStartWith() + token);
//            put("user", jwtUserDto);
//        }};
//        if (loginProperties.isSingleLogin()) {
//            //踢掉之前已经登录的token
//            onlineUserService.checkLoginOnUser(authUser.getUsername(), token);
//        }
//        return CommonResult.success(authInfo);
//    }
//
//
//
//    @ApiOperation("获取用户信息")
//    @GetMapping(value = "/info")
//    public CommonResult<Object> getUserInfo() {
//        return CommonResult.success(SecurityUtils.getCurrentUser());
//    }
//
//    @ApiOperation("获取验证码")
//    @AnonymousGetMapping(value = "/code")
//    public CommonResult<Object> getCode() {
//        // 获取运算的结果
//        Captcha captcha = loginProperties.getCaptcha();
//        String uuid = properties.getCodeKey() + IdUtil.simpleUUID();
//        // 保存
//        redisUtils.set(uuid, captcha.text(), loginProperties.getLoginCode().getExpiration(), TimeUnit.MINUTES);
//        // 验证码信息
//        Map<String, Object> imgResult = new HashMap<String, Object>(2) {{
//            put("img", captcha.toBase64());
//            put("uuid", uuid);
//        }};
//        return CommonResult.success(imgResult);
//    }
//
//    @ApiOperation("退出登录")
//    @AnonymousDeleteMapping(value = "/logout")
//    public CommonResult<Object> logout(HttpServletRequest request) {
//        onlineUserService.logout(tokenProvider.getToken(request));
//        return CommonResult.success();
//    }
//
//
//
//    @ApiOperation("获取公钥")
//    @AnonymousGetMapping(value = "/getPublicKey")
//    public CommonResult<Object> getPublicKey() {
//        return CommonResult.querySuccess(RsaProperties.publicKey,"查询公钥成功");
//    }
//
//
//}
