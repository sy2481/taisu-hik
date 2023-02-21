package com.nbhy.modules.hik.service;

import com.nbhy.modules.hik.domain.dto.SdkPlateDTO;


/**
 * @Author: xcjx
 * @Email: nizhaobudaowo@163.com
 * @Company: nbhy
 * @Date: Created in 16:00 2022/3/11
 * @ClassName: HikPersonService
 * @Description: 海康车辆权限
 * @Version: 1.0
 */
public interface HikSdkCallbackService {



    /**
     * 处理危化品车辆事件
     * @param sdkPlateDTO
     */
//    @Async
    void carHazardousEvent(SdkPlateDTO sdkPlateDTO);

}
