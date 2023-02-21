package com.nbhy.modules.hik.service;

import com.nbhy.modules.hik.domain.vo.*;

/**
 * @Author: xcjx
 * @Email: nizhaobudaowo@163.com
 * @Company: nbhy
 * @Date: Created in 16:00 2022/3/11
 * @ClassName: HikPersonService
 * @Description: 海康车辆
 * @Version: 1.0
 */
public interface HikCarService {

    /**
     * 下发车辆
     * @param carVO
     */
    void create(CarVO carVO);


    /**
     * 解绑车辆
     * @param carUntieVO
     */
    void untieCar(CarUntieVO carUntieVO);

    /**
     * 解绑車卡
     * @param carUntieVO
     */
    void untieCard(CarUntieVO carUntieVO);

    /**
     * 绑定车卡
     * @param carCardVO
     */
    void bindCarCard(CarCardVO carCardVO);

    void bindCarCardOneCardToManyPerson(CarCardVO carCardVO);

    /**
     * 解绑车卡
     * @param carVO
     */
    void untieCard(CarCardUntieVO carVO);

    void untieCardOneCardToManyPerson(CarCardUntieVO carVO);

}
