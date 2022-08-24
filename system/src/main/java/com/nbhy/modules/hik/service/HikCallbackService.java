package com.nbhy.modules.hik.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nbhy.modules.hik.domain.callback.CarEveData;
import com.nbhy.modules.hik.domain.callback.EventData;
import com.nbhy.modules.hik.domain.callback.HikCallBack;
import com.nbhy.modules.hik.domain.entity.HikCarAuth;
import com.nbhy.modules.hik.util.HCNetSDK;
import com.sun.jna.Pointer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author: xcjx
 * @Email: nizhaobudaowo@163.com
 * @Company: nbhy
 * @Date: Created in 16:00 2022/3/11
 * @ClassName: HikPersonService
 * @Description: 海康车辆权限
 * @Version: 1.0
 */
public interface HikCallbackService {

    /**
     * 处理门禁事件
     * @param callBack
     */
    @Async
    void accessControlEvent(HikCallBack<EventData> callBack);

    /**
     * 处理车辆事件
     * @param dwBufLen
     * @param lCommand
     * @param pAlarmer
     * @param pAlarmInfo
     * @param pUser
     */
    @Async
//    void carEvent(HikCallBack<CarEveData> callBack);
    void carEvent(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser);

    /**
     * 处理危化品车辆事件
     * @param callBack
     */
//    @Async
    void carHazardousEvent(HikCallBack<CarEveData> callBack);

}
