
package com.nbhy.modules.hik.util;

import com.nbhy.modules.hik.constant.CarAuthConstant;
import com.nbhy.modules.hik.constant.CarConstant;
import com.nbhy.modules.hik.constant.HikPersonConstant;
import com.nbhy.modules.hik.constant.RedisConstant;
import com.nbhy.modules.hik.domain.dto.EquipmentDTO;
import com.nbhy.modules.hik.domain.entity.Car;
import com.nbhy.modules.hik.mapper.HikCarAuthMapper;
import com.nbhy.modules.hik.mapper.HikCarMapper;
import com.nbhy.modules.hik.service.HikEquipmentService;
import com.nbhy.modules.hik.service.impl.HikEquipmentServiceImpl;
import com.nbhy.utils.RedisUtils;
import com.nbhy.utils.StringUtils;
import com.sun.jna.Pointer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//@RequiredArgsConstructor
@Slf4j
@Service
public class FMSGCallBack_V31 implements HCNetSDK.FMSGCallBack_V31 {

    @Resource
    private HikEquipmentService hikEquipmentService;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private HikCarMapper hikCarMapper;
    @Resource
    private HikCarAuthMapper hikCarAuthMapper;


    //报警信息回调函数
    public boolean invoke(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
        AlarmDataHandle(lCommand, pAlarmer, pAlarmInfo, dwBufLen, pUser);
        return true;
    }

