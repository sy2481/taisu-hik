package com.nbhy.modules.hik.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.nbhy.modules.hik.domain.entity.Card;
import com.nbhy.modules.hik.domain.entity.HikCarAuth;
import com.nbhy.modules.hik.domain.entity.HikPerson;


public interface HikCarAuthMapper extends BaseMapper<HikCarAuth> {


    /**
     * 根据carId和卡的类型和设备查询是否拥有权限
     * @param carId
     * @param carType
     * @param deviceId
     * @return
     */
    default boolean existCarIdAndDeviceIdAndCarType(String carId,Integer carType,String deviceId){
        Integer count = this.selectCount(Wrappers.<HikCarAuth>lambdaUpdate()
                .like(HikCarAuth::getCarId, carId)
                .eq(HikCarAuth::getDeviceId,deviceId)
                .eq(HikCarAuth::getCarType,carType));
        if(count != null && count > 0){
            return true;
        }else{
            return false;
        }
    }

}
