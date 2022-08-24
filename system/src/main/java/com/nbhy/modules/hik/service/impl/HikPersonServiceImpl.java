package com.nbhy.modules.hik.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.nbhy.exception.BadRequestException;
import com.nbhy.modules.hik.constant.*;
import com.nbhy.modules.hik.domain.dto.HEEmpDto;
import com.nbhy.modules.hik.domain.dto.HikDeviceDTO;
import com.nbhy.modules.hik.domain.dto.HikUser;
import com.nbhy.modules.hik.domain.entity.*;
import com.nbhy.modules.hik.domain.vo.*;
import com.nbhy.modules.hik.exception.HikException;
import com.nbhy.modules.hik.mapper.*;
import com.nbhy.modules.hik.service.HikEquipmentService;
import com.nbhy.modules.hik.service.HikPersonAuthService;
import com.nbhy.modules.hik.service.HikPersonService;
import com.nbhy.modules.hik.util.HaiKangTaskUtil;
import com.nbhy.modules.hik.util.HiKUserUtil;
import com.nbhy.modules.hik.util.MatcherUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: xcjx
 * @Email: nizhaobudaowo@163.com
 * @Company: nbhy
 * @Date: Created in 16:00 2022/3/11
 * @ClassName: HikPersonServiceImpl
 * @Description:
 * @Version: 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HikPersonServiceImpl implements HikPersonService {

    private final HikPersonMapper hikPersonMapper;
    private final HikPersonAuthMapper hikPersonAuthMapper;
    private final HikPersonAuthService hikPersonAuthService;
    private final HikCardMapper hikCardMapper;
    private final HikCarAuthMapper hikCarAuthMapper;
    private final HikCarMapper hikCarMapper;

    @Lazy
    @Autowired
    private HikEquipmentService hikEquipmentService;

    private final Snowflake personCertificateNo = IdUtil.getSnowflake(1, 1);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPerson(PersonVO personVO) {
        if(MatcherUtil.getpersonNameIsMatcher(personVO.getPersonName())){
            return;
        }
        if(hikPersonMapper.selectOne(Wrappers.<HikPerson>lambdaQuery()
                .select(HikPerson::getPersonId)
                .eq(HikPerson::getPersonId,personVO.getPersonId())) != null){
            log.info("人员唯一标识已存在");
            return;
        }

        //从海康获取人员信息
        JSONArray jsonArray = null;
        JSONObject json=HiKUserUtil.queryById(new String[]{personVO.getPersonId()});
        if("0".equals(json.getString("code"))){
            jsonArray = json.getJSONObject("data").getJSONArray("list");
        }
        //如果有就存数据库,否则就新建人员
        if(CollectionUtil.isEmpty(jsonArray)) {
            HikPerson hikPerson = new HikPerson();
            BeanUtil.copyProperties(personVO, hikPerson);
            //赋值随机卡号
            hikPerson.setCertificateNo(personCertificateNo.nextIdStr());

            //下发海康
            HikUser hikUser = HiKUserUtil.getHikUserByHikPerson(hikPerson, personVO.getFaceBase64Str());
            HEEmpDto heEmpDto = HiKUserUtil.create(hikUser);
            hikPerson.setFaceId(heEmpDto.getFaceId());
            //保存用户信息
            hikPersonMapper.insert(hikPerson);
        }else{
//            HikPerson hikPerson = new HikPerson();
//            BeanUtil.copyProperties(personVO, hikPerson);
//
//            //赋值随机卡号
//            hikPerson.setCertificateNo(personCertificateNo.nextIdStr());
//
//            //下发海康
//            for (int i = 0; i < jsonArray.size(); i++) {
//                String string = jsonArray.getJSONObject(i).getJSONObject("personPhoto").getString("serverIndexCode");
//                hikPerson.setFaceId(string);
//            }
//            hikPersonMapper.insert(hikPerson);

            HikPerson hikPerson = new HikPerson();
            BeanUtil.copyProperties(personVO, hikPerson);

            //赋值随机卡号
            hikPerson.setCertificateNo(personCertificateNo.nextIdStr());
            String pic="";
            try{
                pic=jsonArray.getJSONObject(0).getJSONArray("personPhoto").getJSONObject(0).getString("personPhotoIndexCode");
            }catch (Exception e){
                log.info("isc圖片不存在");
            }


//            //下发海康
//            for (int i = 0; i < jsonArray.size(); i++) {
//                //String string = jsonArray.getJSONObject(i).getJSONObject("personPhoto").getString("serverIndexCode");
//
//            }
            hikPerson.setFaceId(pic);
            hikPersonMapper.insert(hikPerson);


        }

        HikPerson hikPerson = new HikPerson();
        BeanUtil.copyProperties(personVO, hikPerson);

        List<String> deviceIds = null;
        //如果是下发全部权限
        if(personVO.getAuthIsAll() != null && personVO.getAuthIsAll()){
            deviceIds = hikEquipmentService.queryAll(HikDeviceConstant.FACE_DEVICE).stream()
                    .map(HikDeviceDTO::getIndexCode)
                    .collect(Collectors.toList());
        }else{
            deviceIds = personVO.getDeviceNos();
        }

        if(CollectionUtil.isEmpty(deviceIds)){
            //如果没有下发的权限，直接退出
            return;
        }
        for(int i=0;i<deviceIds.size();i++){
            log.info("deviceIds->>>>>"+deviceIds.get(i));
        }


        //防止重复转set
        Set<String> authDeviceCodes = deviceIds.stream().collect(Collectors.toSet());


        List<HikPersonAuth> hikPersonAuths = authDeviceCodes.stream().map(authDeviceCode -> {
            HikPersonAuth hikPersonAuth = new HikPersonAuth();
            hikPersonAuth.setDeviceId(authDeviceCode);
            hikPersonAuth.setPersonId(personVO.getPersonId());
            return hikPersonAuth;
        }).collect(Collectors.toList());

        //创建任务
        String taskId = HaiKangTaskUtil.createTasks(5);
        //向任务添加数据
        HaiKangTaskUtil.putTaskData(taskId,authDeviceCodes,personVO.getPersonId(),
                null, null,0);
        //下载任务
        HaiKangTaskUtil.downloadTask(taskId);
        hikPersonAuthService.saveBatch(hikPersonAuths);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPersonOnlyFace(PersonVO personVO) {
        if(MatcherUtil.getpersonNameIsMatcher(personVO.getPersonName())){
            return;
        }
        if(hikPersonMapper.selectOne(Wrappers.<HikPerson>lambdaQuery()
                .select(HikPerson::getPersonId)
                .eq(HikPerson::getPersonId,personVO.getPersonId())) != null){
            throw new BadRequestException("人员唯一标识已存在");
        }

        //从海康获取人员信息
        JSONArray jsonArray = null;
        JSONObject json=HiKUserUtil.queryById(new String[]{personVO.getPersonId()});
        if("0".equals(json.getString("code"))){
            jsonArray = json.getJSONObject("data").getJSONArray("list");
        }
        //如果有就存数据库,否则就新建人员
        if(CollectionUtil.isEmpty(jsonArray)) {
            HikPerson hikPerson = new HikPerson();
            BeanUtil.copyProperties(personVO, hikPerson);
            //赋值随机卡号
            hikPerson.setCertificateNo(personCertificateNo.nextIdStr());

            //下发海康
            HikUser hikUser = HiKUserUtil.getHikUserByHikPerson(hikPerson, personVO.getFaceBase64Str());
            HEEmpDto heEmpDto = HiKUserUtil.create(hikUser);
            hikPerson.setFaceId(heEmpDto.getFaceId());
            //保存用户信息
            hikPersonMapper.insert(hikPerson);
        }else{
//            HikPerson hikPerson = new HikPerson();
//            BeanUtil.copyProperties(personVO, hikPerson);
//
//            //赋值随机卡号
//            hikPerson.setCertificateNo(personCertificateNo.nextIdStr());
//
//            //下发海康
//            for (int i = 0; i < jsonArray.size(); i++) {
//                String string = jsonArray.getJSONObject(i).getJSONObject("personPhoto").getString("serverIndexCode");
//                hikPerson.setFaceId(string);
//            }
//            hikPersonMapper.insert(hikPerson);

            HikPerson hikPerson = new HikPerson();
            BeanUtil.copyProperties(personVO, hikPerson);

            //赋值随机卡号
            hikPerson.setCertificateNo(personCertificateNo.nextIdStr());
            String pic="";
            try{
                pic=jsonArray.getJSONObject(0).getJSONArray("personPhoto").getJSONObject(0).getString("personPhotoIndexCode");
            }catch (Exception e){
                log.info("isc圖片不存在");
            }


//            //下发海康
//            for (int i = 0; i < jsonArray.size(); i++) {
//                //String string = jsonArray.getJSONObject(i).getJSONObject("personPhoto").getString("serverIndexCode");
//
//            }
            hikPerson.setFaceId(pic);
            hikPersonMapper.insert(hikPerson);


        }

//        HikPerson hikPerson = new HikPerson();
//        BeanUtil.copyProperties(personVO, hikPerson);
//
//        List<String> deviceIds = null;
//        //如果是下发全部权限
//        if(personVO.getAuthIsAll() != null && personVO.getAuthIsAll()){
//            deviceIds = hikEquipmentService.queryAll(HikDeviceConstant.FACE_DEVICE).stream()
//                    .map(HikDeviceDTO::getIndexCode)
//                    .collect(Collectors.toList());
//        }else{
//            deviceIds = personVO.getDeviceNos();
//        }
//
//        if(CollectionUtil.isEmpty(deviceIds)){
//            //如果没有下发的权限，直接退出
//            return;
//        }
//        for(int i=0;i<deviceIds.size();i++){
//            log.info("deviceIds->>>>>"+deviceIds.get(i));
//        }
//
//
//        //防止重复转set
//        Set<String> authDeviceCodes = deviceIds.stream().collect(Collectors.toSet());
//
//
//        List<HikPersonAuth> hikPersonAuths = authDeviceCodes.stream().map(authDeviceCode -> {
//            HikPersonAuth hikPersonAuth = new HikPersonAuth();
//            hikPersonAuth.setDeviceId(authDeviceCode);
//            hikPersonAuth.setPersonId(personVO.getPersonId());
//            return hikPersonAuth;
//        }).collect(Collectors.toList());
//
//        //创建任务
//        String taskId = HaiKangTaskUtil.createTasks(5);
//        //向任务添加数据
//        HaiKangTaskUtil.putTaskData(taskId,authDeviceCodes,personVO.getPersonId(),
//                null, null,0);
//        //下载任务
//        HaiKangTaskUtil.downloadTask(taskId);
//        hikPersonAuthService.saveBatch(hikPersonAuths);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reCreatePerson(PersonVO personVO) {
        try {
            //先删除人员数据以及isc数据信息
            reDeleteById(personVO.getPersonId());

            //调用下发人员接口
            createPerson(personVO);

        }catch (Exception e){
            log.error(personVO.getPersonId()+ " 重新下发失败>>>>>> {}",e);
        }
        log.info("personId {} 重新下发成功",personVO.getPersonId());
    }
    @Transactional(rollbackFor = Exception.class)
    public void reDeleteById(String personId) {
        hikPersonMapper.deleteById(personId);

        //删除人员权限
        List<HikPersonAuth> hikPersonAuths = hikPersonAuthMapper.selectList(Wrappers.<HikPersonAuth>lambdaQuery()
                .eq(HikPersonAuth::getPersonId, personId));


        if(CollectionUtil.isNotEmpty(hikPersonAuths)){
            //删除权限
            hikPersonAuthMapper.delete(Wrappers.<HikPersonAuth>lambdaUpdate()
                    .eq(HikPersonAuth::getPersonId, personId));
        }

        //删除卡片
        List<Card> cards = hikCardMapper.selectList(Wrappers.<Card>lambdaQuery()
                .eq(Card::getCardNo, personId)
                .in(Card::getCardType, CardConstant.LOCATION_CARD, CardConstant.INTERNAL_TRUCK));

        if(CollectionUtil.isNotEmpty(cards)){
            cards.stream().forEach(card -> {
                //如果是车卡，删除权限
                if(card.getCardType().equals(CardConstant.INTERNAL_TRUCK)){
                    hikCarAuthMapper.delete(Wrappers.<HikCarAuth>lambdaUpdate()
                            .eq(HikCarAuth::getCarId,card.getCardNumber())
                            .eq(HikCarAuth::getCarType, CarAuthConstant.CAR_CARD));
                }
            });
            //删除卡片
            hikCardMapper.deleteBatchIds(cards.stream().map(Card::getCardNumber).collect(Collectors.toList()));
        }

        //删除车辆
        List<Car> cars = hikCarMapper.selectList(Wrappers.<Car>lambdaQuery()
                .eq(Car::getCarSn, personId)
                .eq(Car::getCarType, CarConstant.INTERNAL_CAR_NUMBER));
        if(CollectionUtil.isNotEmpty(cars)){
            hikCarAuthMapper.delete(Wrappers.<HikCarAuth>lambdaUpdate()
                    .in(HikCarAuth::getCarId,cars.stream().map(Car::getCarNumber).collect(Collectors.toList()))
                    .eq(HikCarAuth::getCarType, CarAuthConstant.CAR_NUMBER));
            //删除车辆
            hikCarMapper.deleteBatchIds(cars.stream().map(Car::getCarNumber).collect(Collectors.toList()));
        }

        try {
            //删除海康人员
            HiKUserUtil.delete(new String[]{personId});
            if(CollectionUtil.isEmpty(hikPersonAuths)){
                return;
            }
            //创建任务
            String taskId = HaiKangTaskUtil.createTasks(5);
            //向任务添加数据
            HaiKangTaskUtil.putTaskData(taskId,hikPersonAuths.stream().map(HikPersonAuth::getDeviceId).collect(Collectors.toList()),
                    personId,
                    null, null,2);
            //下载任务
            HaiKangTaskUtil.downloadTask(taskId);
        }catch (HikException e){
            log.error("删除海康信息失败>>>>>>>>>{}",e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(String personId) {
        HikPerson hikPerson = hikPersonMapper.selectById(personId);
        if(hikPerson == null){
            return;
        }
        hikPersonMapper.deleteById(personId);

        //删除人员权限
        List<HikPersonAuth> hikPersonAuths = hikPersonAuthMapper.selectList(Wrappers.<HikPersonAuth>lambdaQuery()
                .eq(HikPersonAuth::getPersonId, personId));


        if(CollectionUtil.isNotEmpty(hikPersonAuths)){
            //删除权限
            hikPersonAuthMapper.delete(Wrappers.<HikPersonAuth>lambdaUpdate()
                    .eq(HikPersonAuth::getPersonId, personId));
        }

        //删除卡片
        List<Card> cards = hikCardMapper.selectList(Wrappers.<Card>lambdaQuery()
                .eq(Card::getCardNo, personId)
                .in(Card::getCardType, CardConstant.LOCATION_CARD, CardConstant.INTERNAL_TRUCK));

        if(CollectionUtil.isNotEmpty(cards)){
            cards.stream().forEach(card -> {
                //如果是车卡，删除权限
                if(card.getCardType().equals(CardConstant.INTERNAL_TRUCK)){
                    hikCarAuthMapper.delete(Wrappers.<HikCarAuth>lambdaUpdate()
                            .eq(HikCarAuth::getCarId,card.getCardNumber())
                            .eq(HikCarAuth::getCarType, CarAuthConstant.CAR_CARD));
                }
            });
            //删除卡片
            hikCardMapper.deleteBatchIds(cards.stream().map(Card::getCardNumber).collect(Collectors.toList()));
        }

        //删除车辆
        List<Car> cars = hikCarMapper.selectList(Wrappers.<Car>lambdaQuery()
                .eq(Car::getCarSn, personId)
                .eq(Car::getCarType, CarConstant.INTERNAL_CAR_NUMBER));
        if(CollectionUtil.isNotEmpty(cars)){
            hikCarAuthMapper.delete(Wrappers.<HikCarAuth>lambdaUpdate()
                    .in(HikCarAuth::getCarId,cars.stream().map(Car::getCarNumber).collect(Collectors.toList()))
                    .eq(HikCarAuth::getCarType, CarAuthConstant.CAR_NUMBER));
            //删除车辆
            hikCarMapper.deleteBatchIds(cars.stream().map(Car::getCarNumber).collect(Collectors.toList()));
        }

        try {
            //删除海康人员
            //HiKUserUtil.delete(new String[]{personId});
            if(CollectionUtil.isEmpty(hikPersonAuths)){
                return;
            }
            //创建任务
            String taskId = HaiKangTaskUtil.createTasks(5);
            //向任务添加数据
            HaiKangTaskUtil.putTaskData(taskId,hikPersonAuths.stream().map(HikPersonAuth::getDeviceId).collect(Collectors.toList()),
                    personId,
                    null, null,2);
            //下载任务
            HaiKangTaskUtil.downloadTask(taskId);
        }catch (HikException e){
            log.error("删除海康信息失败>>>>>>>>>{}",e);
        }
    }

    @Override
    public void deleteByIds() {
        List<HikPerson> hikPersons = (List<HikPerson>)(Object)hikPersonMapper.selectList(Wrappers.<HikPerson>lambdaQuery()
                .select(HikPerson::getPersonId,HikPerson::getCreateTime)
                .eq(HikPerson::getPersonType,HikPersonConstant.VENDOR_EMPLOYEES));
        List<String> personIds = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (HikPerson hikPerson : hikPersons) {
            hikPerson.getCreateTime();
            String format = sdf.format(hikPerson.getCreateTime());
            if("2022-04-19".equals(format)){
                personIds.add(hikPerson.getPersonId());
            }
        }
//        List<String> personIds = (List<String>)(Object)hikPersonMapper.selectObjs(Wrappers.<HikPerson>lambdaQuery()
//                .select(HikPerson::getPersonId)
//                .eq(HikPerson::getPersonType,HikPersonConstant.VENDOR_EMPLOYEES));

        if(CollectionUtil.isEmpty(personIds)){
            return;
        }


        hikPersonMapper.deleteBatchIds(personIds);


        //删除权限
        hikPersonAuthMapper.delete(Wrappers.<HikPersonAuth>lambdaUpdate()
                .in(HikPersonAuth::getPersonId, personIds));

        //删除卡片
        List<Card> cards = hikCardMapper.selectList(Wrappers.<Card>lambdaQuery()
                .in(Card::getCardNo, personIds)
                .eq(Card::getCardType, CardConstant.LOCATION_CARD));

        cards.addAll(hikCardMapper.selectList(Wrappers.<Card>lambdaQuery()
                .eq(Card::getCardType, CardConstant.MANUFACTURER_S_CARD)));

        if(CollectionUtil.isNotEmpty(cards)){
            cards.stream().forEach(card -> {
                //如果是车卡，删除权限
                if(card.getCardType().equals(CardConstant.MANUFACTURER_S_CARD)){
                    hikCarAuthMapper.delete(Wrappers.<HikCarAuth>lambdaUpdate()
                            .eq(HikCarAuth::getCarId,card.getCardNumber())
                            .eq(HikCarAuth::getCarType, CarAuthConstant.CAR_CARD));
                }
            });
            //删除卡片
            hikCardMapper.deleteBatchIds(cards.stream().map(Card::getCardNumber).collect(Collectors.toList()));
        }

        //删除车辆
        List<String> carNumbers = (List<String>) (Object)hikCarMapper.selectObjs(Wrappers.<Car>lambdaQuery()
                .select(Car::getCarNumber)
                .eq(Car::getCarType, CarConstant.MANUFACTURER_CAR_NUMBER));
        if(CollectionUtil.isNotEmpty(carNumbers)){
            hikCarAuthMapper.delete(Wrappers.<HikCarAuth>lambdaUpdate()
                    .in(HikCarAuth::getCarId,carNumbers)
                    .eq(HikCarAuth::getCarType, CarAuthConstant.CAR_NUMBER));
            //删除车辆
            hikCarMapper.deleteBatchIds(carNumbers);
        }

        try {
            for (List<String> tempPersonIds : CollectionUtil.split(personIds, 500)) {
                List<HikDeviceDTO> hikDeviceDTOS = hikEquipmentService.queryAll(HikDeviceConstant.FACE_DEVICE);

                //删除海康人员
                HiKUserUtil.delete(tempPersonIds.toArray(new String[1]));
                //创建任务
                String taskId = HaiKangTaskUtil.createTasks(5);
                DateTime startTime = new DateTime();
                DateTime endTime = DateUtil.offset(startTime, DateField.YEAR,100);
                List<Map<String,Object>> personMapList = new ArrayList<>();
                for (String personId : tempPersonIds) {
                    Map<String,Object> personMap = new HashMap<>();
                    personMap.put("personId",personId);
                    personMap.put("operatorType",2);
                    personMapList.add(personMap);
                }
                //向任务添加数据
                HaiKangTaskUtil.putTaskData(taskId,hikDeviceDTOS.stream().map(HikDeviceDTO::getIndexCode).collect(Collectors.toList()),
                        personMapList,2);
                //下载任务
                HaiKangTaskUtil.downloadTask(taskId);
            }
        }catch (HikException e){
            log.error("删除海康信息失败>>>>>>>>>{}",e);
        }
    }

    @Override
    public void updateById(PersonUpdateVO personUpdateVO) {
        HikPerson hikPerson = hikPersonMapper.selectById(personUpdateVO.getPersonId());

        if(hikPerson == null){
            throw new BadRequestException("人员不存在，无法更新");
        }
        BeanUtil.copyProperties(personUpdateVO,hikPerson);

        HiKUserUtil.update(HiKUserUtil.getHikUserByHikPerson(hikPerson,null));
        hikPersonMapper.updateById(hikPerson);
    }

    @Override
    public void bindCard(CardBindVO cardBindVO) {
        Card card = hikCardMapper.selectById(cardBindVO.getCardNumber());
        if(card != null){
            throw new BadRequestException("卡号已经被绑定，无法重新绑定");
        }

        if(hikCardMapper.existCarSnAndCarType(cardBindVO.getPersonId(),CardConstant.LOCATION_CARD)){
            throw new BadRequestException("人员已经有定位卡，无法进行绑定");
        }

        HikPerson hikPerson = hikPersonMapper.selectById(cardBindVO.getPersonId());
        if(hikPerson == null){
            throw new BadRequestException("需要绑定的人员不存在");
        }
        card = new Card();

        card.setCardNumber(cardBindVO.getCardNumber());
        card.setCardNo(cardBindVO.getPersonId());
        card.setCardType(CardConstant.LOCATION_CARD);
        hikCardMapper.insert(card);
    }

    @Override
    public void untieCard(String cardNumber) {
        Card card = hikCardMapper.selectById(cardNumber);
        if(card == null){
            return;
        }
        if(card.getCardType() == CardConstant.LOCATION_CARD){
            hikCardMapper.deleteById(cardNumber);
        }
    }


    @Override
    public void issueAuth(PersonAuthVO personAuthVO) {

        if(hikPersonMapper.selectOne(Wrappers.<HikPerson>lambdaQuery()
                .select(HikPerson::getPersonId)
                .eq(HikPerson::getPersonId,personAuthVO.getPersonId())) == null){
            JSONArray jsonArray = null;
            JSONObject json=HiKUserUtil.queryById(new String[]{personAuthVO.getPersonId()});
            if("0".equals(json.getString("code"))){
                jsonArray = json.getJSONObject("data").getJSONArray("list");
            }
            //赋值随机卡号

            String pic="";
            try{
                pic=jsonArray.getJSONObject(0).getJSONArray("personPhoto").getJSONObject(0).getString("personPhotoIndexCode");
            }catch (Exception e){
                log.info("isc圖片不存在");
            }
            HikPerson hikPerson=new HikPerson();
            hikPerson.setPersonId(personAuthVO.getPersonId());
            hikPerson.setPersonName(jsonArray.getJSONObject(0).getString("personName"));
            hikPerson.setGender("0");
            hikPerson.setCertificateType("990");
            hikPerson.setJobNo(personAuthVO.getJobNo());
            hikPerson.setPersonType(personAuthVO.getPersonType());
            hikPerson.setCertificateNo(jsonArray.getJSONObject(0).getString("certificateNo"));
            hikPerson.setFaceId(pic);
            hikPersonMapper.insert(hikPerson);

        }


        /**
         * {"code":"0","msg":"success","data":{"total":1,"list":[{"personId":"410727199909152010","personName":"朱test","gender":0,"orgIndexCode":"1339f1c8-7368-413d-872c-bdb7ed52e023","certificateType":990,"certificateNo":"1557900199375015936","age":0,"marriaged":0,"lodge":0,"syncFlag":0,"pinyin":"zhutest","createTime":"2022-08-12T09:18:30.740+08:00","updateTime":"2022-08-12T09:18:30.734+08:00","faceNum":1,"fingerprintNum":0,"orgName":"内部员工","orgPath":"@root000000@1339f1c8-7368-413d-872c-bdb7ed52e023@","orgPathName":"台塑宁波/内部员工","orgList":["1339f1c8-7368-413d-872c-bdb7ed52e023"],"personPhoto":[{"serverIndexCode":"97be2da4-e5f5-40c1-9f79-7244dce30679","personPhotoIndexCode":"f985a379-e304-471d-88b4-8577c7c80f77","picUri":"/pic?0dbf00=1710ip-4eo221-269*9o0=3=2677*0l8721012166*6t6=7*3ps==815b*=144c*6e63b4233-91773c-2*l1bcod053e1=004"}]}]}}
         */

        List<HikPersonAuth> hikPersonAuths = hikPersonAuthMapper.selectList(Wrappers.<HikPersonAuth>lambdaQuery()
                .eq(HikPersonAuth::getPersonId, personAuthVO.getPersonId()));

        Collection<String> deviceIds = null;
        //如果是下发全部权限
        if(personAuthVO.getAuthIsAll() != null && personAuthVO.getAuthIsAll()){
//            deviceIds = (List<String>)( Object)hikDeviceMapper.selectObjs(Wrappers.<HikDevice>lambdaQuery()
//                    .select(HikDevice::getIndexCode)
//                    .eq(HikDevice::getDeviceType, HikDeviceConstant.FACE_DEVICE));
            deviceIds = hikEquipmentService.queryAll(HikDeviceConstant.FACE_DEVICE).stream()
                    .map(HikDeviceDTO::getIndexCode)
                    .collect(Collectors.toList());
        }else{
            deviceIds = personAuthVO.getDeviceNos();
        }
        if(CollectionUtil.isEmpty(deviceIds)){
            return;
        }

        List<String> daoDeviceIds = hikPersonAuths.stream().map(HikPersonAuth::getDeviceId).collect(Collectors.toList());

        //过滤出数据库有，但是新增的地方没有的权限。需要删除
        daoDeviceIds.removeAll(deviceIds);
        //需要删除的权限
        List<String> removeDeviceIds = daoDeviceIds;

        daoDeviceIds = hikPersonAuths.stream().map(HikPersonAuth::getDeviceId).collect(Collectors.toList());

        //过滤出传递过来有，数据库没有的数据，需要新增
        deviceIds.removeAll(daoDeviceIds);
        //需要新增的权限
        Collection<String> insertDeviceIds = deviceIds;

        if(CollectionUtil.isNotEmpty(removeDeviceIds)){
            String taskId = HaiKangTaskUtil.createTasks(5);
            DateTime startTime = new DateTime();
            DateTime endTime = DateUtil.offset(startTime, DateField.YEAR,100);
            //向任务添加数据
            HaiKangTaskUtil.putTaskData(taskId,removeDeviceIds,
                    personAuthVO.getPersonId(),
                    startTime, endTime,2);
            //下载任务
            HaiKangTaskUtil.downloadTask(taskId);

            hikPersonAuthMapper.delete(Wrappers.<HikPersonAuth>lambdaQuery()
                    .eq(HikPersonAuth::getPersonId,personAuthVO.getPersonId())
                    .in(HikPersonAuth::getDeviceId,removeDeviceIds));
        }


        if(CollectionUtil.isNotEmpty(insertDeviceIds)){
            String taskId = HaiKangTaskUtil.createTasks(5);
            DateTime startTime = new DateTime();
            DateTime endTime = DateUtil.offset(startTime, DateField.YEAR,100);
            //向任务添加数据
            HaiKangTaskUtil.putTaskData(taskId,insertDeviceIds,
                    personAuthVO.getPersonId(),
                    startTime, endTime,0);
            //下载任务
            HaiKangTaskUtil.downloadTask(taskId);

            List<HikPersonAuth> insertHikPersonAuths = insertDeviceIds.stream().map(insertDeviceId -> {
                HikPersonAuth hikPersonAuth = new HikPersonAuth();
                hikPersonAuth.setPersonId(personAuthVO.getPersonId());
                hikPersonAuth.setDeviceId(insertDeviceId);
                return hikPersonAuth;
            }).collect(Collectors.toList());

            hikPersonAuthService.saveBatch(insertHikPersonAuths);
        }
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void syncDeletePerson() {
        List<String> personIds = (List<String>)(Object)hikPersonMapper.selectObjs(Wrappers.<HikPerson>lambdaQuery()
                .select(HikPerson::getPersonId)
                .eq(HikPerson::getPersonType,HikPersonConstant.VENDOR_EMPLOYEES));

        if(CollectionUtil.isEmpty(personIds)){
            return;
        }


        hikPersonMapper.deleteBatchIds(personIds);


        //删除权限
        hikPersonAuthMapper.delete(Wrappers.<HikPersonAuth>lambdaUpdate()
                .in(HikPersonAuth::getPersonId, personIds));

        //删除卡片
        //不删除定位卡 20220723需求  AE定位卡先不删除
//        List<Card> cards = hikCardMapper.selectList(Wrappers.<Card>lambdaQuery()
//                .in(Card::getCardNo, personIds)
//                .eq(Card::getCardType, CardConstant.LOCATION_CARD));

        List<Card> cards=hikCardMapper.selectList(Wrappers.<Card>lambdaQuery()
                .eq(Card::getCardType, CardConstant.MANUFACTURER_S_CARD));

        if(CollectionUtil.isNotEmpty(cards)){
            cards.stream().forEach(card -> {
                //如果是车卡，删除权限
                if(card.getCardType().equals(CardConstant.MANUFACTURER_S_CARD)){
                    hikCarAuthMapper.delete(Wrappers.<HikCarAuth>lambdaUpdate()
                            .eq(HikCarAuth::getCarId,card.getCardNumber())
                            .eq(HikCarAuth::getCarType, CarAuthConstant.CAR_CARD));
                }
            });
            //删除卡片
            hikCardMapper.deleteBatchIds(cards.stream().map(Card::getCardNumber).collect(Collectors.toList()));
        }

        //删除车辆
        List<String> carNumbers = (List<String>) (Object)hikCarMapper.selectObjs(Wrappers.<Car>lambdaQuery()
                .select(Car::getCarNumber)
                .eq(Car::getCarType, CarConstant.MANUFACTURER_CAR_NUMBER));
        if(CollectionUtil.isNotEmpty(carNumbers)){
            hikCarAuthMapper.delete(Wrappers.<HikCarAuth>lambdaUpdate()
                    .in(HikCarAuth::getCarId,carNumbers)
                    .eq(HikCarAuth::getCarType, CarAuthConstant.CAR_NUMBER));
            //删除车辆
            hikCarMapper.deleteBatchIds(carNumbers);
        }

//        try {
//            for (List<String> tempPersonIds : CollectionUtil.split(personIds, 500)) {
//                List<HikDeviceDTO> hikDeviceDTOS = hikEquipmentService.queryAll(HikDeviceConstant.FACE_DEVICE);
//
//                //删除海康人员
//                HiKUserUtil.delete(tempPersonIds.toArray(new String[1]));
//                //创建任务
//                String taskId = HaiKangTaskUtil.createTasks(5);
//                DateTime startTime = new DateTime();
//                DateTime endTime = DateUtil.offset(startTime, DateField.YEAR,100);
//                List<Map<String,Object>> personMapList = new ArrayList<>();
//                for (String personId : tempPersonIds) {
//                    Map<String,Object> personMap = new HashMap<>();
//                    personMap.put("personId",personId);
//                    personMap.put("operatorType",2);
//                    personMapList.add(personMap);
//                }
//                //向任务添加数据
//                HaiKangTaskUtil.putTaskData(taskId,hikDeviceDTOS.stream().map(HikDeviceDTO::getIndexCode).collect(Collectors.toList()),
//                        personMapList,2);
//                //下载任务
//                HaiKangTaskUtil.downloadTask(taskId);
//            }
//        }catch (HikException e){
//            log.error("删除海康信息失败>>>>>>>>>{}",e);
//        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void issueFace(FaceVO faceVO) {
        HikPerson hikPerson = hikPersonMapper.selectById(faceVO.getPersonId());
        if(hikPerson == null){
            throw new BadRequestException("人员不存在");
        }

        if(StringUtils.isNotBlank(hikPerson.getFaceId())){
            HiKUserUtil.deleteFace(hikPerson.getFaceId());
        }

        JSONObject face1 = HiKUserUtil.createFace(faceVO.getPersonId(), faceVO.getFace());
        if(face1 == null){
            throw new BadRequestException("下发人脸失败");
        }

        hikPersonMapper.update(null,Wrappers.<HikPerson>lambdaUpdate()
                .eq(HikPerson::getPersonId,faceVO.getPersonId()).set(HikPerson::getFaceId,face1.getString("faceId")));


        List<String> deviceIds = (List<String>)(Object)hikPersonAuthMapper.selectObjs(Wrappers.<HikPersonAuth>lambdaQuery()
                .select(HikPersonAuth::getDeviceId)
                .eq(HikPersonAuth::getPersonId, faceVO.getPersonId()));
        if(CollectionUtil.isNotEmpty(deviceIds)){
            //马上下载人脸
            String taskId = HaiKangTaskUtil.createTasks(5);
            //向任务添加数据
            HaiKangTaskUtil.putTaskData(taskId,deviceIds,faceVO.getPersonId(),
                    null, null,0);
            //下载任务
            HaiKangTaskUtil.downloadTask(taskId);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void issueFaceUpdate(FaceVO faceVO) {
        HikPerson hikPerson = hikPersonMapper.selectById(faceVO.getPersonId());
        if(hikPerson == null){
            throw new BadRequestException("人员不存在");
        }

//        if(StringUtils.isNotBlank(hikPerson.getFaceId())){
//            HiKUserUtil.deleteFace(hikPerson.getFaceId());
//        }
//
//        JSONObject face1 = HiKUserUtil.createFace(faceVO.getPersonId(), faceVO.getFace());
//        if(face1 == null){
//            throw new BadRequestException("下发人脸失败");
//        }

        JSONArray jsonArray = null;
        JSONObject json=HiKUserUtil.queryById(new String[]{faceVO.getPersonId()});
        if("0".equals(json.getString("code"))){
            jsonArray = json.getJSONObject("data").getJSONArray("list");
        }
        //如果有就存数据库,否则就新建人员

//            HikPerson hikPerson = new HikPerson();
//            BeanUtil.copyProperties(personVO, hikPerson);
//
//            //赋值随机卡号
//            hikPerson.setCertificateNo(personCertificateNo.nextIdStr());
//
//            //下发海康
//            for (int i = 0; i < jsonArray.size(); i++) {
//                String string = jsonArray.getJSONObject(i).getJSONObject("personPhoto").getString("serverIndexCode");
//                hikPerson.setFaceId(string);
//            }
//            hikPersonMapper.insert(hikPerson);



            //赋值随机卡号

            String pic="";
            try{
                pic=jsonArray.getJSONObject(0).getJSONArray("personPhoto").getJSONObject(0).getString("personPhotoIndexCode");
            }catch (Exception e){
                log.info("isc圖片不存在");
            }

        if(pic.equals("")){
            log.info("isc圖片不存在");
            return;
        }else {
            JSONObject face1 = HiKUserUtil.updateFace(faceVO.getFace(), faceVO.getFace());

        hikPersonMapper.update(null,Wrappers.<HikPerson>lambdaUpdate()
                .eq(HikPerson::getPersonId,faceVO.getPersonId()).set(HikPerson::getFaceId,face1.getString("faceId")));

        }
//        List<String> deviceIds = (List<String>)(Object)hikPersonAuthMapper.selectObjs(Wrappers.<HikPersonAuth>lambdaQuery()
//                .select(HikPersonAuth::getDeviceId)
//                .eq(HikPersonAuth::getPersonId, faceVO.getPersonId()));
//        if(CollectionUtil.isNotEmpty(deviceIds)){
//            //马上下载人脸
//            String taskId = HaiKangTaskUtil.createTasks(5);
//            //向任务添加数据
//            HaiKangTaskUtil.putTaskData(taskId,deviceIds,faceVO.getPersonId(),
//                    null, null,0);
//            //下载任务
//            HaiKangTaskUtil.downloadTask(taskId);
//        }

    }


}
