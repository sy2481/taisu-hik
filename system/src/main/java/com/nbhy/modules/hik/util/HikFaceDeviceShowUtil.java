package com.nbhy.modules.hik.util;

import com.nbhy.modules.plc.client.PlcMessageSocket;
import com.nbhy.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HikFaceDeviceShowUtil {
    private final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(5, 15, 5000, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1000), Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());


    /**
     * 根据Ip調用字幕
     *
     * @param ip               设备ip
     * @param name             卡用户
     * @param tips             字幕消息
     * @param showSubtitleCard 卡号
     */
    public static HKSDK_MsgResult<String> showSubtitle(String ip, String name, String tips, String showSubtitleCard) throws UnsupportedEncodingException {

        HKSDK_MsgResult<String> msgResult = new HKSDK_MsgResult<>();
        //HKSDK_Device Device = GetDeviceByIp(Ip);//根据设备Ip去拿到已经布防的这个Ip的设备对象  后续通过ISAPI指令给他下发字幕消息
        if (SdkConfig.sdkInstance.get(ip) == null) {
            return null;
        }
        HCNetSDK.NET_DVR_XML_CONFIG_INPUT struInput = new HCNetSDK.NET_DVR_XML_CONFIG_INPUT();
        HCNetSDK.NET_DVR_XML_CONFIG_OUTPUT struOuput = new HCNetSDK.NET_DVR_XML_CONFIG_OUTPUT();
        struInput.read();
        struOuput.read();

        struInput.dwSize = struInput.size();
        String url = "PUT /ISAPI/AccessControl/uiShowCustom?format=json";

        HCNetSDK.BYTE_ARRAY ptrUrl = new HCNetSDK.BYTE_ARRAY(256);
        System.arraycopy(url.getBytes(), 0, ptrUrl.byValue, 0, url.length());
        ptrUrl.write();
        struInput.lpRequestUrl = ptrUrl.getPointer();
        struInput.dwRequestUrlLen = url.length();


        //    System.out.println("inputURL:" + new String(ptrUrl.byValue));
        byte[] Name = name.getBytes("utf-8");
        byte[] Tips = tips.getBytes("utf-8");


        String strInBuffer1 = "{\"name\":\"";
        String strInBuffer2 = "\",\"card\":\"" + showSubtitleCard + "\",\"tips\":\"";
        String strInBuffer3 = "\"}";


        int iStringSize = Name.length + Tips.length + strInBuffer1.length() + strInBuffer2.length() + strInBuffer3.length();

        HCNetSDK.BYTE_ARRAY ptrByte = new HCNetSDK.BYTE_ARRAY(iStringSize);
        System.arraycopy(strInBuffer1.getBytes(), 0, ptrByte.byValue, 0, strInBuffer1.length());
        System.arraycopy(Name, 0, ptrByte.byValue, strInBuffer1.length(), Name.length);
        System.arraycopy(strInBuffer2.getBytes(), 0, ptrByte.byValue, strInBuffer1.length() + Name.length, strInBuffer2.length());
        System.arraycopy(Tips, 0, ptrByte.byValue, strInBuffer1.length() + Name.length + strInBuffer2.length(), Tips.length);
        System.arraycopy(strInBuffer3.getBytes(), 0, ptrByte.byValue, strInBuffer1.length() + Name.length + strInBuffer2.length() + Tips.length, strInBuffer3.length());


        ptrByte.write();
        //System.out.println(new String(ptrByte.byValue));


        struInput.lpInBuffer = ptrByte.getPointer();
        struInput.dwInBufferSize = iStringSize;
        struInput.write();

        struOuput.dwSize = struOuput.size();
        HCNetSDK.BYTE_ARRAY ptrOutByte = new HCNetSDK.BYTE_ARRAY(10 * 1024);
        struOuput.lpOutBuffer = ptrOutByte.getPointer();
        struOuput.dwOutBufferSize = 10 * 1024;
        struOuput.write();


        if (!SdkConfig.hCNetSDK.NET_DVR_STDXMLConfig(SdkConfig.sdkInstance.get(ip), struInput, struOuput)) {
            //System.out.println("NET_DVR_STDXMLConfig failed,error:" + SdkConfig.hCNetSDK.NET_DVR_GetLastError());
            msgResult.msg = "系统内部错误";
        } else {
            //System.out.println("NET_DVR_STDXMLConfig succeed");
            struOuput.read();
            ptrOutByte.read();
            //System.out.println("lpOutBuffer:" + new String(ptrOutByte.byValue).trim());
            msgResult.isSuccess = true;
            msgResult.msg = "根据Ip調用字幕:" + tips;
            msgResult.data = tips;
        }
        return msgResult;
    }

    /**
     * 根据Ip調用字幕
     *
     * @param ip                设备ip
     * @param subtitleMachineIp 字幕機IP
     * @param tips              字幕消息
     */
    public HKSDK_MsgResult<String> showTips(String ip, String subtitleMachineIp, String tips) throws UnsupportedEncodingException {
        if (!StringUtils.isEmpty(subtitleMachineIp)&&(!subtitleMachineIp.equals("127.0.0.1"))) {
            threadPool.execute(() -> {
                PlcMessageSocket.sentMessage(subtitleMachineIp, tips);
            });

        }
        HKSDK_MsgResult<String> result = new HKSDK_MsgResult<>();
        if (!StringUtils.isEmpty(ip)) {
            result = this.showSubtitle(ip, "", tips, "");
        }
        return result;
    }
}
