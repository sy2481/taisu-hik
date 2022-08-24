//package com.nbhy.modules.system.rest;
//
//import com.nbhy.annotation.Log;
//import com.nbhy.annotation.RepeatSubmit;
//import com.nbhy.annotation.validation.Update;
//import com.nbhy.config.RsaProperties;
//import com.nbhy.domain.PageBean;
//import com.nbhy.domain.PageQuery;
//import com.nbhy.exception.BadRequestException;
//import com.nbhy.modules.security.config.bean.SecurityProperties;
//import com.nbhy.modules.system.domain.entity.User;
//import com.nbhy.modules.system.domain.vo.UserPassVo;
//import com.nbhy.modules.system.service.*;
//import com.nbhy.modules.system.domain.dto.UserDTO;
//import com.nbhy.modules.system.domain.query.UserQueryCriteria;
//import com.nbhy.result.CommonResult;
//import com.nbhy.result.ResultCode;
//import com.nbhy.utils.RsaUtils;
//import com.nbhy.utils.SecurityUtils;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.HashSet;
//import java.util.Set;
//
//@Api(tags = "系统：用户管理")
//@RestController
//@RequestMapping("/api/users")
//@RequiredArgsConstructor
//public class UserController {
//
//    private final PasswordEncoder passwordEncoder;
//    private final UserService userService;
//    private final SecurityProperties securityProperties;
//    private static final String ENTITY_NAME = "用户";
//
//    @Log("导出用户数据")
//    @ApiOperation("导出用户数据")
//    @GetMapping(value = "/download")
//    @PreAuthorize("@el.check('user:list')")
//    public void download(HttpServletResponse response, UserQueryCriteria criteria) throws IOException {
//        userService.download(userService.queryAll(criteria), response);
//    }
//
//    @Log("查询用户")
//    @ApiOperation("查询用户")
//    @GetMapping
//    @PreAuthorize("@el.check('user:list')")
//    @RepeatSubmit(keyType = RepeatSubmit.KeyType.PARAM,paramKey = "username")
//    public CommonResult<PageBean<UserDTO>> query(UserQueryCriteria criteria, PageQuery pageable){
//        criteria.setIdDesc(true);
//         return  CommonResult.querySuccess(userService.queryAll(criteria,pageable),ENTITY_NAME);
//    }
//
//    @Log("新增用户")
//    @ApiOperation("新增用户")
//    @PostMapping
//    @PreAuthorize("@el.check('user:add')")
//    public CommonResult<Object> create(@Validated @RequestBody User resources){
//        // 默认密码 123456
//        resources.setPassword(passwordEncoder.encode("123456"));
//        userService.create(resources);
//        return  CommonResult.insertSuccess(ENTITY_NAME);
//    }
//
//    @Log("修改用户")
//    @ApiOperation("修改用户")
//    @PutMapping
//    @PreAuthorize("@el.check('user:edit')")
//    public CommonResult<Object> update(@Validated(Update.class) @RequestBody User resources){
//        userService.update(resources);
//        return  CommonResult.updateSuccess(ENTITY_NAME);
//    }
//
//    @Log("修改用户：个人中心")
//    @ApiOperation("修改用户：个人中心")
//    @PutMapping(value = "center")
//    public CommonResult<Object> center(@RequestBody User resources){
//        if(resources.getId() == null){
//            throw new BadRequestException(ResultCode.VALIDATE_FAILED.getCode(),"id不能为空");
//        }
//        if(!resources.getId().equals(SecurityUtils.getCurrentUserId())){
//            throw new BadRequestException("不能修改他人资料");
//        }
//        userService.updateCenter(resources);
//        return  CommonResult.updateSuccess(ENTITY_NAME);
//    }
//
//    @Log("删除多个用户")
//    @ApiOperation("删除多个用户")
//    @DeleteMapping
//    @PreAuthorize("@el.check('user:del')")
//    public CommonResult<Object> delete(@RequestBody Set<Long> ids){
//        userService.delete(ids);
//        return  CommonResult.deleteSuccess(ENTITY_NAME);
//    }
//
//
//    @Log("删除用户")
//    @ApiOperation("删除用户")
//    @DeleteMapping("/{id}")
//    @PreAuthorize("@el.check('user:del')")
//    public CommonResult<Object> delete(@PathVariable("id") Long id){
//        userService.delete(new HashSet<Long>(2){{
//                add(id);
//        }});
//        return  CommonResult.deleteSuccess(ENTITY_NAME);
//    }
//
//
//    @ApiOperation("修改密码")
//    @PostMapping(value = "/updatePass")
//    public CommonResult<Object> updatePass(@RequestBody UserPassVo passVo) throws Exception {
//        String oldPass = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey,passVo.getOldPass());
//        String newPass = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey,passVo.getNewPass());
//        UserDTO user = userService.findByName(SecurityUtils.getCurrentUsername());
//        if(!passwordEncoder.matches(oldPass, user.getPassword())){
//            throw new BadRequestException("修改失败，旧密码错误");
//        }
//        if(passwordEncoder.matches(newPass, user.getPassword())){
//            throw new BadRequestException("新密码不能与旧密码相同");
//        }
//        userService.updatePass(user.getId(),passwordEncoder.encode(newPass),user.getUsername());
//        return  CommonResult.updateSuccess(ENTITY_NAME);
//    }
//
//
//}
