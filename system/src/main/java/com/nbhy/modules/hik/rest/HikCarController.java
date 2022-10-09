package com.nbhy.modules.hik.rest;

import com.nbhy.annotation.AnonymousAccess;
import com.nbhy.annotation.Log;
import com.nbhy.modules.hik.domain.vo.*;
import com.nbhy.modules.hik.service.HikCarService;
import com.nbhy.result.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * HIK设备控制层
 * @author makejava
 * @since 2021-12-08 17:17:23
 */
@Api(tags = "海康车辆管理")
@RestController
@RequestMapping("/hik/car")
@RequiredArgsConstructor
public class HikCarController {

    private final HikCarService hikCarService;

    @PostMapping
    @ApiOperation("下发海康车辆")
    @AnonymousAccess
    @Log(value = "下发海康车辆")
    public CommonResult create(@RequestBody @Validated CarVO carVO){
        hikCarService.create(carVO);
        return  CommonResult.success(null,"下发海康车辆成功");
    }


    @PostMapping("/untie")
    @ApiOperation("解绑车牌")
    @AnonymousAccess
    @Log(value = "解绑车牌")
    public CommonResult untieCar(@RequestBody @Validated CarUntieVO carUntieVO){
        hikCarService.untieCar(carUntieVO);
        return  CommonResult.success(null,"解绑车牌成功");
    }


//    @PostMapping("/bind/card")
//    @ApiOperation("绑定车卡")
//    @AnonymousAccess
//    @Log(value = "绑定车卡")
//    public CommonResult bindCarCard(@RequestBody @Validated CarCardVO carCardVO){
//        hikCarService.bindCarCard(carCardVO);
//        return  CommonResult.success(null,"绑定车卡成功");
//    }
//
//
//
//    @PostMapping("/untie/card")
//    @ApiOperation("解绑车卡")
//    @AnonymousAccess
//    @Log(value = "解绑车卡")
//    public CommonResult untieCard(@RequestBody @Validated CarCardUntieVO carVO){
//        hikCarService.untieCard(carVO);
//        return  CommonResult.success(null,"解绑车卡成功");
//    }

    @PostMapping("/bind/card")
    @ApiOperation("绑定车卡")
    @AnonymousAccess
    @Log(value = "绑定车卡")
    public CommonResult bindCarCard(@RequestBody @Validated CarCardVO carCardVO){
//        hikCarService.bindCarCard(carCardVO);
        hikCarService.bindCarCardOneCardToManyPerson(carCardVO);
        return  CommonResult.success(null,"绑定车卡成功");
    }



    @PostMapping("/untie/card")
    @ApiOperation("解绑车卡")
    @AnonymousAccess
    @Log(value = "解绑车卡")
    public CommonResult untieCard(@RequestBody @Validated CarCardUntieVO carVO){
//        hikCarService.untieCard(carVO);
        hikCarService.untieCardOneCardToManyPerson(carVO);
        return  CommonResult.success(null,"解绑车卡成功");
    }

}