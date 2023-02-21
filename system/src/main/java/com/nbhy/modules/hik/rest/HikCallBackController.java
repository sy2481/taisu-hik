package com.nbhy.modules.hik.rest;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.nbhy.annotation.AnonymousAccess;
import com.nbhy.modules.hik.constant.CallBackConstant;
import com.nbhy.modules.hik.constant.RedisConstant;
import com.nbhy.modules.hik.domain.callback.CarEveData;
import com.nbhy.modules.hik.domain.callback.EventData;
import com.nbhy.modules.hik.domain.callback.Events;
import com.nbhy.modules.hik.domain.callback.HikCallBack;
import com.nbhy.modules.hik.domain.dto.EquipmentDTO;
import com.nbhy.modules.hik.domain.vo.CardBindVO;
import com.nbhy.modules.hik.domain.vo.PersonAuthVO;
import com.nbhy.modules.hik.domain.vo.PersonUpdateVO;
import com.nbhy.modules.hik.domain.vo.PersonVO;
import com.nbhy.modules.hik.service.HikCallbackService;
import com.nbhy.modules.hik.service.HikEquipmentService;
import com.nbhy.modules.hik.util.HCNetSDK;
import com.nbhy.modules.hik.util.HikCarEquipmentUtil;
import com.nbhy.result.CommonResult;
import com.nbhy.utils.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * HIK设备控制层
 * @author makejava
 * @since 2021-12-08 17:17:23
 */
@Slf4j
@Api(tags = "海康事件管理",hidden = true)
@RestController
@RequestMapping("/hik/event")
@RequiredArgsConstructor
public class HikCallBackController {
    private final RedisUtils redisUtils;

    private final HikCallbackService hikCallbackService;

    private final HikEquipmentService hikEquipmentService;

    /**
     * 订阅的事件：
     *  196893： 人脸认证通过
     *  261952: 卡加密失败，用于台塑定位卡和车卡识别
     *  197634: 无此卡号
     */
    @PostMapping("/door/callback")
    @ApiOperation(value = "海康门禁事件回调")
    @AnonymousAccess
    public void callback(@RequestBody JSONObject jsonObject){

            //log.info("收到的信息为>>>>>>>>>>>>>{}",jsonObject);
            if(CallBackConstant.ACCESS_CONTROL_EVENT_CALLBACK.equals(jsonObject.getJSONObject("params").getString("ability"))){
                HikCallBack<EventData> hikCallBack = jsonObject.toJavaObject(new TypeReference<HikCallBack<EventData>>(){});
                hikCallbackService.accessControlEvent(hikCallBack);
            }
//            });
    }



//    /**
//     * 订阅的事件：
//     *  196893： 人脸认证通过
//     *  261952: 卡加密失败，用于台塑定位卡和车卡识别
//     */
//    @PostMapping("/door/callback")
//    @ApiOperation(value = "海康门禁事件回调",hidden = true)
//    @AnonymousAccess
//    public void callback(@RequestBody JSONObject jsonObject){
//        log.info("收到的信息为>>>>>>>>>>>>>{}",jsonObject.toJSONString());
////        if(CallBackConstant.ACCESS_CONTROL_EVENT_CALLBACK.equals(callBack.getParams().getAbility())){
////            hikCallbackService.accessControlEvent(callBack);
////        }
//    }

//    /**
//     * 订阅的事件：
//     * @param callBack
//     */
//    @PostMapping("/car/callback")
////    @ApiOperation(value = "海康车辆回调事件")
//    @ApiOperation(value = "海康车辆回调事件",hidden = true)
//    @AnonymousAccess
//    public void carCallback(@RequestBody  HikCallBack<CarEveData> callBack){
////        inOutThreadPool.execute(()-> {
//            log.info("收到的信息为>>>>>>>>>>>>>{}", callBack);
//            if (CallBackConstant.CAR_EVENT_CALLBACK.equals(callBack.getParams().getAbility())) {
////            for (Events<CarEveData> event : callBack.getParams().getEvents()) {
////
////            }
//                hikCallbackService.carEvent(callBack);
//            }
////        });
//    }
    /**
     * 订阅的事件：
     *
     */
//    @PostMapping("/car/callback")
//    @ApiOperation(value = "海康车辆回调事件")
//    @ApiOperation(value = "海康车辆回调事件",hidden = true)
//    @AnonymousAccess
//    public void carCallback(@RequestBody  int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser){
//        inOutThreadPool.execute(()-> {
//        log.info("参数信息lCommand:" + lCommand);
//        if (CallBackConstant.CAR_EVENT_CALLBACK.equals(callBack.getParams().getAbility())) {
////            for (Events<CarEveData> event : callBack.getParams().getEvents()) {
////
////            }
//            hikCallbackService.carEvent(callBack);
//        }
//        });
//    }

    /**
     * 订阅的事件：
     * @param callBack
     */
    @PostMapping("/car/callback")
    @ApiOperation(value = "海康车辆回调事件")
    @AnonymousAccess
    public void carHazardousCallback(@RequestBody  HikCallBack<CarEveData> callBack){
        log.info("收到的信息为>>>>>>>>>>>>>{}",callBack);
        if(CallBackConstant.CAR_EVENT_CALLBACK.equals(callBack.getParams().getAbility())){
            hikCallbackService.carHazardousEvent(callBack);
        }
    }

}