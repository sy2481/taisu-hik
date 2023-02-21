package com.nbhy.modules.hik.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.nbhy.modules.erp.config.LogConstant;
import com.nbhy.modules.erp.domain.InAndOutLog;
import com.nbhy.modules.erp.util.HazardousChemicalsUtil;
import com.nbhy.modules.erp.util.LogUtil;
import com.nbhy.modules.erp.util.ManufacturerUtil;
import com.nbhy.modules.erp.util.PersonCardUtil;
import com.nbhy.modules.hik.constant.*;
import com.nbhy.modules.hik.domain.callback.CarEveData;
import com.nbhy.modules.hik.domain.callback.EventData;
import com.nbhy.modules.hik.domain.callback.Events;
import com.nbhy.modules.hik.domain.callback.HikCallBack;
import com.nbhy.modules.hik.domain.dto.EquipmentDTO;
import com.nbhy.modules.hik.domain.dto.SdkPlateDTO;
import com.nbhy.modules.hik.domain.entity.Car;
import com.nbhy.modules.hik.domain.entity.Card;
import com.nbhy.modules.hik.domain.entity.HikPerson;
import com.nbhy.modules.hik.domain.vo.CarPerilousVO;
import com.nbhy.modules.hik.mapper.*;
import com.nbhy.modules.hik.service.HikCallbackService;
import com.nbhy.modules.hik.service.HikEquipmentService;
import com.nbhy.modules.hik.service.HikSdkCallbackService;
import com.nbhy.modules.hik.util.HCNetSDK;
import com.nbhy.modules.hik.util.HikCaptureShowUtil;
import com.nbhy.modules.hik.util.HikCaptureVoiceUtil;
import com.nbhy.modules.hik.util.HikCarEquipmentUtil;
import com.nbhy.modules.plc.client.PlcClient;
import com.nbhy.modules.plc.client.PlcMessageSocket;
import com.nbhy.modules.plc.client.PlcSocket;
import com.nbhy.modules.system.service.SysConfigService;
import com.nbhy.utils.RedisUtils;
import com.nbhy.utils.StringUtils;
import com.sun.jna.Pointer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: xcjx
 * @Email: nizhaobudaowo@163.com
 * @Company: nbhy
 * @Date: Created in 16:45 2022/3/12
 * @ClassName: HikCallbackServiceImpl
 * @Description: 海康回调处理类
 * @Version: 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HikSdkCallbackServiceImpl implements HikSdkCallbackService {


    private final HikEquipmentService hikEquipmentService;
    private final RedisUtils redisUtils;
    private final HikCardMapper hikCardMapper;
    private final HikPersonMapper hikPersonMapper;
    private final HikPersonAuthMapper hikPersonAuthMapper;
    private final HikCarMapper hikCarMapper;
    private final HikCarAuthMapper hikCarAuthMapper;
    private final PlcClient plcClient;
    private final SysConfigService sysConfigService;
    private final ThreadPoolExecutor inOutThreadPool = new ThreadPoolExecutor(5, 15, 5000, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1000), Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());


    @Override
    public void carHazardousEvent(SdkPlateDTO sdkPlateDTO) {

        String carNumberInitial = sdkPlateDTO.getLicense();
        String carNumber = sdkPlateDTO.getLicense().trim().substring(1);
        log.info("carNumber" + carNumber);

        Map<String, EquipmentDTO> sdkEquipmentMap = hikEquipmentService.getSdkEquipments();
        EquipmentDTO equipmentDTO = sdkEquipmentMap.get(sdkPlateDTO.getIp());
        if (equipmentDTO == null) {
            log.error("查询不到此设备");
            return;
        }
        String deviceId = equipmentDTO.getIndexCode();



        if (StringUtils.isEmpty(equipmentDTO.getBindIndexCode())) {
            log.error("道闸未绑定人脸设备");
            return;
        }

//        Car car = hikCarMapper.selectById(sdkPlateDTO.getLicense());
//        if (car == null) {
//            log.info("車牌不存在{}", carNumber + " IP-->" + equipmentDTO.getSubtitleMachineIp());
//            //HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(deviceId, carNumber + " 此車牌不存在 ");
//            HikCaptureShowUtil.showSubtitle(equipmentDTO.getIp(), carNumberInitial + " 車輛未核卡 ", carNumberInitial, equipmentDTO.getSubtitleMachineIp());
//            return;
//        }
        List<Car> cars = hikCarMapper.selectList(Wrappers.<Car>lambdaQuery()
                .like(Car::getCarNumber, carNumber));
        if (!CollectionUtil.isNotEmpty(cars)) {
            log.info("車牌不存在{}", carNumber);
            HikCaptureShowUtil.showSubtitle(equipmentDTO.getIp(), carNumberInitial + "車牌不存在", carNumberInitial, equipmentDTO.getSubtitleMachineIp());
            //HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(deviceId, carNumber + " 此車牌不存在 ");
            return;
        }

        if (!hikCarAuthMapper.existCarIdAndDeviceIdAndCarType(carNumber, CarAuthConstant.CAR_NUMBER, equipmentDTO.getIndexCode())) {
            log.info("您沒有進出此門崗的權限{}", carNumber);

            HikCaptureShowUtil.showSubtitle(equipmentDTO.getIp(), carNumberInitial + "車輛沒有進出門崗的權限", carNumberInitial, equipmentDTO.getSubtitleMachineIp());
//            HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(deviceId, carNumber + " 您沒有進出此門崗的權限");
            return;
        }

        log.info("carNumberCache  start");
        String carNumberCache = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getBindIndexCode(), RedisConstant.IN_AND_OUT_KEY_CAR);
        //如果发现扫描的车牌和缓存一致，不做处理
        log.info("carNumberCache--->" + carNumberCache);

        if (StringUtils.isNotEmpty(carNumberCache) && carNumber.contains(carNumberCache)) {
            return;
        }

//            清除之前的缓存
        redisUtils.del(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getBindIndexCode());
//            boolean b = HazardousChemicalsUtil.carExists(carNumber);
//            if(b){
//                log.info("危化品车开门");
//                plcClient.openDoor(equipmentDTO.getPlcIp(),equipmentDTO.getPlcCommand());
////                drivewayOpen(equipmentDTO,carNumber,null);
//            }else{
//                log.info("字幕机提示,看服务端返回的信息，提示为什么不能开门");
//                plcClient.sendMes(equipmentDTO.getSubtitleMachineIp(),"没查到车牌");
//            }
//            设置车卡
        log.info("redisUtils.del");
        redisUtils.hset(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getBindIndexCode(), RedisConstant.IN_AND_OUT_KEY_CAR, carNumberInitial);
