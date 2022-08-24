//package com.nbhy.modules.email.rest;
//
//import com.nbhy.annotation.Log;
//import com.nbhy.modules.email.domain.EmailConfig;
//import com.nbhy.modules.email.domain.vo.EmailVo;
//import com.nbhy.result.CommonResult;
//import com.nbhy.modules.email.service.EmailService;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.RequiredArgsConstructor;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
///**
// * 发送邮件
// * @author 郑杰
// * @date 2018/09/28 6:55:53
// */
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("api/email")
//@Api(tags = "工具：邮件管理")
//public class EmailController {
//
//    private final EmailService emailService;
//
//    @GetMapping
//    public CommonResult<Object> queryConfig(){
//        return CommonResult.success(emailService.find());
//    }
//
//    @Log("配置邮件")
//    @PutMapping
//    @ApiOperation("配置邮件")
//    public CommonResult<Object> updateConfig(@Validated @RequestBody EmailConfig emailConfig) throws Exception {
//        emailService.config(emailConfig,emailService.find());
//        return CommonResult.success();
//    }
//
//    @Log("发送邮件")
//    @PostMapping
//    @ApiOperation("发送邮件")
//    public CommonResult<Object> sendEmail(@Validated @RequestBody EmailVo emailVo){
//        emailService.send(emailVo,emailService.find());
//        return CommonResult.success();
//    }
//}
