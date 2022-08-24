package com.nbhy.modules.hik.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.nbhy.modules.hik.constant.CardConstant;
import com.nbhy.modules.hik.domain.dto.HikCard;
import com.nbhy.modules.hik.domain.entity.Card;
import com.nbhy.modules.hik.domain.entity.HikPerson;


public interface HikCardMapper extends BaseMapper<Card> {


    /**
     * 检查卡号是否存在
     * @param extEventCardNo
     * @return
     */
    default boolean existCardNubmer(String extEventCardNo){
        Integer count = this.selectCount(Wrappers.<Card>lambdaUpdate().eq(Card::getCardNumber, extEventCardNo));
        if(count != null && count > 0){
            return true;
        }else{
            return false;
        }
    }


    /**
     * 检查卡号是否存在
     * @param cardSo
     * @return
     */
    default boolean existCarSnAndCarType(String cardSo,Integer cardType){
        Integer count = this.selectCount(Wrappers.<Card>lambdaUpdate()
                .eq(Card::getCardNo, cardSo)
                .eq(Card::getCardType, cardType));
        if(count != null && count > 0){
            return true;
        }else{
            return false;
        }
    }


}