//            设置人员类型
//            如果是内部员工
        Car car = cars.get(0);
        log.info("car.getCarType()" + car.getCarType());
        if (CarConstant.INTERNAL_CAR_NUMBER == car.getCarType()) {
            redisUtils.hset(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getBindIndexCode(), RedisConstant.IN_AND_OUT_CAR_PERSON_TYPE, HikPersonConstant.INTERNAL_STAFF);
        } else if (CarConstant.MANUFACTURER_CAR_NUMBER == car.getCarType()) {
            redisUtils.hset(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getBindIndexCode(), RedisConstant.IN_AND_OUT_CAR_PERSON_TYPE, HikPersonConstant.VENDOR_EMPLOYEES);
        } else if (CarConstant.HAZARDOUS_CAR_NUMBER == car.getCarType()) {
            //redisUtils.hset(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getBindIndexCode(), RedisConstant.IN_AND_OUT_CAR_PERSON_TYPE, HikPersonConstant.PERILOUS_EMPLOYEES);
            String showIp = "";
            String captureIp = "";
            if (equipmentDTO.getDeviceType().equals(HikDeviceConstant.CAR_DEVICE)) {
                captureIp = equipmentDTO.getIp();
                showIp = equipmentDTO.getSubtitleMachineIp();
            } else {
                EquipmentDTO carEquipmentDTO = hikEquipmentService.getEquipments().get(equipmentDTO.getBindIndexCode());
                captureIp = carEquipmentDTO.getIp();
                showIp = carEquipmentDTO.getSubtitleMachineIp();
            }

            CarPerilousVO carPerilousVO = new CarPerilousVO();
            carPerilousVO.setCarNo(carNumberInitial);
            carPerilousVO.setCheckingType(equipmentDTO.getControlJSONBo().getDanger());
            carPerilousVO.setInOutType(equipmentDTO.getSign());
            carPerilousVO.setIp(equipmentDTO.getIp());
            //carPerilousVO.setCarNo(carNumber);
//            carPerilousVO.setCarNo(carNumber);
            log.info("carPerilousVO create success");
            String errorMsg = HazardousChemicalsUtil.carExists(carPerilousVO);
            if (errorMsg == null) {
                log.info("危化品员工校验成功,开门");
//                        humaneDoor(equipmentDTO, hikPerson, null);
                drivewayOpen(equipmentDTO, null, null);
                HikCaptureShowUtil.showSubtitle(captureIp, "危化品車牌: " + carNumberInitial + " 請盡速通行 ", carNumberInitial, showIp);
            } else {
                log.info("字幕机提示,看服务端返回的信息，提示为什么不能开门" + errorMsg);
                //plcClient.sendMes(equipmentDTO.getSubtitleMachineIp(), errorMsg);
                //HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), errorMsg);
            }
            return;
        }
        List<String> carSnlist = new ArrayList<>();
        String carSn = car.getCarSn();
        carSnlist.add(carSn);
//            设置车辆唯一值
        redisUtils.hset(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getBindIndexCode(), RedisConstant.IN_AND_OUT_KEY_CAR_SN, carSnlist);


//            设置车辆唯一值
//            redisUtils.hset(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getBindIndexCode(), RedisConstant.IN_AND_OUT_KEY_CAR_SN, car.getCarSn());
//            设置过期时间
        redisUtils.expire(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getBindIndexCode(), RedisConstant.IN_AND_OUT_CAR_KEY_VALIDITY_PERIOD, TimeUnit.SECONDS);
        //HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), "車牌號碼: " + carNumber + " 請下車刷人臉");
        HikCaptureShowUtil.showSubtitle(equipmentDTO.getIp(), "車牌號碼: " + carNumberInitial + " 請下車刷人臉 ", carNumberInitial, equipmentDTO.getSubtitleMachineIp());
        HikCaptureVoiceUtil.showVoice(equipmentDTO.getIp(),"車牌號碼: " + carNumberInitial + " 請下車刷人臉 ");
    }


    /**
     * 人脸校验通过事件
     *
     * @param event 事件内容
     */
    private void faceVerificationPassedEvent(Events<EventData> event, EquipmentDTO equipmentDTO) {
        log.info("77777777");
        log.info("equipmentDTO.getDeviceAttribute()++++" + equipmentDTO.getDeviceAttribute());
        switch (equipmentDTO.getDeviceAttribute()) {
            case HikDeviceConstant.ORDINARY_EQUIPMENT: {
                ordinaryDeviceToBrushFace(event, equipmentDTO);
                break;
            }
            case HikDeviceConstant.BIND_THE_POSITIONING_CARD_DEVICE: {
                bindingLocationCardDeviceToSwipeFace(event, equipmentDTO);
                break;
            }
            case HikDeviceConstant.LANE_BOUND_FACE_DEVICE:
//                drivewayBrushFace(event, equipmentDTO);
                drivewayBrushFaceOneCardToManyPerson(event, equipmentDTO);
                break;
            default:
                log.error("不存在的设备属性");
        }
    }


    /**
     * 卡加密失败事件
     *
     * @param event        事件内容
     * @param equipmentDTO 设备信息
     */
    private void cardEncryptionFailureEvent(Events<EventData> event, EquipmentDTO equipmentDTO) {
        switch (equipmentDTO.getDeviceAttribute()) {
            case HikDeviceConstant.ORDINARY_EQUIPMENT: {
                ordinaryDeviceSwipeCard(event, equipmentDTO);
                break;
            }
            case HikDeviceConstant.BIND_THE_POSITIONING_CARD_DEVICE: {
                bindTheLocationCardDeviceToSwipeTheCard(event, equipmentDTO);
                break;
            }

            case HikDeviceConstant.LANE_BOUND_FACE_DEVICE: {
//                drivewaySwipe(event, equipmentDTO);
                drivewaySwipeOneCardToManyPerson(event, equipmentDTO);
                break;
            }
            default:
                log.error("不存在的设备属性");
        }
    }


    /**
     * 普通设备刷脸
     *
     * @param event        时间数据
     * @param equipmentDTO 时间发生的设备
     */
    private void ordinaryDeviceToBrushFace(Events<EventData> event, EquipmentDTO equipmentDTO) {
        log.info("88888888");
        HikPerson hikPerson = hikPersonMapper.selectById(event.getData().getExtEventPersonNo());
        if (hikPerson == null) {
            log.info("字幕机提示没有，系统没有这个人");
            PlcMessageSocket.sentMessage(equipmentDTO.getSubtitleMachineIp(), "本日未核卡");

//            plcClient.sendMes(equipmentDTO.getSubtitleMachineIp(),"查無此人");
            return;
        }
        //判断用户是否拥有权限
        if (!hikPersonAuthMapper.existPersonIdAndDeviceId(event.getData().getExtEventPersonNo(), equipmentDTO.getIndexCode())) {
            log.info("字母机提示没有权限");
            PlcMessageSocket.sentMessage(equipmentDTO.getSubtitleMachineIp(), "本日未核卡 請聯繫管理員");
//            plcClient.sendMes(equipmentDTO.getSubtitleMachineIp(),"沒有權限");
            return;
        }

        //如果不使用定位卡，直接开门
//        if(!sysConfigService.getConfig().getLocationCardEnabled()){
        String humane = equipmentDTO.getControlJSONBo().getHumane();
        log.info("门禁方式人道:人脸(face),定位卡(locationCard)+++" + humane);
        if (humane.indexOf("face") >= 0 && humane.indexOf("locationCard") < 0) {
            if (HikPersonConstant.INTERNAL_STAFF == hikPerson.getPersonType()) {
                log.info("校验成功,开门");
                humaneDoor(equipmentDTO, hikPerson, null);
            } else if (HikPersonConstant.VENDOR_EMPLOYEES == hikPerson.getPersonType()) {
                //如果是厂商员工
                if (hikPerson.getOrderSn().startsWith("1")) {
                    log.info("厂商员工校验成功,开门");
                    humaneDoor(equipmentDTO, hikPerson, null);
                } else {
                    String errorMsg = ManufacturerUtil.isItPossibleToGetInAndOut(HikDeviceConstant.IN_OR_OUT_IDENTIFICATION.get(equipmentDTO.getSign()), hikPerson.getPersonId());
                    if (errorMsg == null) {
                        log.info("厂商员工校验成功,开门");
                        humaneDoor(equipmentDTO, hikPerson, null);
                    } else {
                        log.info("字幕机提示,看服务端返回的信息，提示为什么不能开门");
                        PlcMessageSocket.sentMessage(equipmentDTO.getSubtitleMachineIp(), errorMsg);
//                        plcClient.sendMes(equipmentDTO.getSubtitleMachineIp(),errorMsg);
                    }
                }
            } else if (HikPersonConstant.PERILOUS_EMPLOYEES == hikPerson.getPersonType()) {
                String carNo = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_KEY_CAR);
                CarPerilousVO carPerilousVO = new CarPerilousVO();
                carPerilousVO.setCarNo(carNo);
                carPerilousVO.setInOutType(equipmentDTO.getSign());
                carPerilousVO.setIp(equipmentDTO.getIp());
                carPerilousVO.setCheckingType(equipmentDTO.getControlJSONBo().getDanger());
                carPerilousVO.setIdCard(hikPerson.getPersonId());
                String errorMsg = HazardousChemicalsUtil.carExists(carPerilousVO);
                if (errorMsg == null) {
                    log.info("危化品员工校验成功,开门");
                    humaneDoor(equipmentDTO, hikPerson, null);
                } else {
                    log.info("字幕机提示,看服务端返回的信息，提示为什么不能开门");
                    PlcMessageSocket.sentMessage(equipmentDTO.getSubtitleMachineIp(), errorMsg);
//                    plcClient.sendMes(equipmentDTO.getSubtitleMachineIp(), errorMsg);
                }
//
            }
            return;
        }

        if ("9a2306cff7514a9aa8e0d643536a11d2".equals(equipmentDTO.getIndexCode())) {
            if (HikPersonConstant.INTERNAL_STAFF == hikPerson.getPersonType()) {
                log.info("校验成功,开门");
                humaneDoor(equipmentDTO, hikPerson, null);
            } else if (HikPersonConstant.VENDOR_EMPLOYEES == hikPerson.getPersonType()) {
                //如果是厂商员工
                if (hikPerson.getOrderSn().startsWith("1")) {
                    log.info("厂商员工校验成功,开门");
                    humaneDoor(equipmentDTO, hikPerson, null);
                } else {
                    String errorMsg = ManufacturerUtil.isItPossibleToGetInAndOut(HikDeviceConstant.IN_OR_OUT_IDENTIFICATION.get(equipmentDTO.getSign()), hikPerson.getPersonId());
                    if (errorMsg == null) {
                        log.info("厂商员工校验成功,开门");
                        humaneDoor(equipmentDTO, hikPerson, null);
                    } else {
                        log.info("字幕机提示,看服务端返回的信息，提示为什么不能开门");
                        PlcMessageSocket.sentMessage(equipmentDTO.getSubtitleMachineIp(), errorMsg);
//                        plcClient.sendMes(equipmentDTO.getSubtitleMachineIp(),errorMsg);
                    }
                }
            }
            return;
        }

        //设置刷脸的人员id
        redisUtils.hset(RedisConstant.IN_AND_OUT_PERSON_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_PERSON_FACE,
                hikPerson.getPersonId());
        //设置刷脸的人员类型
        redisUtils.hset(RedisConstant.IN_AND_OUT_PERSON_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_PERSON_PERSON_TYPE,
                hikPerson.getPersonType());
        //设置过期时间
        redisUtils.expire(RedisConstant.IN_AND_OUT_PERSON_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_PERSON_KEY_VALIDITY_PERIOD, TimeUnit.SECONDS);
        log.info("字母机提示，刷定位卡");
        PlcMessageSocket.sentMessage(equipmentDTO.getSubtitleMachineIp(), "請刷定位卡");
