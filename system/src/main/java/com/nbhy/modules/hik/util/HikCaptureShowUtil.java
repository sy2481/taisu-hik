package com.nbhy.modules.hik.util;

import com.nbhy.modules.plc.client.PlcSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class HikCaptureShowUtil {
    /**
     * 根据Ip調用字幕
     *
     * @param ip               设备ip
     * @param name             卡用户
     * @param tips             字幕消息
     * @param showSubtitleCard 卡号
     */
    private static Logger logger = LoggerFactory.getLogger(HikCaptureShowUtil.class);

    public static void showSubtitle(String ip, String tip,String carNumber, String showIp) {

        HKSDK_MsgResult<String> msgResult = new HKSDK_MsgResult<>();
        //HKSDK_Device Device = GetDeviceByIp(Ip);//根据设备Ip去拿到已经布防的这个Ip的设备对象  后续通过ISAPI指令给他下发字幕消息
        if (SdkConfig.sdkInstance.get(ip) == null) {
            return;
        }
        HCNetSDK.NET_DVR_XML_CONFIG_INPUT struInput = new HCNetSDK.NET_DVR_XML_CONFIG_INPUT();
        HCNetSDK.NET_DVR_XML_CONFIG_OUTPUT struOuput = new HCNetSDK.NET_DVR_XML_CONFIG_OUTPUT();
        struInput.read();
        struOuput.read();

        struInput.dwSize = struInput.size();
        String url = "PUT /ISAPI/Parking/channels/1/LEDConfigurations/multiScene/1?format=json";

        HCNetSDK.BYTE_ARRAY ptrUrl = new HCNetSDK.BYTE_ARRAY(256);
        System.arraycopy(url.getBytes(), 0, ptrUrl.byValue, 0, url.length());
        ptrUrl.write();
        struInput.lpRequestUrl = ptrUrl.getPointer();
        struInput.dwRequestUrlLen = url.length();


        //    System.out.println("inputURL:" + new String(ptrUrl.byValue));
        // byte[] Name = name.getBytes("utf-8");
        byte[] tips = null;
        byte[] carNumbers=null;
        try {
             tips = tip.getBytes("utf-8");
            carNumbers=carNumber.getBytes("utf-8");
        } catch (Exception e) {

        }
        String strInBuffer1 = "{\"SingleSceneLEDConfigurations\":{\"sid\":1,\"mode\":\"passingVehicle\",\"showFreeEnabled\":true,\"displayTime\":240,\"vehicleDisplayEnabled\":true,\"allowListDisplayEnabled\":false,\"blockListDisplayEnabled\":false,\"temporaryListDisplayEnabled\":false,\"ctrlMode\":\"camera\",\"LEDConfigurationList\":[{\"LEDConfiguration\":{\"id\":1,\"enabled\":true,\"ShowInfoList\":[{\"ShowInfo\":{\"id\":1,\"fontSize\":16,\"fontColor\":\"green\",\"speedType\":\"slow\",\"displayMode\":\"left\",\"LineInfoList\":[{\"LineInfo\":{\"id\":1,\"customValue\":\"";
        String strInBuffer2 = "\"}}],\"content\":\"";
        String strInBuffer3 = "\"}},{\"ShowInfo\":{\"id\":2,\"fontSize\":16,\"fontColor\":\"green\",\"speedType\":\"medium\",\"displayMode\":\"left\",\"LineInfoList\":[{\"LineInfo\":{\"id\":1,\"customValue\":\"";
        String strInBuffer4="\"}}],\"content\":\"";
        String strInBuffer5="\"}}]}}],\"LedInfo\":{\"communicateMode\":\"network\",\"networkCtrlInfo\":{\"ipaddr\":\"" + showIp + "\",\"portNo\":10000}}}}";


//        String strInBuffer1 = "{\"name\":\"";
//        String strInBuffer2 = "\",\"card\":\"" + showSubtitleCard + "\",\"tips\":\"";
//        String strInBuffer3 = "\"}";


        int iStringSize = tips.length + tips.length + strInBuffer1.length() + strInBuffer2.length() + strInBuffer3.length()+carNumbers.length+carNumbers.length+strInBuffer4.length()+strInBuffer5.length();

        HCNetSDK.BYTE_ARRAY ptrByte = new HCNetSDK.BYTE_ARRAY(iStringSize);
        System.arraycopy(strInBuffer1.getBytes(), 0, ptrByte.byValue, 0, strInBuffer1.length());
        System.arraycopy(tips, 0, ptrByte.byValue, strInBuffer1.length(), tips.length);
        System.arraycopy(strInBuffer2.getBytes(), 0, ptrByte.byValue, strInBuffer1.length() + tips.length, strInBuffer2.length());
        System.arraycopy(tips, 0, ptrByte.byValue, strInBuffer1.length() + tips.length + strInBuffer2.length(), tips.length);
        System.arraycopy(strInBuffer3.getBytes(), 0, ptrByte.byValue, strInBuffer1.length() + tips.length + strInBuffer2.length() + tips.length, strInBuffer3.length());
        System.arraycopy(carNumbers, 0, ptrByte.byValue, strInBuffer1.length() + tips.length + strInBuffer2.length() + tips.length+strInBuffer3.length(),carNumbers.length);
        System.arraycopy(strInBuffer4.getBytes(), 0, ptrByte.byValue, strInBuffer1.length() + tips.length + strInBuffer2.length() + tips.length+strInBuffer3.length()+carNumbers.length,strInBuffer4.length());
        System.arraycopy(carNumbers, 0, ptrByte.byValue, strInBuffer1.length() + tips.length + strInBuffer2.length() + tips.length+strInBuffer3.length()+carNumbers.length+strInBuffer4.length(),carNumbers.length);
        System.arraycopy(strInBuffer5.getBytes(), 0, ptrByte.byValue, strInBuffer1.length() + tips.length + strInBuffer2.length() + tips.length+strInBuffer3.length()+carNumbers.length+strInBuffer4.length()+carNumbers.length,strInBuffer5.length());


      //  System.arraycopy(strInBuffer1.getBytes(), 0, ptrByte.byValue, 0, strInBuffer1.length());
        ptrByte.write();
        logger.info("ptrByte.byValue-->" + new String(ptrByte.byValue));


        struInput.lpInBuffer = ptrByte.getPointer();
        struInput.dwInBufferSize = iStringSize;
        struInput.write();

        struOuput.dwSize = struOuput.size();
        HCNetSDK.BYTE_ARRAY ptrOutByte = new HCNetSDK.BYTE_ARRAY(10 * 1024);
        struOuput.lpOutBuffer = ptrOutByte.getPointer();
        struOuput.dwOutBufferSize = 10 * 1024;
        struOuput.write();


        if (!SdkConfig.hCNetSDK.NET_DVR_STDXMLConfig(SdkConfig.sdkInstance.get(ip), struInput, struOuput)) {
            logger.info("NET_DVR_STDXMLConfig failed,error:" + SdkConfig.hCNetSDK.NET_DVR_GetLastError());
            msgResult.msg = "系统内部错误";
        } else {
            logger.info("NET_DVR_STDXMLConfig succeed");
            struOuput.read();
            ptrOutByte.read();

            logger.info("lpOutBuffer:" + new String(ptrOutByte.byValue).trim());
            msgResult.isSuccess = true;
            msgResult.msg = "根据Ip調用字幕:" + tips;
            msgResult.data = tips.toString();
        }
        return;
    }
}
