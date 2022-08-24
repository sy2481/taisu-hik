//package com.nbhy.modules.upload.rest;
//
//
//import com.nbhy.annotation.Log;
//import com.nbhy.exception.BadRequestException;
//import com.nbhy.modules.upload.domain.OssCallbackResult;
//import com.nbhy.modules.upload.domain.OssPolicyResult;
//import com.nbhy.modules.upload.domain.Upload;
//import com.nbhy.modules.upload.service.UploadService;
//import com.nbhy.result.CommonResult;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.servlet.http.HttpServletRequest;
//import java.io.IOException;
//
//@Api(tags = "系统：图片上传")
//@RestController
//@RequestMapping("/api/upload")
//@RequiredArgsConstructor
//public class UploadController {
//
//    private final UploadService uploadService;
//
//
//    @Log("上传图片")
//    @ApiOperation("上传图片")
//    @PostMapping
//    public CommonResult upload(MultipartFile file) throws IOException {
//        if(file.isEmpty()){
//            throw new BadRequestException("文件不能为空");
//        }
//        return CommonResult.success(uploadService.create(file));
//    }
//
//
//    @ApiOperation(value = "oss上传签名生成")
//    @RequestMapping(value = "/policy", method = RequestMethod.GET)
//    public CommonResult<OssPolicyResult> policy() {
//        OssPolicyResult result = uploadService.policy();
//        return CommonResult.success(result);
//    }
//
//
//    @ApiOperation(value = "oss上传成功回调")
//    @RequestMapping(value = "callback", method = RequestMethod.POST)
//    @ResponseBody
//    public CommonResult<OssCallbackResult> callback(HttpServletRequest request) {
//        OssCallbackResult ossCallbackResult = uploadService.callback(request);
//        return CommonResult.success(ossCallbackResult);
//    }
//
//}
