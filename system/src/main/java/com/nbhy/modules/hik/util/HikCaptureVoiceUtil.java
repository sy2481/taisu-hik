package com.nbhy.modules.hik.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HikCaptureVoiceUtil {
    private static Logger logger = LoggerFactory.getLogger(HikCaptureVoiceUtil.class);

    public static void showVoice(String ip, String tip) {
        try {
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

            String strInBuffer1 = "{\n" +
                    "  \"CombinateBroadcast\": {\n" +
                    "    \"Illegal\": {},\n" +
                    "    \"PedestrianAlert\": {},\n" +
                    "    \"volume\": 10,\n" +
                    "    \"volumeTimeEnabled\": false,\n" +
                    "    \"VolumeTimeBlockList\": [\n" +
                    "      {\n" +
                    "        \"TimeBlock\": {\n" +
                    "          \"id\": 1,\n" +
                    "          \"volume\": 5,\n" +
                    "          \"beginTime\": \"07:00\",\n" +
                    "          \"endTime\": \"21:00\"\n" +
                    "        }\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"TimeBlock\": {\n" +
                    "          \"id\": 2,\n" +
                    "          \"volume\": 2,\n" +
                    "          \"beginTime\": \"21:00\",\n" +
                    "          \"endTime\": \"07:00\"\n" +
                    "        }\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"ctrlMode\": \"platform\",\n" +
                    "    \"vehicleBroadcastEnabled\": true,\n" +
                    "    \"allowListBroadcastEnabled\": false,\n" +
                    "    \"blockListBroadcastEnabled\": false,\n" +
                    "    \"temporaryListBroadcastEnabled\": false,\n" +
                    "    \"BroadcastInfoList\": [\n" +
                    "      {\n" +
                    "        \"BroadcastInfo\": {\n" +
                    "          \"id\": 1,\n" +
                    "          \"customValue\": \"" + tip + "\"\n" +
                    "        }\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"enabled\": true\n" +
                    "  }\n" +
                    "}";

//        String strInBuffer1 = "{\"name\":\"";
//        String strInBuffer2 = "\",\"card\":\"" + showSubtitleCard + "\",\"tips\":\"";
//        String strInBuffer3 = "\"}";
            int iStringSize = 0;

            iStringSize = strInBuffer1.getBytes("UTF-8").length;

            HCNetSDK.BYTE_ARRAY ptrByte = new HCNetSDK.BYTE_ARRAY(iStringSize);
            System.arraycopy(strInBuffer1.getBytes("UTF-8"), 0, ptrByte.byValue, 0, iStringSize);


            //  System.arraycopy(strInBuffer1.getBytes(), 0, ptrByte.byValue, 0, strInBuffer1.length());
            ptrByte.write();
            //logger.info("ptrByte.byValue-->" + new String(ptrByte.byValue));


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
               
            }
            return;
        } catch (Exception e) {
            return;
        }
    }
}