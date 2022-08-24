package com.nbhy.modules.hik.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.nbhy.modules.hik.domain.entity.Card;
import com.nbhy.modules.hik.domain.entity.HikPersonAuth;


public interface HikPersonAuthMapper extends BaseMapper<HikPersonAuth> {

    /**
     * 根据人员ID和设备id查询
     * @param personId
     * @param indexCode
     * @return
     */
    default boolean existPersonIdAndDeviceId(String personId, String indexCode){
        Integer count = this.selectCount(Wrappers.<HikPersonAuth>lambdaQuery()
                .eq(HikPersonAuth::getPersonId, personId)
                .eq(HikPersonAuth::getDeviceId,indexCode));
        if(count != null && count > 0){
            return true;
        }else{
            return false;
        }
    }

}
