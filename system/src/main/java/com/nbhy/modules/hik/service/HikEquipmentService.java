package com.nbhy.modules.hik.service;


import com.nbhy.modules.hik.domain.dto.EquipmentDTO;
import com.nbhy.modules.hik.domain.dto.HikDeviceDTO;
import com.nbhy.modules.hik.domain.dto.HikEquipment;

import java.util.List;
import java.util.Map;

/**
 * @Author: xcjx
 * @Email: nizhaobudaowo@163.com
 * @Company: nbhy
 * @Date: Created in 16:00 2022/3/11
 * @ClassName: HikPersonService
 * @Description: 海康设备接口文档
 * @Version: 1.0
 */
public interface HikEquipmentService {

    /**
     * 查询所有设备
     * @Param deviceType 当不传查询所有类型的设备
     * @return
     */
    List<HikDeviceDTO> queryAll(Integer deviceType);

    /**
     * 清楚设备缓存
     */
    void cleanCache();


    /**
     * 同步服务器  设备缓存
     */
    void syncServiceDevice();

    /**
     * 获取设备缓存
     * @return
     */
    Map<String, EquipmentDTO> getEquipments();

    Map<String, EquipmentDTO> getSdkEquipments();

    /**
     * 硬件sdk加载
     *
     * @return
     */
    void syncsdk();


    /**
     * 关闭netty连接
     */
    void shutdownChannel();

    /**
     * 关闭netty连接
     */
    void initializeChannel();

}
