package com.nbhy.test;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.nbhy.AppRun;
import com.nbhy.modules.erp.util.PersonCardUtil;
import com.nbhy.modules.hik.constant.CarAuthConstant;
import com.nbhy.modules.hik.constant.CardConstant;
import com.nbhy.modules.hik.constant.HikDeviceConstant;
import com.nbhy.modules.hik.constant.HikPersonConstant;
import com.nbhy.modules.hik.domain.dto.HikDeviceDTO;
import com.nbhy.modules.hik.domain.dto.HikEquipment;
import com.nbhy.modules.hik.domain.entity.Card;
import com.nbhy.modules.hik.domain.entity.HikCarAuth;
import com.nbhy.modules.hik.domain.entity.HikPerson;
import com.nbhy.modules.hik.domain.entity.HikPersonAuth;
import com.nbhy.modules.hik.mapper.*;
import com.nbhy.modules.hik.service.HikEquipmentService;
import com.nbhy.modules.hik.service.HikPersonService;
import com.nbhy.modules.hik.util.*;
import com.nbhy.modules.plc.client.PlcClient;
import com.nbhy.modules.plc.constant.PlcCommandConstant;
import com.nbhy.modules.system.domain.entity.Menu;
import com.nbhy.modules.system.domain.entity.User;
import com.nbhy.modules.system.mapper.MenuMapper;
import com.nbhy.modules.system.mapper.RoleMapper;
import com.nbhy.modules.system.mapper.UserMapper;
import com.nbhy.modules.system.service.UserService;
import com.nbhy.result.CommonResult;
import com.nbhy.utils.RedisUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(classes = {AppRun.class})
@RunWith(value = SpringJUnit4ClassRunner.class)
public class TestSpringboot {
    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private RoleMapper roleMapper;


    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private HikEquipmentService hikEquipmentService;

    @Autowired
    private HikPersonMapper hikPersonMapper;

    @Autowired
    private HikPersonAuthMapper hikPersonAuthMapper;

    @Test
    public void issueAuth(){
        List<HikPerson> hikPeople = hikPersonMapper.selectList(Wrappers.<HikPerson>lambdaQuery().eq(HikPerson::getPersonType, HikPersonConstant.INTERNAL_STAFF));

        hikPeople.stream().forEach(hikPerson -> {
            List<HikPersonAuth> hikPersonAuths = hikPersonAuthMapper.selectList(Wrappers.<HikPersonAuth>lambdaQuery().eq(HikPersonAuth::getPersonId, hikPerson.getPersonId()));

            List<String> daoDeviceIds = hikPersonAuths.stream().map(HikPersonAuth::getDeviceId).collect(Collectors.toList());

                if(CollectionUtil.isNotEmpty(daoDeviceIds)){
                    String taskId = HaiKangTaskUtil.createTasks(5);
                    //向任务添加数据
                    HaiKangTaskUtil.putTaskData(taskId,daoDeviceIds,
                            hikPerson.getPersonId(),
                            null, null,0);
                    //下载任务
                    HaiKangTaskUtil.downloadTask(taskId);
                }

            });
    }


    @Test
    public void testmanyToMany(){
//        HiKUserUtil.delete(new String[]{"a882a322d8cc436085531f792c3bd7ec"});
        String taskId = HaiKangTaskUtil.createTasks(5);
        DateTime startTime = new DateTime();
        DateTime endTime = DateUtil.offset(startTime, DateField.YEAR,100);
        //向任务添加数据
        HaiKangTaskUtil.putTaskData(taskId,hikEquipmentService.queryAll(HikDeviceConstant.FACE_DEVICE)
                        .stream().map(HikDeviceDTO::getIndexCode).collect(Collectors.toList()),
                "testasdfadfasdfaasa",
                startTime, endTime,2);
        //下载任务
        HaiKangTaskUtil.downloadTask(taskId);
    }

    @Autowired
    private PlcClient plcClient;

    @Test
    public void testPlc(){
        plcClient.initConnect("192.168.70.152",6000);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(plcClient.hasChannelFuture("192.168.70.152"));
//        plcClient.openDoor("192.168.70.152","04");
        ChannelFuture channelFuture = plcClient.getChannelFuture("192.168.70.152");
        channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer(CrcUtils.hexStringToByte(String.format(PlcCommandConstant.OPEN_DOOR_COMMAND,"04"))));
//        channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer(CrcUtils.hexStringToByte(String.format(PlcCommandConstant.OPEN_DOOR_COMMAND,"0A"))));

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testUserMapper(){
        LambdaQueryWrapper<User> eq = Wrappers.<User>lambdaQuery().eq(User::getId, 1L).
                in(User::getNickName, Arrays.asList("adsf","Asdfasd")).eq(User::getUsername,"test''';delete from sys_user");

        List<User> byTest = userMapper.findByTest(eq);
        System.out.println(byTest);

    }

    @Autowired
    private HikPersonService hikPersonService;
    @Autowired
    private HikCardMapper hikCardMapper;

    @Autowired
    private HikCarAuthMapper hikCarAuthMapper;


    @Test
    public void testDelete(){
//        hikPersonService.syncDeletePerson();
        //删除卡片
        List<Card> cards = new ArrayList<>();

        cards.addAll(hikCardMapper.selectList(Wrappers.<Card>lambdaQuery()
                .eq(Card::getCardType, CardConstant.MANUFACTURER_S_CARD)));

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

//        HikCarEquipmentUtil.sendCarLedMsgDefaultConfig("5bda3cbaaf9746abb033a6e8ecdf97a4","请刷车牌或者车卡");
    }


    @Test
    public void testHik(){
        plcClient.sendMes("192.168.70.85","刷定位卡  ");
        String 刷定位卡 = CrcUtils.bytesToHexString(SubtitleMachineUtil.getCommand("刷定位卡  "));
        System.out.println(刷定位卡);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        ChannelFuture channelFuture = plcClient.getChannelFuture("192.168.70.85");
//        channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer(CrcUtils.hexStringToByte("024131A101044ED0CBA2B6A8CEBBBFA82020520D03")));
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }


    @Test
    public void testAccsic(){
        PersonCardUtil.locationCardBind("110101194203078672","1642599777");
    }
}
