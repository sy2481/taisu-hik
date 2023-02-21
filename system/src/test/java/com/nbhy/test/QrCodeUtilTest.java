package com.nbhy.test;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.nbhy.modules.plc.client.PlcClient;
import com.nbhy.utils.BaseHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.junit.Test;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class QrCodeUtilTest {

    @Test
    public void test(){
        //其中0B就是继电器槽位
        String pclcommon = "00 FF 0A 00 0B 00 00 00 20 4D 01 00";


        HttpRequest request = HttpUtil.createGet("http://124.222.171.172:8080" + "/api/equipment/getAll");
        request.form("inOutType","ENTER");
        request.form("idCard","asf");
        BaseHttpUtil.setTimeOut(request);
        HttpResponse execute = request.execute();
        System.out.println(execute.body());
    }


    @Test
    public void testImge()throws Exception{

    }


    @Test
    public void test1(){
        
    }


    @Test
    public void testCode()throws Exception{
        PlcClient plcClient = new PlcClient();
        plcClient.initConnect("192.168.70.152",6000);

        plcClient.openDoor("192.168.70.152","20");
    }







}