//        plcClient.sendMes(equipmentDTO.getSubtitleMachineIp(),"請刷定位卡");
    }


    /**
     * 普通设备刷卡
     *
     * @param event        时间数据
     * @param equipmentDTO 时间发生的设备
     */
    private void ordinaryDeviceSwipeCard(Events<EventData> event, EquipmentDTO equipmentDTO) {
        boolean b = PersonCardUtil.guestCardCheck(event.getData().getExtEventCardNo(), equipmentDTO.getIp(), equipmentDTO.getSign());
        if (b) {
            PlcSocket.sentMessage(equipmentDTO.getPlcIp(), equipmentDTO.getPlcCommand());
            PlcMessageSocket.sentMessage(equipmentDTO.getSubtitleMachineIp(), "开门");
            return;
        }
        Card card = hikCardMapper.selectById(event.getData().getExtEventCardNo());
        if (card == null) {
            log.info("字幕机提示卡号不存在");
            PlcMessageSocket.sentMessage(equipmentDTO.getSubtitleMachineIp(), "卡號不存在");
//            plcClient.sendMes(equipmentDTO.getSubtitleMachineIp(),"卡號不存在");
            return;
        }
        if (card.getCardType() != CardConstant.LOCATION_CARD) {
            log.info("字幕机提示只能刷定位卡");
            PlcMessageSocket.sentMessage(equipmentDTO.getSubtitleMachineIp(), "無效卡");
//            plcClient.sendMes(equipmentDTO.getSubtitleMachineIp(),"無效卡");
            return;
        }
        String personId = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_PERSON_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_PERSON_FACE);

        //如果还没有人脸缓存，提示先刷人脸
        if (StringUtils.isEmpty(personId)) {
            log.info("提示先刷人脸");
            PlcMessageSocket.sentMessage(equipmentDTO.getSubtitleMachineIp(), "請刷人臉");
//            plcClient.sendMes(equipmentDTO.getSubtitleMachineIp(),"請刷人臉");
            return;
        }

        //校验通过
        if (personId.equals(card.getCardNo())) {
            Integer personType = (Integer) redisUtils.hget(RedisConstant.IN_AND_OUT_PERSON_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_PERSON_PERSON_TYPE);
            //清除缓存
            redisUtils.del(RedisConstant.IN_AND_OUT_PERSON_KEY + equipmentDTO.getIndexCode());
            HikPerson hikPerson = hikPersonMapper.selectById(personId);
            //如果是内部员工
            if (HikPersonConstant.INTERNAL_STAFF == personType) {
                log.info("校验成功,开门");
                humaneDoor(equipmentDTO, hikPerson, card);
            } else if (HikPersonConstant.VENDOR_EMPLOYEES == personType) {
                //如果是厂商员工
                if (hikPerson.getOrderSn().startsWith("1")) {
                    log.info("厂商员工校验成功,开门");
                    humaneDoor(equipmentDTO, hikPerson, null);
                } else {
                    String errorMsg = ManufacturerUtil.isItPossibleToGetInAndOut(HikDeviceConstant.IN_OR_OUT_IDENTIFICATION.get(equipmentDTO.getSign()), personId);
                    if (errorMsg == null) {
                        log.info("厂商员工校验成功,开门");
                        humaneDoor(equipmentDTO, hikPerson, card);
                    } else {
                        log.info("字幕机提示,看服务端返回的信息，提示为什么不能开门");
                        PlcMessageSocket.sentMessage(equipmentDTO.getSubtitleMachineIp(), errorMsg);
//                        plcClient.sendMes(equipmentDTO.getSubtitleMachineIp(),errorMsg);
                    }
                }
            } else if (HikPersonConstant.PERILOUS_EMPLOYEES == personType) {
                //如果是危化品员工
                String carNo = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_KEY_CAR);
                CarPerilousVO carPerilousVO = new CarPerilousVO();
                carPerilousVO.setCarNo(carNo);
                carPerilousVO.setInOutType(equipmentDTO.getSign());
                carPerilousVO.setIp(equipmentDTO.getIp());
                carPerilousVO.setCheckingType(equipmentDTO.getControlJSONBo().getDanger());
                carPerilousVO.setIdCard(hikPerson.getPersonId());
                String errorMsg = HazardousChemicalsUtil.carExists(carPerilousVO);
                if (errorMsg == null) {
                    log.info("危化品员工校验成功,开门");
                    humaneDoor(equipmentDTO, hikPerson, null);
                } else {
                    log.info("字幕机提示,看服务端返回的信息，提示为什么不能开门");
                    PlcMessageSocket.sentMessage(equipmentDTO.getSubtitleMachineIp(), errorMsg);
//                    plcClient.sendMes(equipmentDTO.getSubtitleMachineIp(), errorMsg);
                }
            }
        } else {
            log.info("提示人卡不匹配");
            PlcMessageSocket.sentMessage(equipmentDTO.getSubtitleMachineIp(), "人卡不匹配");
//            plcClient.sendMes(equipmentDTO.getSubtitleMachineIp(),"人卡不匹配");
        }

    }


    /**
     * 绑定定位卡设备刷脸
     *
     * @param event        时间数据
     * @param equipmentDTO 时间发生的设备
     */
    private void bindingLocationCardDeviceToSwipeFace(Events<EventData> event, EquipmentDTO equipmentDTO) {
        EventData data = event.getData();
        if (hikCardMapper.existCarSnAndCarType(data.getExtEventPersonNo(), CardConstant.LOCATION_CARD)) {
            log.info("提示用于已经绑定卡号，无法重新绑定");
            PlcMessageSocket.sentMessage(equipmentDTO.getSubtitleMachineIp(), "卡號已綁定");
//            plcClient.sendMes(equipmentDTO.getSubtitleMachineIp(),"卡號已綁定");
            return;
        }

        HikPerson hikPerson = hikPersonMapper.selectById(data.getExtEventPersonNo());
        if (hikPerson == null) {
            log.info("人员资料不存在>>>>>>>>>>>>{}", data.getExtEventPersonNo());
            PlcMessageSocket.sentMessage(equipmentDTO.getSubtitleMachineIp(), "人員不存在");
//            plcClient.sendMes(equipmentDTO.getSubtitleMachineIp(),"人員不存在");
            return;
        }

        if (hikPerson.getPersonType() != HikPersonConstant.VENDOR_EMPLOYEES) {
            log.info("只能用于厂商人员绑卡>>>>>>>>>>>>{}", data.getExtEventPersonNo());
            PlcMessageSocket.sentMessage(equipmentDTO.getSubtitleMachineIp(), "非廠商人員");
//            plcClient.sendMes(equipmentDTO.getSubtitleMachineIp(),"非廠商人員");
            return;
        }

        //存储人员编码 key是关键字+设备编码
        redisUtils.set(RedisConstant.MANUFACTURER_BOUND_POSITIONING_CARD_KEY + equipmentDTO.getIndexCode(),
                data.getExtEventPersonNo(), RedisConstant.MANUFACTURER_BOUND_POSITIONING_CARD_TIME, TimeUnit.SECONDS);
    }

    /**
     * 绑定定位卡设备刷卡
     *
     * @param event        时间数据
     * @param equipmentDTO 时间发生的设备
     */
    private void bindTheLocationCardDeviceToSwipeTheCard(Events<EventData> event, EquipmentDTO equipmentDTO) {
        //获取卡号
        String extEventCardNo = event.getData().getExtEventCardNo();
        Card cardDao = hikCardMapper.selectById(extEventCardNo);
        //如果卡号已经存在，解绑
        if (cardDao != null) {
            if (cardDao.getCardType() != CardConstant.LOCATION_CARD) {
                log.info("只能刷定位卡>>>>{}", extEventCardNo);
                //PlcMessageSocket.sentMessage(equipmentDTO.getSubtitleMachineIp(), "無效卡");
                PlcMessageSocket.sentMessage("192.168.70.201", "無效卡");
//                plcClient.sendMes(equipmentDTO.getSubtitleMachineIp(),"無效卡");
                return;
            }
            HikPerson hikPerson = hikPersonMapper.selectById(cardDao.getCardNo());
            if (hikPerson != null) {
                if (hikPerson.getPersonType() == HikPersonConstant.INTERNAL_STAFF) {
                    log.info("内部员工卡，无法解绑>>>>{}", extEventCardNo);
//                PlcMessageSocket.sentMessage(equipmentDTO.getSubtitleMachineIp(), "請後臺解綁");
                    PlcMessageSocket.sentMessage("192.168.70.201", "請後臺解綁");
//                plcClient.sendMes(equipmentDTO.getSubtitleMachineIp(),"請後臺解綁");
                    return;
                }
                //解绑
                PersonCardUtil.locationCardUnbind(extEventCardNo);
                hikCardMapper.deleteById(extEventCardNo);
                log.info("提示用户卡已经解绑>>>{}", extEventCardNo);
//            PlcMessageSocket.sentMessage(equipmentDTO.getSubtitleMachineIp(), "已解綁");
                PlcMessageSocket.sentMessage("192.168.70.201", hikPerson.getPersonName() + "解綁");
//                plcClient.sendMes(equipmentDTO.getSubtitleMachineIp(),"已解綁");
            } else {
                //第二天未核卡 來做解綁操作
                PersonCardUtil.locationCardUnbind(extEventCardNo);
                hikCardMapper.deleteById(extEventCardNo);
                log.info("提示用户卡已经解绑>>>{}", extEventCardNo);
                PlcMessageSocket.sentMessage("192.168.70.201", "解綁成功");
            }
        } else {
            String personId = (String) redisUtils.get(RedisConstant.MANUFACTURER_BOUND_POSITIONING_CARD_KEY + equipmentDTO.getIndexCode());
            if (StringUtils.isEmpty(personId)) {
                log.info("字幕机提示先刷人脸");
                PlcMessageSocket.sentMessage("192.168.70.201", "先刷人臉");
//                plcClient.sendMes(equipmentDTO.getSubtitleMachineIp(),"先刷人臉");
                return;
            }
            //确定之后删除key
            redisUtils.del(RedisConstant.MANUFACTURER_BOUND_POSITIONING_CARD_KEY + equipmentDTO.getIndexCode());
            //定位卡进行绑定
            Card card = new Card();
            card.setCardNo(personId);
            card.setCardNumber(extEventCardNo);
            card.setCardType(CardConstant.LOCATION_CARD);
            hikCardMapper.insert(card);
            PersonCardUtil.locationCardBind(personId, extEventCardNo);
            HikPerson hikPerson = hikPersonMapper.selectById(personId);
            String personName = null;
            if (hikPerson != null) {
                personName = hikPerson.getPersonName();
            }
            //PlcMessageSocket.sentMessage(equipmentDTO.getSubtitleMachineIp(), "綁卡成功");
            PlcMessageSocket.sentMessage("192.168.70.201", personName + "成功");
//            plcClient.sendMes(equipmentDTO.getSubtitleMachineIp(),"綁卡成功");
            log.info("绑定成功");
        }
    }


    /**
     * 车道刷脸
     *
     * @param event        事件数据
     * @param equipmentDTO 时间发生的设备
     */
    private void drivewayBrushFace(Events<EventData> event, EquipmentDTO equipmentDTO) {
        //先延长事件
        redisUtils.expire(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_CAR_KEY_VALIDITY_PERIOD, TimeUnit.SECONDS);
        Integer personType = (Integer) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_CAR_PERSON_TYPE);
        //如果没有值，代表车牌或者车卡还没有刷
        if (personType == null) {
            log.info("字母机提示先刷车卡或者车牌");
            HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), "請先刷車卡或者車牌");
            return;
        }
        //如果是内部员工
        if (HikPersonConstant.INTERNAL_STAFF == personType) {
            //如果车牌的所有者和当前刷脸的人一致
            if (event.getData().getExtEventPersonNo().equals(redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_KEY_CAR_SN))) {
                if (sysConfigService.getConfig().getLocationCardEnabled()) {
                    log.info("字幕机提示请刷定位卡");
                    HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), "請刷定位卡");
                    redisUtils.hset(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_CAR_FACE, event.getData().getExtEventPersonNo());
                } else {
                    //不启用定位卡直接开门
                    //获取人脸设备绑定的车辆设备
                    EquipmentDTO carEquipmentDTO = hikEquipmentService.getEquipments().get(equipmentDTO.getBindIndexCode());
                    drivewayOpen(carEquipmentDTO, event.getData().getExtEventPersonNo(), null);

                    //清除缓存
                    redisUtils.del(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode());


//                    String carNo = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY+equipmentDTO.getIndexCode(),RedisConstant.IN_AND_OUT_KEY_CAR);
//                    if(StringUtils.isEmpty(carNo)){
//                        carNo = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY+equipmentDTO.getIndexCode(),RedisConstant.IN_AND_OUT_KEY_CAR_CARD);
//                    }
//                    HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(),carNo+ (carEquipmentDTO.getSign() == HikDeviceConstant.ENTER_THE_DOOR ? "入" : "出"));

                }

            } else {
                log.info("字母机提示车牌和人不对应");
                HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), "車牌與人臉不對應");
            }
        } else if (HikPersonConstant.VENDOR_EMPLOYEES == personType) {
            HikPerson hikPerson = hikPersonMapper.selectById(event.getData().getExtEventPersonNo());
            //比对厂商员工的工单号和车牌的工单号是否一致
            if (hikPerson.getOrderSn().equals(redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_KEY_CAR_SN))) {
                if (sysConfigService.getConfig().getLocationCardEnabled()) {
                    log.info("字幕机提示请刷定位卡");
                    HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), "請刷定位卡");
                    redisUtils.hset(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_CAR_FACE, event.getData().getExtEventPersonNo());
                } else {
                    if (hikPerson.getOrderSn().startsWith("1")) {
                        //不启用定位卡直接开门
                        //获取人脸设备绑定的车辆设备
                        EquipmentDTO carEquipmentDTO = hikEquipmentService.getEquipments().get(equipmentDTO.getBindIndexCode());
                        drivewayOpen(carEquipmentDTO, event.getData().getExtEventPersonNo(), null);

                        //清除缓存
                        redisUtils.del(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode());
                    } else {
                        String errorMsg = ManufacturerUtil.isItPossibleToGetInAndOut(HikDeviceConstant.IN_OR_OUT_IDENTIFICATION.get(equipmentDTO.getSign()), hikPerson.getPersonId());

                        if (StringUtils.isEmpty(errorMsg)) {
                            //不启用定位卡直接开门
                            //获取人脸设备绑定的车辆设备
                            EquipmentDTO carEquipmentDTO = hikEquipmentService.getEquipments().get(equipmentDTO.getBindIndexCode());
                            drivewayOpen(carEquipmentDTO, event.getData().getExtEventPersonNo(), null);

                            //清除缓存
                            redisUtils.del(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode());

//                        String carNo = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY+equipmentDTO.getIndexCode(),RedisConstant.IN_AND_OUT_KEY_CAR);
//                        if(StringUtils.isEmpty(carNo)){
//                            carNo = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY+equipmentDTO.getIndexCode(),RedisConstant.IN_AND_OUT_KEY_CAR_CARD);
//                        }
//                        HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(),carNo+ (carEquipmentDTO.getSign() == HikDeviceConstant.ENTER_THE_DOOR ? "入" : "出"));
                        } else {
                            HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), errorMsg);
                        }
                    }
                }
            }
        } else if (HikPersonConstant.PERILOUS_EMPLOYEES == personType) {
            HikPerson hikPerson = hikPersonMapper.selectById(event.getData().getExtEventPersonNo());
            //比对厂商员工的工单号和车牌的工单号是否一致
//                if(hikPerson.getOrderSn().equals(redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY+equipmentDTO.getIndexCode(),RedisConstant.IN_AND_OUT_KEY_CAR_SN))){
            if (sysConfigService.getConfig().getLocationCardEnabled()) {
                log.info("字幕机提示请刷定位卡");
                HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), "請刷定位卡");
                redisUtils.hset(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_CAR_FACE, event.getData().getExtEventPersonNo());
            } else {
//                        if(hikPerson.getOrderSn().startsWith("1")){
//                            //不启用定位卡直接开门
//                            //获取人脸设备绑定的车辆设备
//                            EquipmentDTO carEquipmentDTO = hikEquipmentService.getEquipments().get(equipmentDTO.getBindIndexCode());
//                            drivewayOpen(carEquipmentDTO,event.getData().getExtEventPersonNo(),null);
//
//                            //清除缓存
//                     carSn.contains       redisUtils.del(RedisConstant.IN_AND_OUT_CAR_KEY+equipmentDTO.getIndexCode());
//                        }else{
                String carNo = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_KEY_CAR);
                CarPerilousVO carPerilousVO = new CarPerilousVO();
                carPerilousVO.setCarNo(carNo);
                carPerilousVO.setInOutType(equipmentDTO.getSign());
                carPerilousVO.setIp(equipmentDTO.getIp());
                carPerilousVO.setCheckingType(equipmentDTO.getControlJSONBo().getDanger());
                carPerilousVO.setIdCard(hikPerson.getPersonId());
                String errorMsg = HazardousChemicalsUtil.carExists(carPerilousVO);
                if (StringUtils.isEmpty(errorMsg)) {
                    //不启用定位卡直接开门
                    //获取人脸设备绑定的车辆设备
                    EquipmentDTO carEquipmentDTO = hikEquipmentService.getEquipments().get(equipmentDTO.getBindIndexCode());
                    drivewayOpen(carEquipmentDTO, event.getData().getExtEventPersonNo(), null);

                    //清除缓存
                    redisUtils.del(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode());

//                        String carNo = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY+equipmentDTO.getIndexCode(),RedisConstant.IN_AND_OUT_KEY_CAR);
//                        if(StringUtils.isEmpty(carNo)){
//                            carNo = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY+equipmentDTO.getIndexCode(),RedisConstant.IN_AND_OUT_KEY_CAR_CARD);
//                        }
//                        HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(),carNo+ (carEquipmentDTO.getSign() == HikDeviceConstant.ENTER_THE_DOOR ? "入" : "出"));
                } else {
                    HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), errorMsg);
                }
