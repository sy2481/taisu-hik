//package com.nbhy.modules.system.rest;
//
//import com.nbhy.modules.email.domain.vo.EmailVo;
//import com.nbhy.modules.system.service.VerifyService;
//import com.nbhy.result.CommonResult;
//import com.nbhy.modules.email.service.EmailService;
//import com.nbhy.utils.enums.CodeBiEnum;
//import com.nbhy.utils.enums.CodeEnum;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Objects;
//
///**
// * @author Zheng Jie
// * @date 2018-12-26
// */
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/code")
//@Api(tags = "系统：验证码管理")
//public class VerifyController {
//
//    private final VerifyService verificationCodeService;
//    private final EmailService emailService;
//
//    @PostMapping(value = "/resetEmail")
//    @ApiOperation("重置邮箱，发送验证码")
//    public CommonResult<Object> resetEmail(@RequestParam String email){
//        EmailVo emailVo = verificationCodeService.sendEmail(email, CodeEnum.EMAIL_RESET_EMAIL_CODE.getKey());
//        emailService.send(emailVo,emailService.find());
//        return  CommonResult.success();
//    }
//
//    @PostMapping(value = "/email/resetPass")
//    @ApiOperation("重置密码，发送验证码")
//    public CommonResult<Object> resetPass(@RequestParam String email){
//        EmailVo emailVo = verificationCodeService.sendEmail(email, CodeEnum.EMAIL_RESET_PWD_CODE.getKey());
//        emailService.send(emailVo,emailService.find());
//        return  CommonResult.success();
//    }
//
//    @GetMapping(value = "/validated")
//    @ApiOperation("验证码验证")
//    public CommonResult<Object> validated(@RequestParam String email, @RequestParam String code, @RequestParam Integer codeBi){
//        CodeBiEnum biEnum = CodeBiEnum.find(codeBi);
//        switch (Objects.requireNonNull(biEnum)){
//            case ONE:
//                verificationCodeService.validated(CodeEnum.EMAIL_RESET_EMAIL_CODE.getKey() + email ,code);
//                break;
//            case TWO:
//                verificationCodeService.validated(CodeEnum.EMAIL_RESET_PWD_CODE.getKey() + email ,code);
//                break;
//            default:
//                break;
//        }
//        return  CommonResult.success();
//    }
//}
