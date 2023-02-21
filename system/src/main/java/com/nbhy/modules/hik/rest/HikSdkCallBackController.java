package com.nbhy.modules.hik.rest;


import com.nbhy.annotation.AnonymousAccess;
import com.nbhy.modules.hik.constant.CallBackConstant;
import com.nbhy.modules.hik.domain.callback.CarEveData;
import com.nbhy.modules.hik.domain.callback.HikCallBack;
import com.nbhy.modules.hik.domain.dto.SdkPlateDTO;
import com.nbhy.modules.hik.service.HikSdkCallbackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "海康事件管理",hidden = true)
@RestController
@RequestMapping("/hik/sdkevent")
public class HikSdkCallBackController {

    Logger log = LoggerFactory.getLogger(HikSdkCallBackController.class);

    @Autowired
    private HikSdkCallbackService hikSdkCallbackService;

    /**
     * 订阅的事件：
     * @param sdkPlateDTO
     */
    @PostMapping("/car/callback")
    @ApiOperation(value = "海康车辆回调事件")
    @AnonymousAccess
    public void carHazardousCallback(@RequestBody @Validated SdkPlateDTO sdkPlateDTO){
        log.info("收到的信息为>>>>>>>>>>>>>{ip}"+sdkPlateDTO.getIp()+"plate"+sdkPlateDTO.getLicense());
        hikSdkCallbackService.carHazardousEvent(sdkPlateDTO);
    }
}