    @Async
    public void AlarmDataHandle(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
//        System.out.println("报警事件类型： lCommand:" + Integer.toHexString(lCommand));
        Map<String, EquipmentDTO> equipmentMap = hikEquipmentService.getEquipments();
        String sTime;
        String MonitoringSiteID;
//        String s = Integer.toHexString(lCommand);
        //lCommand是传的报警类型
        //停车场数据上传
        HCNetSDK.NET_ITS_PARK_VEHICLE strParkVehicle = new HCNetSDK.NET_ITS_PARK_VEHICLE();
        if(HCNetSDK.COMM_ITS_PARK_VEHICLE == lCommand) {
            strParkVehicle.write();
            Pointer pstrParkVehicle = strParkVehicle.getPointer();
            pstrParkVehicle.write(0, pAlarmInfo.getByteArray(0, strParkVehicle.size()), 0, strParkVehicle.size());
            strParkVehicle.read();
//            try {
//                byte ParkError = strParkVehicle.byParkError; //停车异常：0- 正常，1- 异常
//                String ParkingNo = new String(strParkVehicle.byParkingNo, "UTF-8"); //车位编号
//                byte LocationStatus = strParkVehicle.byLocationStatus; //车位车辆状态 0- 无车，1- 有车
//                MonitoringSiteID = strParkVehicle.byMonitoringSiteID.toString();
//                String plateNo = new String(strParkVehicle.struPlateInfo.sLicense, "GBK"); //车牌号
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
            String carNumber = null;
            String byDeviceID = null;
            try {
                carNumber = new String(strParkVehicle.struPlateInfo.sLicense, "GBK");
                byDeviceID = new String(strParkVehicle.byDeviceID, "GBK");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //如果车道号或者设备编号为空
            if (StringUtils.isEmpty(carNumber) || StringUtils.isEmpty(byDeviceID)) {
                return;
            }
            if (!redisUtils.hasKey(RedisConstant.IN_AND_OUT_LIMITTIME_KEY + carNumber)) {
                log.info("新建车牌carNumber ++++" + carNumber);
                //设置刷脸的车牌id
                redisUtils.set(RedisConstant.IN_AND_OUT_LIMITTIME_KEY + carNumber, 1, RedisConstant.ANGIN_PERSON_KEY_VALIDITY_PERIOD);
            } else {
                log.info("重复车牌carNumber ++++" + carNumber);
                return;
//                    Map<String, EquipmentDTO> equipmentMap = hikEquipmentService.getEquipments();
//                    EquipmentDTO equipmentDTO = equipmentMap.get(event.getData().getRoadwayIndex());
//                    HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(),"抱歉，您兩次刷臉間隔小於15秒，請15秒後再刷臉");
            }
            EquipmentDTO equipmentDTO = equipmentMap.get(byDeviceID);
            if (equipmentDTO == null) {
                log.error("查询不到此设备");
                return;
            }
//
            if(StringUtils.isEmpty(equipmentDTO.getBindIndexCode())){
                log.error("道闸未绑定人脸设备");
                return;
            }
            Car car = hikCarMapper.selectById(carNumber);
            if (car == null) {
                log.info("車牌不存在{}", carNumber);
                HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(byDeviceID, "車牌不存在");
                return;
            }
//
            if (!hikCarAuthMapper.existCarIdAndDeviceIdAndCarType(carNumber, CarAuthConstant.CAR_NUMBER, equipmentDTO.getIndexCode())) {
                log.info("您沒有進出此門崗的權限{}", carNumber);
                HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(byDeviceID, "您沒有進出此門崗的權限");
                return;
            }
//
//
            String carNumberCache = (String) redisUtils.hget(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getBindIndexCode(), RedisConstant.IN_AND_OUT_KEY_CAR);
            //如果发现扫描的车牌和缓存一致，不做处理
            if (StringUtils.isNotEmpty(carNumberCache) && carNumberCache.equals(carNumber)) {
                return;
            }
            //清除之前的缓存
            redisUtils.del(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getBindIndexCode());

            //设置车卡
            redisUtils.hset(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getBindIndexCode(), RedisConstant.IN_AND_OUT_KEY_CAR, carNumber);
            //设置人员类型
            //如果是内部员工
            if (CarConstant.INTERNAL_CAR_NUMBER == car.getCarType()) {
                redisUtils.hset(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getBindIndexCode(), RedisConstant.IN_AND_OUT_CAR_PERSON_TYPE, HikPersonConstant.INTERNAL_STAFF);
            } else if (CarConstant.MANUFACTURER_CAR_NUMBER == car.getCarType()) {
                redisUtils.hset(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getBindIndexCode(), RedisConstant.IN_AND_OUT_CAR_PERSON_TYPE, HikPersonConstant.VENDOR_EMPLOYEES);
            } else if (CarConstant.HAZARDOUS_CAR_NUMBER == car.getCarType()) {
                redisUtils.hset(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getBindIndexCode(), RedisConstant.IN_AND_OUT_CAR_PERSON_TYPE, HikPersonConstant.PERILOUS_EMPLOYEES);
//                    CarPerilousVO carPerilousVO = new CarPerilousVO();
//                    carPerilousVO.setCarNo(carNumber);
//                    carPerilousVO.setCheckingType(equipmentDTO.getControlJSONBo().getDanger());
//                    carPerilousVO.setCarNo(carNumber);
//                    carPerilousVO.setCarNo(carNumber);
//                    String errorMsg = HazardousChemicalsUtil.carExists(carPerilousVO);
//                    if (errorMsg == null) {
//                        log.info("危化品员工校验成功,开门");
//                        humaneDoor(equipmentDTO, hikPerson, null);
//                    } else {
//                        log.info("字幕机提示,看服务端返回的信息，提示为什么不能开门");
//                        plcClient.sendMes(equipmentDTO.getSubtitleMachineIp(), errorMsg);
//                    }
            }
            //设置车辆唯一值
            redisUtils.hset(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getBindIndexCode(), RedisConstant.IN_AND_OUT_KEY_CAR_SN, car.getCarSn());
            //设置过期时间
            redisUtils.expire(RedisConstant.IN_AND_OUT_CAR_KEY + equipmentDTO.getBindIndexCode(), RedisConstant.IN_AND_OUT_CAR_KEY_VALIDITY_PERIOD, TimeUnit.SECONDS);
            HikCarEquipmentUtil.sendCarLedMsgDefaultConfig(equipmentDTO.getBindIndexCode(), "請刷人臉");
//            }
            //报警图片信息
//            for (int i = 0; i < strParkVehicle.dwPicNum; i++) {
//                if (strParkVehicle.struPicInfo[i].dwDataLen > 0) {
//                    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
//                    String newName = sf.format(new Date());
//                    FileOutputStream fout;
//                    try {
//                        String filename = "../pic/" + newName + "_ParkVehicle.jpg";
//                        fout = new FileOutputStream(filename);
//                        //将字节写入文件
//                        long offset = 0;
//                        ByteBuffer buffers = strParkVehicle.struPicInfo[i].pBuffer.getByteBuffer(offset, strParkVehicle.struPicInfo[i].dwDataLen);
//                        byte[] bytes = new byte[strParkVehicle.struPicInfo[i].dwDataLen];
//                        buffers.rewind();
//                        buffers.get(bytes);
//                        fout.write(bytes);
//                        fout.close();
//                    } catch (FileNotFoundException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                }
//            }
        }
        return ;
    }
}