//                        }
            }
        } else {
            log.info("字母机提示车牌和人不对应");
            HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), "車牌與人臉不對應");
        }
    }
//    }

    /**
     * 车道刷脸 一卡多人绑定
     *
     * @param event        事件数据
     * @param equipmentDTO 时间发生的设备
     */
    private void drivewayBrushFaceOneCardToManyPerson(Events<EventData> event, EquipmentDTO equipmentDTO) {
        log.info("car-------11111111");

        //先延长事件
        redisUtils.expire(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_CAR_KEY_VALIDITY_PERIOD, TimeUnit.SECONDS);
        Integer personType = (Integer) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_CAR_PERSON_TYPE);
        log.info("RedisConstant.IN_AND_OUT_CAR_KEY+equipmentDTO.getIndexCode()+++  " + RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode());
        log.info("personType  " + personType);

        //如果没有值，代表车牌或者车卡还没有刷
        if (personType == null) {
            log.info("字母机提示先刷车卡或者车牌");
            HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), "請先刷車卡或者車牌");
            return;
        }
        //获取事件的人员
        String personNo = event.getData().getExtEventPersonNo();
        //如果是内部员工
        if (HikPersonConstant.INTERNAL_STAFF == personType) {
            //获取缓存中的所有权限人员
            List<String> carSn = (List<String>) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_KEY_CAR_SN);
            //如果车牌的所有者和当前刷脸的人一致
            if (carSn.contains(personNo)) {
                if (sysConfigService.getConfig().getLocationCardEnabled()) {
                    log.info("字幕机提示请刷定位卡");
                    HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), "請刷定位卡");
                    redisUtils.hset(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_CAR_FACE, event.getData().getExtEventPersonNo());
                } else {
                    //不启用定位卡直接开门
                    //获取人脸设备绑定的车辆设备
                    EquipmentDTO carEquipmentDTO = hikEquipmentService.getEquipments().get(equipmentDTO.getBindIndexCode());
                    drivewayOpen(carEquipmentDTO, event.getData().getExtEventPersonNo(), null);

//                    String carNoCard = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(),RedisConstant.IN_AND_OUT_KEY_CAR_CARD);
//                    String carNoCar = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(),RedisConstant.IN_AND_OUT_KEY_CAR);
//
//                    String returnCarNo = null;
//                    if (!StringUtils.isEmpty(carNoCard)){
//                        returnCarNo = carNoCard;
//                    }else {
//                        returnCarNo = carNoCar;
//                    }
//                    Card card = new Card();
//                    card.setCardNo(returnCarNo);
//                    drivewayOpen(carEquipmentDTO, event.getData().getExtEventPersonNo(),card);

                    //清除缓存
                    redisUtils.del(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode());


//                    String carNo = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY+equipmentDTO.getIndexCode(),RedisConstant.IN_AND_OUT_KEY_CAR);
//                    if(StringUtils.isEmpty(carNo)){
//                        carNo = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY+equipmentDTO.getIndexCode(),RedisConstant.IN_AND_OUT_KEY_CAR_CARD);
//                    }
//                    HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(),carNo+ (carEquipmentDTO.getSign() == HikDeviceConstant.ENTER_THE_DOOR ? "入" : "出"));

                }

            } else {
                log.info("字幕机提示车牌和人不对应");
                HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), "車牌與人臉不對應");
            }
        } else if (HikPersonConstant.VENDOR_EMPLOYEES == personType) {
            HikPerson hikPerson = hikPersonMapper.selectById(event.getData().getExtEventPersonNo());
            //比对厂商员工的工单号和车牌的工单号是否一致
            List<String> carSn = (List<String>) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_KEY_CAR_SN);
            //如果车牌的所有者和当前刷脸的人一致
            if (carSn.contains(hikPerson.getOrderSn())) {
                if (sysConfigService.getConfig().getLocationCardEnabled()) {
                    log.info("字幕机提示请刷定位卡");
                    HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), "請刷定位卡");
                    redisUtils.hset(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_CAR_FACE, event.getData().getExtEventPersonNo());
                } else {
                    if (hikPerson.getOrderSn().startsWith("1")) {
                        //不启用定位卡直接开门
                        //获取人脸设备绑定的车辆设备
                        EquipmentDTO carEquipmentDTO = hikEquipmentService.getEquipments().get(equipmentDTO.getBindIndexCode());
                        drivewayOpen(carEquipmentDTO, event.getData().getExtEventPersonNo(), null);

                        //清除缓存
                        redisUtils.del(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode());
                    } else {
                        String errorMsg = ManufacturerUtil.isItPossibleToGetInAndOut(HikDeviceConstant.IN_OR_OUT_IDENTIFICATION.get(equipmentDTO.getSign()), hikPerson.getPersonId());
                        log.info("errorMsg---->" + errorMsg);
                        if (StringUtils.isEmpty(errorMsg)) {
                            //不启用定位卡直接开门
                            //获取人脸设备绑定的车辆设备
                            EquipmentDTO carEquipmentDTO = hikEquipmentService.getEquipments().get(equipmentDTO.getBindIndexCode());
                            drivewayOpen(carEquipmentDTO, event.getData().getExtEventPersonNo(), null);


//                            String carNoCard = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(),RedisConstant.IN_AND_OUT_KEY_CAR_CARD);
//                            String carNoCar = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(),RedisConstant.IN_AND_OUT_KEY_CAR);
//
//                            String returnCarNo = null;
//                            if (!StringUtils.isEmpty(carNoCard)){
//                                returnCarNo = carNoCard;
//                            }else {
//                                returnCarNo = carNoCar;
//                            }
//                            Card card = new Card();
//                            card.setCardNo(returnCarNo);
//                            drivewayOpen(carEquipmentDTO, event.getData().getExtEventPersonNo(),card);

                            //清除缓存
                            redisUtils.del(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode());

//                        String carNo = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY+equipmentDTO.getIndexCode(),RedisConstant.IN_AND_OUT_KEY_CAR);
//                        if(StringUtils.isEmpty(carNo)){
//                            carNo = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY+equipmentDTO.getIndexCode(),RedisConstant.IN_AND_OUT_KEY_CAR_CARD);
//                        }
//                        HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(),carNo+ (carEquipmentDTO.getSign() == HikDeviceConstant.ENTER_THE_DOOR ? "入" : "出"));
                        } else {

                            HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), errorMsg);
                        }
                    }
                }
            }
        } else if (HikPersonConstant.PERILOUS_EMPLOYEES == personType) {
            HikPerson hikPerson = hikPersonMapper.selectById(event.getData().getExtEventPersonNo());
            //比对厂商员工的工单号和车牌的工单号是否一致
//                if(hikPerson.getOrderSn().equals(redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY+equipmentDTO.getIndexCode(),RedisConstant.IN_AND_OUT_KEY_CAR_SN))){
            if (sysConfigService.getConfig().getLocationCardEnabled()) {
                log.info("字幕机提示请刷定位卡");
                HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), "請刷定位卡");
                redisUtils.hset(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_CAR_FACE, event.getData().getExtEventPersonNo());
            } else {
//                        if(hikPerson.getOrderSn().startsWith("1")){
//                            //不启用定位卡直接开门
//                            //获取人脸设备绑定的车辆设备
//                            EquipmentDTO carEquipmentDTO = hikEquipmentService.getEquipments().get(equipmentDTO.getBindIndexCode());
//                            drivewayOpen(carEquipmentDTO,event.getData().getExtEventPersonNo(),null);
//
//                            //清除缓存
//                            redisUtils.del(RedisConstant.IN_AND_OUT_CAR_KEY+equipmentDTO.getIndexCode());
//                        }else{
                String carNo = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_KEY_CAR);
                CarPerilousVO carPerilousVO = new CarPerilousVO();
                carPerilousVO.setCarNo(carNo);
                carPerilousVO.setInOutType(equipmentDTO.getSign());
                carPerilousVO.setIp(equipmentDTO.getIp());
                carPerilousVO.setCheckingType(equipmentDTO.getControlJSONBo().getDanger());
                carPerilousVO.setIdCard(hikPerson.getPersonId());
                String errorMsg = HazardousChemicalsUtil.carExists(carPerilousVO);
                if (StringUtils.isEmpty(errorMsg)) {
                    //不启用定位卡直接开门
                    //获取人脸设备绑定的车辆设备
                    EquipmentDTO carEquipmentDTO = hikEquipmentService.getEquipments().get(equipmentDTO.getBindIndexCode());
                    drivewayOpen(carEquipmentDTO, event.getData().getExtEventPersonNo(), null);

                    //清除缓存
                    redisUtils.del(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode());

//                        String carNo = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY+equipmentDTO.getIndexCode(),RedisConstant.IN_AND_OUT_KEY_CAR);
//                        if(StringUtils.isEmpty(carNo)){
//                            carNo = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY+equipmentDTO.getIndexCode(),RedisConstant.IN_AND_OUT_KEY_CAR_CARD);
//                        }
//                        HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(),carNo+ (carEquipmentDTO.getSign() == HikDeviceConstant.ENTER_THE_DOOR ? "入" : "出"));
                } else {
                    HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), errorMsg);
                }
