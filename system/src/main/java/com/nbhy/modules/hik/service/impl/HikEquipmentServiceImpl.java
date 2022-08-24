package com.nbhy.modules.hik.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.nbhy.modules.erp.config.ERPConfigConstant;
import com.nbhy.modules.erp.util.EquipmentUtil;
import com.nbhy.modules.hik.constant.HikDeviceConstant;
import com.nbhy.modules.hik.constant.SubtitleMachineConstant;
import com.nbhy.modules.hik.domain.dto.*;
import com.nbhy.modules.hik.exception.HikException;
import com.nbhy.modules.hik.service.HikEquipmentService;
import com.nbhy.modules.hik.util.*;
import com.nbhy.modules.hik.util.CommonMethod.osSelect;
import com.nbhy.modules.plc.client.PlcClient;
import com.nbhy.modules.plc.client.PlcMessageSocket;
import com.nbhy.utils.StringUtils;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

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
public class HikEquipmentServiceImpl implements HikEquipmentService  {


    private static Map<String,EquipmentDTO> equipmentDTOMap = new ConcurrentHashMap<>();
//    private  final PlcClient plcClient;
    @Autowired
    private PlcClient plcClient;
    private final ERPConfigConstant erpConfigConstant;

    @Value("${spring.profiles.active}")
    private String profiles;//当前环境dev/prod

