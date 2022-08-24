package com.nbhy.modules.hik.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbhy.exception.BadRequestException;
import com.nbhy.modules.hik.constant.CarAuthConstant;
import com.nbhy.modules.hik.constant.CarConstant;
import com.nbhy.modules.hik.constant.HikDeviceConstant;
import com.nbhy.modules.hik.domain.dto.HEEmpDto;
import com.nbhy.modules.hik.domain.dto.HikCar;
import com.nbhy.modules.hik.domain.dto.HikDeviceDTO;
import com.nbhy.modules.hik.domain.dto.HikUser;
import com.nbhy.modules.hik.domain.entity.*;
import com.nbhy.modules.hik.domain.vo.CarCardUntieVO;
import com.nbhy.modules.hik.domain.vo.CarCardVO;
import com.nbhy.modules.hik.domain.vo.CarUntieVO;
import com.nbhy.modules.hik.domain.vo.CarVO;
import com.nbhy.modules.hik.mapper.*;
import com.nbhy.modules.hik.service.HikCarAuthService;
import com.nbhy.modules.hik.service.HikCarService;
import com.nbhy.modules.hik.service.HikEquipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: xcjx
 * @Email: nizhaobudaowo@163.com
 * @Company: nbhy
 * @Date: Created in 16:00 2022/3/11
 * @ClassName: HikPersonServiceImpl
 * @Description: 海康车辆
 * @Version: 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HikCarServiceImpl implements HikCarService {

    private final HikCarMapper hikCarMapper;
    private final HikPersonMapper hikPersonMapper;
    private final HikCarAuthMapper hikCarAuthMapper;

    private final HikCarAuthService hikCarAuthService;
    private final HikCardMapper hikCardMapper;

    @Lazy
    @Autowired
    private HikEquipmentService hikEquipmentService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(CarVO carVO) {
        if(carVO.getCarType() == CarConstant.INTERNAL_CAR_NUMBER){
            if(hikPersonMapper.selectById(carVO.getCarSn()) == null){
                throw new BadRequestException("车辆绑定的人员不存在");
            }
        }

        if(hikCarMapper.selectOne(Wrappers.<Car>lambdaQuery()
                .select(Car::getCarNumber)
                .eq(Car::getCarNumber,carVO.getCarNumber())) != null){
            throw new BadRequestException("车牌已经绑定，无法重新绑定");
        }

        Car car = new Car();
        BeanUtil.copyProperties(carVO, car);

        hikCarMapper.insert(car);

        Collection<String> deviceIds = null;
        //如果是下发全部权限
        if(carVO.getAuthIsAll() != null && carVO.getAuthIsAll()){
//            deviceIds = (List<String>)( Object)hikDeviceMapper.selectObjs(Wrappers.<HikDevice>lambdaQuery()
//                    .select(HikDevice::getIndexCode)
//                    .eq(HikDevice::getDeviceType, HikDeviceConstant.CAR_DEVICE));

            deviceIds = hikEquipmentService.queryAll(HikDeviceConstant.CAR_DEVICE).stream()
                    .map(HikDeviceDTO::getIndexCode)
                    .collect(Collectors.toList());
        }else{
            deviceIds = carVO.getAuths();
        }

        if(CollectionUtil.isEmpty(deviceIds)){
            //如果没有下发的权限，直接退出
            return;
        }

        //防止重复转set
        Set<String> authDeviceCodes = deviceIds.stream().collect(Collectors.toSet());


        List<HikCarAuth> hikCarAuths = authDeviceCodes.stream().map(authDeviceCode -> {
            HikCarAuth hikCarAuth = new HikCarAuth();
            hikCarAuth.setDeviceId(authDeviceCode);
            hikCarAuth.setCarId(carVO.getCarNumber());
            hikCarAuth.setCarType(CarAuthConstant.CAR_NUMBER);
            return hikCarAuth;
        }).collect(Collectors.toList());

        hikCarAuthService.saveBatch(hikCarAuths);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void untieCar(CarUntieVO carUntieVO) {
        Car car = hikCarMapper.selectById(carUntieVO.getCarNumber());
        if(car == null){
            log.info("车辆不存在");
            return;
        }

        hikCarMapper.deleteById(car.getCarNumber());

        hikCarAuthMapper.delete(Wrappers.<HikCarAuth>lambdaUpdate()
                .eq(HikCarAuth::getCarId, carUntieVO.getCarNumber())
                .eq(HikCarAuth::getCarType, CarAuthConstant.CAR_NUMBER));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)

    public void bindCarCard(CarCardVO carCardVO) {
        if(hikCardMapper.selectOne(Wrappers.<Card>lambdaQuery()
                .select(Card::getCardNumber)
                .eq(Card::getCardNumber,carCardVO.getCardNumber())) != null){
            throw new BadRequestException("车卡已经绑定，无法重新绑定");
        }

        Card card = new Card();
        BeanUtil.copyProperties(carCardVO,card);

        hikCardMapper.insert(card);

        Collection<String> deviceIds = null;
        //如果是下发全部权限
        if(carCardVO.getAuthIsAll() != null && carCardVO.getAuthIsAll()){
//            deviceIds = (List<String>)( Object)hikDeviceMapper.selectObjs(Wrappers.<HikDevice>lambdaQuery()
//                    .select(HikDevice::getIndexCode)
//                    .eq(HikDevice::getDeviceType, HikDeviceConstant.CAR_DEVICE));
            deviceIds = hikEquipmentService.queryAll(HikDeviceConstant.CAR_DEVICE).stream()
                    .map(HikDeviceDTO::getIndexCode)
                    .collect(Collectors.toList());
        }else{
            deviceIds = carCardVO.getDeviceNos();
        }

        if(CollectionUtil.isEmpty(deviceIds)){
            //如果没有下发的权限，直接退出
            return;
        }

        //防止重复转set
        Set<String> authDeviceCodes = deviceIds.stream().collect(Collectors.toSet());


        List<HikCarAuth> hikCarAuths = authDeviceCodes.stream().map(authDeviceCode -> {
            HikCarAuth hikCarAuth = new HikCarAuth();
            hikCarAuth.setDeviceId(authDeviceCode);
            hikCarAuth.setCarId(carCardVO.getCardNumber());
            hikCarAuth.setCarType(CarAuthConstant.CAR_CARD);
            return hikCarAuth;
        }).collect(Collectors.toList());

        hikCarAuthService.saveBatch(hikCarAuths);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void untieCard(CarCardUntieVO carVO) {
        Card card = hikCardMapper.selectById(carVO.getCardNumber());
        if(card == null){
            throw new BadRequestException("车卡不存在");
        }

        hikCardMapper.deleteById(card.getCardNumber());

        hikCarAuthMapper.delete(Wrappers.<HikCarAuth>lambdaUpdate()
                .eq(HikCarAuth::getCarId, carVO.getCardNumber())
                .eq(HikCarAuth::getCarType, CarAuthConstant.CAR_CARD));
    }
}