//                        }
            }
        } else {
            log.info("字母机提示车牌和人不对应");
            HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), "車牌與人臉不對應");
        }
    }


    /**
     * 车道刷卡
     *
     * @param event        事件数据
     * @param equipmentDTO 时间发生的设备
     */
    private void drivewaySwipe(Events<EventData> event, EquipmentDTO equipmentDTO) {
        Card card = hikCardMapper.selectById(event.getData().getExtEventCardNo());
        if (card == null) {
            log.info("字幕机提示卡号不存在");
            HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), "卡號不存在");
            return;
        }
        //如果是定位卡
        if (CardConstant.LOCATION_CARD == card.getCardType()) {
            redisUtils.expire(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_CAR_KEY_VALIDITY_PERIOD, TimeUnit.SECONDS);
            Integer personType = (Integer) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_CAR_PERSON_TYPE);
            //如果没有值，代表车牌或者车卡还没有刷
            if (personType == null) {
                log.info("字母机提示先刷车卡或者车牌");
                HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), "請先刷車卡或者車牌");
                return;
            }
            String personId = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_CAR_FACE);
            if (StringUtils.isEmpty(personId)) {
                log.info("字幕机提示先刷人脸");
                HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), "先刷人臉");
                return;
            }

            //查看定位卡是否属于该用户
            if (Objects.equals(card.getCardNo(), personId)) {
                String errorMsg = null;

                if (HikPersonConstant.VENDOR_EMPLOYEES == personType) {
                    if (!(CardConstant.MANUFACTURER_S_CARD == card.getCardType() && card.getCardNo().startsWith("1"))) {
                        errorMsg = ManufacturerUtil.isItPossibleToGetInAndOut(HikDeviceConstant.IN_OR_OUT_IDENTIFICATION.get(equipmentDTO.getSign()), personId);
                    }
                }
                //如果是厂商员工需要多一层校验
                if (HikPersonConstant.INTERNAL_STAFF == personType || errorMsg == null) {
                    log.info("校验通过，开启车辆道闸");
//                    //发送日志
//                    String carNumber = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY+equipmentDTO.getIndexCode(),RedisConstant.IN_AND_OUT_KEY_CAR);
//                    if(StringUtils.isEmpty(carNumber)){
//                        carNumber = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY+equipmentDTO.getIndexCode(),RedisConstant.IN_AND_OUT_KEY_CAR_CARD);
//                    }

                    //获取人脸设备绑定的车辆设备
                    EquipmentDTO carEquipmentDTO = hikEquipmentService.getEquipments().get(equipmentDTO.getBindIndexCode());
//                    HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(),carNumber+ (carEquipmentDTO.getSign() == HikDeviceConstant.ENTER_THE_DOOR ? "入" : "出"));


                    drivewayOpen(carEquipmentDTO, personId, card);
//                    if(plcClient.hasChannelFuture(carEquipmentDTO.getPlcIp())){
//                        plcClient.humaneDoor(carEquipmentDTO.getPlcIp(),carEquipmentDTO.getPlcCommand());
//                    }else{
//                        log.info("该设备没有绑定plc>>>>>>>{}",carEquipmentDTO.getIndexCode());
//                    }
//
//                    LogUtil.sendLog(new InAndOutLog(personId,card.getCardNumber(),carEquipmentDTO.getIp(),
//                            LogConstant.DEVICE_TO_LOG_PASS_IN_AND_OUT.get(carEquipmentDTO.getSign()),carNumber));
                } else {
                    log.info("字幕机提示,看服务端返回的信息，提示为什么不能开启道闸");
                    HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), errorMsg);
                }
                //清除缓存
                redisUtils.del(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode());
            } else {
                log.info("字幕机提示车辆和他没有关系");
                HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), "人員和定位卡不匹配");
            }

        } else if (CardConstant.INTERNAL_TRUCK == card.getCardType() || CardConstant.MANUFACTURER_S_CARD == card.getCardType()) {
            //如果是内部车卡或者外部车卡
            if (!hikCarAuthMapper.existCarIdAndDeviceIdAndCarType(event.getData().getExtEventCardNo(), CarAuthConstant.CAR_CARD, equipmentDTO.getBindIndexCode())) {
                log.info("提示车辆没有权限");
                HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), "車輛沒有權限");
                return;
            }
            String carCardCache = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_KEY_CAR_CARD);
            //如果发现扫描的车牌和缓存一致，不做处理
            if (StringUtils.isNotEmpty(carCardCache) && carCardCache.equals(event.getData().getExtEventCardNo())) {
                return;
            }
            //清除之前的缓存
            redisUtils.del(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode());

            //设置车卡
            redisUtils.hset(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_KEY_CAR_CARD, card.getCardNumber());
            //设置人员类型
            //如果是内部员工
            if (CardConstant.INTERNAL_TRUCK == card.getCardType()) {
                redisUtils.hset(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_CAR_PERSON_TYPE, HikPersonConstant.INTERNAL_STAFF);
            } else {
                redisUtils.hset(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_CAR_PERSON_TYPE, HikPersonConstant.VENDOR_EMPLOYEES);
            }
            //设置车辆唯一值
            redisUtils.hset(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_KEY_CAR_SN, card.getCardNo());
            //设置过期时间
            redisUtils.expire(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_CAR_KEY_VALIDITY_PERIOD, TimeUnit.SECONDS);
            log.info("字幕机提示下车刷人脸");
            HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), "請刷人臉");
        }
    }


    /**
     * 车道刷卡 一卡对多人
     *
     * @param event        事件数据
     * @param equipmentDTO 时间发生的设备
     */
    private void drivewaySwipeOneCardToManyPerson(Events<EventData> event, EquipmentDTO equipmentDTO) {
        QueryWrapper<Card> query = new QueryWrapper<>();
        query.eq("card_number", event.getData().getExtEventCardNo());
        List<Card> cards = hikCardMapper.selectList(query);
        if (CollectionUtil.isEmpty(cards)) {
            log.info("字幕机提示卡号不存在");
            HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), "卡號不存在");
            return;
        }


        //定位卡或外部车卡
        Card card = cards.get(0);
        //
        List<String> carSnlist = new ArrayList<>();
        for (Card card1 : cards) {
            carSnlist.add(card1.getCardNo());
        }
        //如果是定位卡
        if (CardConstant.LOCATION_CARD == card.getCardType()) {
            redisUtils.expire(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_CAR_KEY_VALIDITY_PERIOD, TimeUnit.SECONDS);
            Integer personType = (Integer) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_CAR_PERSON_TYPE);
            //如果没有值，代表车牌或者车卡还没有刷
            if (personType == null) {
                log.info("字母机提示先刷车卡或者车牌");
                HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), "請先刷車卡或者車牌");
                return;
            }
            String personId = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_CAR_FACE);
            if (StringUtils.isEmpty(personId)) {
                log.info("字幕机提示先刷人脸");
                HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), "先刷人臉");
                return;
            }

            //查看定位卡是否属于该用户
            if (Objects.equals(card.getCardNo(), personId)) {
                String errorMsg = null;

                if (HikPersonConstant.VENDOR_EMPLOYEES == personType) {
                    if (!(CardConstant.MANUFACTURER_S_CARD == card.getCardType() && card.getCardNo().startsWith("1"))) {
                        errorMsg = ManufacturerUtil.isItPossibleToGetInAndOut(HikDeviceConstant.IN_OR_OUT_IDENTIFICATION.get(equipmentDTO.getSign()), personId);
                    }
                }
                //如果是厂商员工需要多一层校验
                if (HikPersonConstant.INTERNAL_STAFF == personType || errorMsg == null) {
                    log.info("校验通过，开启车辆道闸");
//                    //发送日志
//                    String carNumber = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY+equipmentDTO.getIndexCode(),RedisConstant.IN_AND_OUT_KEY_CAR);
//                    if(StringUtils.isEmpty(carNumber)){
//                        carNumber = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY+equipmentDTO.getIndexCode(),RedisConstant.IN_AND_OUT_KEY_CAR_CARD);
//                    }

                    //获取人脸设备绑定的车辆设备
                    EquipmentDTO carEquipmentDTO = hikEquipmentService.getEquipments().get(equipmentDTO.getBindIndexCode());
//                    HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(),carNumber+ (carEquipmentDTO.getSign() == HikDeviceConstant.ENTER_THE_DOOR ? "入" : "出"));


                    drivewayOpen(carEquipmentDTO, personId, card);
//                    if(plcClient.hasChannelFuture(carEquipmentDTO.getPlcIp())){
//                        plcClient.humaneDoor(carEquipmentDTO.getPlcIp(),carEquipmentDTO.getPlcCommand());
//                    }else{
//                        log.info("该设备没有绑定plc>>>>>>>{}",carEquipmentDTO.getIndexCode());
//                    }
//
//                    LogUtil.sendLog(new InAndOutLog(personId,card.getCardNumber(),carEquipmentDTO.getIp(),
//                            LogConstant.DEVICE_TO_LOG_PASS_IN_AND_OUT.get(carEquipmentDTO.getSign()),carNumber));
                } else {
                    log.info("字幕机提示,看服务端返回的信息，提示为什么不能开启道闸");
                    HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), errorMsg);
                }
                //清除缓存
                redisUtils.del(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode());
            } else {
                log.info("字幕机提示车辆和他没有关系");
                HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), "人員和定位卡不匹配");
            }

        } else if (CardConstant.INTERNAL_TRUCK == card.getCardType() || CardConstant.MANUFACTURER_S_CARD == card.getCardType()) {
            // ++ 内部车卡不用判断
            if (CardConstant.MANUFACTURER_S_CARD == card.getCardType()) {
                //如果是外部车卡
                if (!hikCarAuthMapper.existCarIdAndDeviceIdAndCarType(event.getData().getExtEventCardNo(), CarAuthConstant.CAR_CARD, equipmentDTO.getBindIndexCode())) {
                    log.info("提示车辆没有权限");
                    HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), "車輛沒有權限");
                    return;
                }
            }
            String carCardCache = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_KEY_CAR_CARD);
            //如果发现扫描的车牌和缓存一致，不做处理
            if (StringUtils.isNotEmpty(carCardCache) && carCardCache.equals(event.getData().getExtEventCardNo())) {
                return;
            }

            //清除之前的缓存
            redisUtils.del(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode());
            //设置车卡
            redisUtils.hset(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_KEY_CAR_CARD, card.getCardNumber());

            //如果是内部员工
            if (CardConstant.INTERNAL_TRUCK == card.getCardType()) {
                redisUtils.hset(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_CAR_PERSON_TYPE, HikPersonConstant.INTERNAL_STAFF);
            } else {
                redisUtils.hset(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_CAR_PERSON_TYPE, HikPersonConstant.VENDOR_EMPLOYEES);
            }
