package com.nbhy.modules.hik.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.nbhy.annotation.AnonymousAccess;
import com.nbhy.annotation.Log;
import com.nbhy.modules.hik.domain.dto.EquipmentDTO;
import com.nbhy.modules.hik.domain.dto.HikEquipment;
import com.nbhy.modules.hik.domain.vo.CarVO;
import com.nbhy.modules.hik.service.HikEquipmentService;
import com.nbhy.modules.hik.util.HikCallBackUtil;
import com.nbhy.modules.system.domain.entity.User;
import com.nbhy.result.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * HIK设备控制层
 *
 * @author makejava
 * @since 2021-12-08 17:17:23
 */
@Api(tags = "海康设备管理")
@RestController
@RequestMapping("/hik/equipment")
@RequiredArgsConstructor
public class HikEquipmentController {

    private final HikEquipmentService hikEquipmentService;

    @GetMapping
    @ApiOperation("查询海康设备")
    @AnonymousAccess
    @Log(value = "查询海康设备")
    @ApiImplicitParam(name = "deviceType",value = "资源类型 0代表人脸设备，1代表车辆设备 ,不传代表查询全部")
    public CommonResult query(Integer deviceType){
        return  CommonResult.success(hikEquipmentService.queryAll(deviceType),"创建海康人员成功");
    }




    @GetMapping("/clean/cache")
    @ApiOperation("清空缓存")
    @AnonymousAccess
    public CommonResult cleanCache(){
        hikEquipmentService.cleanCache();
        return  CommonResult.success(null,"创建海康人员成功");
    }


    @GetMapping("/get/eventView")
    @ApiOperation("获取订阅事件")
    @AnonymousAccess
    public CommonResult getEventView(){
        String eventView = HikCallBackUtil.getEventView();
        return  CommonResult.success(eventView,"创建海康人员成功");
    }


    @GetMapping("/netty/shutdownChannel")
    @ApiOperation("关闭netty连接")
    @AnonymousAccess
    public CommonResult shutdownChannel(){
        hikEquipmentService.shutdownChannel();
        return  CommonResult.success(null,"创建海康人员成功");
    }

    @GetMapping("/netty/initializeChannel")
    @ApiOperation("启动netty连接")
    @AnonymousAccess
    public CommonResult initializeChannel(){
        hikEquipmentService.initializeChannel();
        return  CommonResult.success(null,"创建海康人员成功");
    }
}