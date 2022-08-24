package com.nbhy.modules.hik.rest;

import com.nbhy.annotation.AnonymousAccess;
import com.nbhy.annotation.Log;
import com.nbhy.modules.hik.constant.HikPersonConstant;
import com.nbhy.modules.hik.domain.vo.*;
import com.nbhy.modules.hik.exception.HikException;
import com.nbhy.modules.hik.service.HikPersonService;
import com.nbhy.result.CommonResult;
import com.nbhy.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



/**
 * HIK设备控制层
 * @author makejava
 * @since 2021-12-08 17:17:23
 */
@Slf4j
@Api(tags = "海康人员管理")
@RestController
@RequestMapping("/hik/person")
@RequiredArgsConstructor
public class HikPersonController {

    private final HikPersonService hikPersonService;

    @PostMapping
    @Log(value = "下发海康人员")
    @ApiOperation("下发海康人员")
    @AnonymousAccess
    public CommonResult create(@RequestBody @Validated PersonVO personVO){
        //如果为外部员工
        if(HikPersonConstant.VENDOR_EMPLOYEES == personVO.getPersonType()){
            if(StringUtils.isEmpty(personVO.getOrderSn())){
                return CommonResult.failed("厂商编号必传");
            }
        }
        try {
            hikPersonService.createPerson(personVO);
        }catch (HikException e){
            log.error("下发海康人员失败>>>>>{}",e);
            return CommonResult.failed("下发人员失败，请重新尝试");
        }
        return  CommonResult.success(null,"创建海康人员成功");
    }

    @PostMapping("/onlyFace")
    @Log(value = "下发海康人员")
    @ApiOperation("下发海康人员")
    @AnonymousAccess
    public CommonResult createOnlyFace(@RequestBody @Validated PersonVO personVO){
        //如果为外部员工
        if(HikPersonConstant.VENDOR_EMPLOYEES == personVO.getPersonType()){
            if(StringUtils.isEmpty(personVO.getOrderSn())){
                return CommonResult.failed("厂商编号必传");
            }
        }
        try {
            hikPersonService.createPersonOnlyFace(personVO);
        }catch (HikException e){
            log.error("下发海康人员失败>>>>>{}",e);
            return CommonResult.failed("下发人员失败，请重新尝试");
        }
        return  CommonResult.success(null,"创建海康人员成功");
    }

    @PostMapping("/reCreate")
    @ApiOperation("重新下发海康人员")
    @AnonymousAccess
    @Log(value = "重新下发海康人员")
    public CommonResult reCreate(@RequestBody @Validated PersonVO personVO){
        //如果为外部员工
        if(HikPersonConstant.VENDOR_EMPLOYEES == personVO.getPersonType()){
            if(StringUtils.isEmpty(personVO.getOrderSn())){
                return CommonResult.failed("厂商编号必传");
            }
        }
        try {
            hikPersonService.reCreatePerson(personVO);
        }catch (HikException e){
            log.error("下发海康人员失败>>>>>{}",e);
            return CommonResult.failed("下发人员失败，请重新尝试");
        }
        return  CommonResult.success(null,"创建海康人员成功");
    }


    @PostMapping("/delete/{personId}")
    @ApiOperation("删除海康人员")
    @AnonymousAccess
    @Log(value = "删除海康人员")
    public CommonResult deleteById(@PathVariable("personId") String personId){
        hikPersonService.deleteById(personId);
        return  CommonResult.success(null,"删除海康人员成功");
    }

    @PostMapping("/deletes")
    @ApiOperation("批量删除海康人员")
    @AnonymousAccess
    @Log(value = "批量删除海康人员")
    public CommonResult deleteByIds(){
        hikPersonService.deleteByIds();
        return  CommonResult.success(null,"批量删除海康人员成功");
    }


    @PostMapping("/update")
    @ApiOperation("更新海康人员信息")
    @AnonymousAccess
    @Log(value = "更新海康人员信息")
    public CommonResult updateById(@RequestBody @Validated PersonUpdateVO personUpdateVO){
        hikPersonService.updateById(personUpdateVO);
        return  CommonResult.success(null,"删除海康人员成功");
    }

    @PostMapping("/issue/face")
    @ApiOperation("下发海康人脸")
    @AnonymousAccess
    @Log(value = "下发海康人脸")
    public CommonResult issueFace(@RequestBody @Validated FaceVO faceVO){
//        if(StringUtils.isEmpty(personId) || StringUtils.isEmpty(face)){
//            return CommonResult.failed("人脸和用户不能为空");
//        }
        hikPersonService.issueFace(faceVO);
        return  CommonResult.success(null,"删除海康人员成功");
    }


    @PostMapping("/issue/faceUpdate")
    @ApiOperation("下发海康人脸")
    @AnonymousAccess
    @Log(value = "下发海康人脸")
    public CommonResult issueFaceUpdate(@RequestBody @Validated FaceVO faceVO){
//        if(StringUtils.isEmpty(personId) || StringUtils.isEmpty(face)){
//            return CommonResult.failed("人脸和用户不能为空");
//        }
        hikPersonService.issueFaceUpdate(faceVO);
        return  CommonResult.success(null,"删除海康人员成功");
    }

    @PostMapping("/bind/card")
    @ApiOperation("绑定定位卡")
    @AnonymousAccess
    @Log(value = "绑定定位卡")
    public CommonResult bindCard(@RequestBody @Validated CardBindVO cardBindVO){
        hikPersonService.bindCard(cardBindVO);
        return  CommonResult.success(null,"绑定卡号成功");
    }




    @PostMapping("/untie/card/{cardNumber}")
    @ApiOperation("解绑定位卡")
    @AnonymousAccess
    @Log(value = "解绑定位卡")
    public CommonResult untieCard(@PathVariable("cardNumber") String cardNumber){
        hikPersonService.untieCard(cardNumber);
        return  CommonResult.success(null,"解绑海康卡号");
    }


    @PostMapping("/issue/auth")
    @ApiOperation("下发海康权限")
    @AnonymousAccess
    @Log(value = "下发海康权限")
    public CommonResult issueAuth(@RequestBody @Validated PersonAuthVO personAuthVO){
        log.info("下发权限");
        hikPersonService.issueAuth(personAuthVO);
        return  CommonResult.success(null,"更新海康權限");
    }


}