    @Override
    public List<HikDeviceDTO> queryAll(Integer deviceType) {
        List<HikDeviceDTO> hikDeviceDTOS = new ArrayList<>();
        if(deviceType == null || deviceType == HikDeviceConstant.FACE_DEVICE){
            int index = 1;
            int size = 500;
            while (true) {
                List<HikAcsDevEquipment> acsDeviceHikEquipments = HikEquipmentUtil.deviceResource(index, size, "acsDevice");
                if (CollectionUtils.isEmpty(acsDeviceHikEquipments)) {
                    break;
                }
                //把海康设备设置为对应的属性
                for (HikAcsDevEquipment hikAcsDevEquipment : acsDeviceHikEquipments) {
                    HikDeviceDTO hikDeviceDTO = new HikDeviceDTO();
                    hikDeviceDTO.setDeviceName(hikAcsDevEquipment.getName());
                    hikDeviceDTO.setIndexCode(hikAcsDevEquipment.getIndexCode());
                    hikDeviceDTO.setDeviceType(HikDeviceConstant.FACE_DEVICE);
                    hikDeviceDTO.setIp(hikAcsDevEquipment.getIp());
                    hikDeviceDTO.setPort(hikAcsDevEquipment.getPort());
                    hikDeviceDTOS.add(hikDeviceDTO);
                }
                if(acsDeviceHikEquipments.size() < size){
                    break;
                }
                index++;
            }
//            List<HikEquipmentChannel> hikEquipmentChannels = HikEquipmentUtil.queryAllChannel();
//            if(CollectionUtil.isNotEmpty(hikEquipmentChannels)){
//                for (HikEquipmentChannel hikEquipmentChannel : hikEquipmentChannels) {
//                    HikDeviceDTO hikDeviceDTO = new HikDeviceDTO();
//                    hikDeviceDTO.setDeviceName(hikEquipmentChannel.getDoorName());
//                    hikDeviceDTO.setIndexCode(hikEquipmentChannel.getDoorIndexCode());
//                    hikDeviceDTO.setDeviceType(HikDeviceConstant.FACE_DEVICE);
//                    hikDeviceDTOS.add(hikDeviceDTO);
//                }
//            }
        }

        if(deviceType == null || deviceType == HikDeviceConstant.CAR_DEVICE){
            List<CarEquipment> carEquipments = HikEquipmentUtil.queryAllCarChannel();
            if(CollectionUtil.isNotEmpty(carEquipments)){
                for (CarEquipment carEquipment : carEquipments) {
                    HikDeviceDTO hikDeviceDTO = new HikDeviceDTO();
                    hikDeviceDTO.setDeviceName(carEquipment.getRoadwayName());
                    hikDeviceDTO.setIndexCode(carEquipment.getRoadwayIndexCode());
                    hikDeviceDTO.setDeviceType(HikDeviceConstant.CAR_DEVICE);
//                    hikDeviceDTO.setPort(carEquip);
                    hikDeviceDTOS.add(hikDeviceDTO);
                }
            }
        }
        return hikDeviceDTOS;
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanCache() {
        syncServiceDevice();
    }

    @Override
    @PostConstruct
    public void syncServiceDevice() {
        log.info("profiles++++" + profiles);
        if("dev".equals(profiles)){
            return;
        }
        String result = EquipmentUtil.queryEquipment();
        JSONObject jsonObject = JSON.parseObject(result);
        List<EquipmentDTO> equipmentDTOS = jsonObject.getJSONArray("data").toJavaList(EquipmentDTO.class);
        equipmentDTOS.stream().forEach(equipmentDTO -> {
            log.info("equipmentDTO111111" + JSON.toJSONString(equipmentDTO));
            equipmentDTOMap.put(equipmentDTO.getIndexCode(),equipmentDTO);
//            if(!"192.168.70.152".equals(equipmentDTO.getPlcIp())){
//                log.info("2111111111");
//                if(StringUtils.isNotEmpty(equipmentDTO.getPlcIp()) && equipmentDTO.getPlcPort() != null
//                        && StringUtils.isNotEmpty(equipmentDTO.getPlcCommand())) {
//                    log.info("plcClient.hasChannelFuture(equipmentDTO.getPlcIp())+++" + plcClient.hasChannelFuture(equipmentDTO.getPlcIp()));
//                    if (!plcClient.hasChannelFuture(equipmentDTO.getPlcIp())) {
//                        log.info("24444444444444");
//                        plcClient.initConnect(equipmentDTO.getPlcIp(), equipmentDTO.getPlcPort());
//                    }
//                }
//                log.info("22,2222222222222");
//                if(StringUtils.isNotEmpty(equipmentDTO.getSubtitleMachineIp())){
//                    log.info("23333333333333");
////                    plcClient.initConnect(equipmentDTO.getSubtitleMachineIp(), 6000);
//                    PlcMessageSocket plc=new PlcMessageSocket("192.168.70.86",6000,0, PlcMessageSocket.CODE_TYPE.HEX.name());
//                    plc.sendCommLine();
//                    String message="中文 english";
//                    String[] messages = StrUtil.split(message, 5);
//                    for (int i = 0; i < messages.length; i++) {
//                        plc.sendComm(messages[i]);
//                        //如果没有字符，退出循环
//                        if(i == (message.length() -1) ){
//                            continue;
//                        }
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//
//                    plc.sendComm("");
//                    plc.close();
//                    try {
//                        Thread.sleep(150);
//                    }catch (Exception e){
//
//                    }
//                    ChannelFuture channelFuture = plcClient.getChannelFuture(equipmentDTO.getSubtitleMachineIp());
//                    if(channelFuture != null){
//                        //恢复指令
//                        channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer(SubtitleMachineConstant.RESTORE_LINE_NUMBER));
//                        try {
//                            Thread.sleep(10);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                        //下发空打底
//                        channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer(SubtitleMachineUtil.getCommand(" ")));
//                    }
//                }
//            }

        });
    }

    @Override
    public Map<String, EquipmentDTO> getEquipments() {
//        EquipmentDTO equipmentDTO = new EquipmentDTO();
//        equipmentDTO.setIndexCode("cea0aaf7967341a8829c6318a2bbb4fc");
//        equipmentDTO.setDeviceAttribute(HikDeviceConstant.LANE_BOUND_FACE_DEVICE);
//        equipmentDTO.setBindIndexCode("28e61a399cbd40de9e79807f036d1f3e");
//        Map<String,EquipmentDTO> equipmentDTOMap = new HashMap<>();
//        equipmentDTOMap.put("cea0aaf7967341a8829c6318a2bbb4fc",equipmentDTO);
//        return equipmentDTOMap;
        return equipmentDTOMap;
    }


    static HCNetSDK hCNetSDK = null;
    static FMSGCallBack_V31 fMSFCallBack_V31 = null;


    @Override
//    @PostConstruct
    public void syncsdk() {
        if (hCNetSDK == null) {
            if (!CreateSDKInstance()) {
                log.error("Load SDK fail");
                throw new HikException("加载sdk失败");
            }
        }
        /**初始化*/
        hCNetSDK.NET_DVR_Init();
        /**加载日志*/
        hCNetSDK.NET_DVR_SetLogToFile(3, "../sdklog", false);
        //设置报警回调函数
        if (fMSFCallBack_V31 == null) {
            fMSFCallBack_V31 = new FMSGCallBack_V31();
            Pointer pUser = null;
            if (!hCNetSDK.NET_DVR_SetDVRMessageCallBack_V31(fMSFCallBack_V31, pUser)) {
                log.info("设置回调函数失败!");
                return;
            } else {
                log.info("设置回调函数成功!");
            }
        }
        /** 设备上传的报警信息是COMM_VCA_ALARM(0x4993)类型，
         在SDK初始化之后增加调用NET_DVR_SetSDKLocalCfg(enumType为NET_DVR_LOCAL_CFG_TYPE_GENERAL)设置通用参数NET_DVR_LOCAL_GENERAL_CFG的byAlarmJsonPictureSeparate为1，
         将Json数据和图片数据分离上传，这样设置之后，报警布防回调函数里面接收到的报警信息类型为COMM_ISAPI_ALARM(0x6009)，
         报警信息结构体为NET_DVR_ALARM_ISAPI_INFO（与设备无关，SDK封装的数据结构），更便于解析。*/
        HCNetSDK.NET_DVR_LOCAL_GENERAL_CFG struNET_DVR_LOCAL_GENERAL_CFG = new HCNetSDK.NET_DVR_LOCAL_GENERAL_CFG();
        struNET_DVR_LOCAL_GENERAL_CFG.byAlarmJsonPictureSeparate = 1;   //设置JSON透传报警数据和图片分离
        struNET_DVR_LOCAL_GENERAL_CFG.write();
        Pointer pStrNET_DVR_LOCAL_GENERAL_CFG = struNET_DVR_LOCAL_GENERAL_CFG.getPointer();
        hCNetSDK.NET_DVR_SetSDKLocalCfg(17, pStrNET_DVR_LOCAL_GENERAL_CFG);
        for (String s : equipmentDTOMap.keySet()) {
            EquipmentDTO equipmentDTO = equipmentDTOMap.get(s);
            if(equipmentDTO.getDeviceType() == 1){
                Alarm.Login_V40(0, equipmentDTO.getIp(), (short) 8000, "admin", "zaq12wsx");
                Alarm.SetAlarm(0);
            }
        }

//        while (true) {
//            //这里加入控制台输入控制，是为了保持连接状态，当输入Y表示布防结束
////            System.out.print("请选择是否撤出布防(Y/N)：");
//            Scanner input = new Scanner(System.in);
//            String str = input.next();
//            if (str.equals("Y")) {
//                break;
//            }
//        }
//        Alarm.Logout(0);
    }

    @Override
    public void shutdownChannel() {
        plcClient.destroyChannel();
    }

    @Override
    public void initializeChannel() {
        plcClient = new PlcClient();
    }

    /**
     * 动态库加载
     *
     * @return
     */
    private static boolean CreateSDKInstance() {
        if (hCNetSDK == null) {
            synchronized (HCNetSDK.class) {
                String strDllPath = "";
                try {
                    if (osSelect.isWindows())
                        //win系统加载库路径
                        strDllPath = System.getProperty("user.dir") + "\\lib\\HCNetSDK.dll";
                    else if (osSelect.isLinux())
                        //Linux系统加载库路径
                        strDllPath = System.getProperty("user.dir") + "/lib/libhcnetsdk.so";
                    hCNetSDK = (HCNetSDK) Native.loadLibrary(strDllPath, HCNetSDK.class);
                } catch (Exception ex) {
                    System.out.println("loadLibrary: " + strDllPath + " Error: " + ex.getMessage());
                    return false;
                }
            }
        }
        return true;
    }
}