//            //设置车辆唯一值
//            redisUtils.hset(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_KEY_CAR_SN, card.getCardNo());
            //设置车辆绑定的身份值
            redisUtils.hset(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_KEY_CAR_SN, carSnlist);
            //设置过期时间
            redisUtils.expire(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getIndexCode(), RedisConstant.IN_AND_OUT_CAR_KEY_VALIDITY_PERIOD, TimeUnit.SECONDS);
            log.info("字幕机提示下车刷人脸");
            HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), "請刷人臉");
        }
    }


    /**
     * 人道开门
     *
     * @param equipmentDTO
     * @param hikPerson    海康人员信息
     * @param card
     */
    private void humaneDoor(EquipmentDTO equipmentDTO, HikPerson hikPerson, Card card) {
        log.info("equipmentDTO.getPlcIp()+++" + equipmentDTO.getPlcIp());
        log.info("plcClient.hasChannelFuture(equipmentDTO.getPlcIp())+++" + plcClient.hasChannelFuture(equipmentDTO.getPlcIp()));
//        if(!"192.168.70.152".equals(equipmentDTO.getPlcIp())){
//            if(plcClient.hasChannelFuture(equipmentDTO.getPlcIp())){
//                plcClient.openDoor(equipmentDTO.getPlcIp(),equipmentDTO.getPlcCommand());
//            }else{
//                log.info("该设备没有绑定plc>>>>>>>{}",equipmentDTO.getIndexCode());
//                return;
//            }
//
//            plcClient.sendMes(equipmentDTO.getSubtitleMachineIp(),hikPerson.getPersonName() );
//        }else{

//            plcClient.sendMes(equipmentDTO.getSubtitleMachineIp(),hikPerson.getPersonName() );

        PlcSocket.sentMessage(equipmentDTO.getPlcIp(), equipmentDTO.getPlcCommand());
        try {
            PlcMessageSocket.sentMessage(equipmentDTO.getSubtitleMachineIp(), hikPerson.getPersonName());
        } catch (Exception e) {
            log.info("发送字幕机失败");
        }
//        }
        //发送日志
        LogUtil.sendLog(new InAndOutLog(hikPerson.getPersonId(), card == null ? "" : card.getCardNumber(), equipmentDTO.getIp(),
                LogConstant.DEVICE_TO_LOG_PASS_IN_AND_OUT.get(equipmentDTO.getSign()), null));
    }


    /**
     * 车道开门
     *
     * @param equipmentDTO
     * @param personId
     * @param card
     */
    private void drivewayOpen(EquipmentDTO equipmentDTO, String personId, Card card) {
//        if(plcClient.hasChannelFuture(equipmentDTO.getPlcIp())){
//            plcClient.openDoor(equipmentDTO.getPlcIp(),equipmentDTO.getPlcCommand());
//        }else{
//            log.info("该设备没有绑定plc>>>>>>>{}",equipmentDTO.getIndexCode());
//            return;
//        }
        PlcSocket.sentMessage(equipmentDTO.getPlcIp(), equipmentDTO.getPlcCommand());
        //发送开门消息
        String carNo = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getBindIndexCode(), RedisConstant.IN_AND_OUT_KEY_CAR);
        if (StringUtils.isEmpty(carNo)) {
            carNo = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getBindIndexCode(), RedisConstant.IN_AND_OUT_KEY_CAR_CARD);
        }
        HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), carNo + (equipmentDTO.getSign() == HikDeviceConstant.ENTER_THE_DOOR ? "入" : "出"));

        //发送日志
        log.info("InAndOutLog--->personId=" + personId + "card-->" + (card == null ? "" : card.getCardNumber()) + "carNo--->" + carNo);
        LogUtil.sendLog(new InAndOutLog(personId, card == null ? "" : card.getCardNumber(), equipmentDTO.getIp(),
                LogConstant.DEVICE_TO_LOG_PASS_IN_AND_OUT.get(equipmentDTO.getSign()), carNo));

//        //发送日志
//        LogUtil.sendLog(new InAndOutLog(personId, card == null ? "" : card.getCardNumber(), equipmentDTO.getIp(),
//                LogConstant.DEVICE_TO_LOG_PASS_IN_AND_OUT.get(equipmentDTO.getSign()), card == null ? "" : card.getCardNo()));
    }


}
