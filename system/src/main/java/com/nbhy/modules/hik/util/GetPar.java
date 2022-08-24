package com.nbhy.modules.hik.util;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.hikvision.artemis.sdk.config.ArtemisConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetPar {

    public static String GetCameraPreviewURL() {

        /**
         * STEP1：设置平台参数，根据实际情况,设置host appkey appsecret 三个参数.
         */
        ArtemisConfig.host = "192.168.10.16:443"; // 平台的ip端口
        ArtemisConfig.appKey = "28597477";  // 密钥appkey
        ArtemisConfig.appSecret = "1ugtioooqS6uJDYaBlH1";// 密钥appSecret

        /**
         * STEP2：设置OpenAPI接口的上下文
         */
        final String ARTEMIS_PATH = "/artemis";

        /**
         * STEP3：设置接口的URI地址
         */
        // String previewURLsApi = ARTEMIS_PATH + "/api/video/v1/cameras/previewURLs";
//       final String previewURLsApi=ARTEMIS_PATH+"/api/pms/v1/park/deviceRelation/search";
//       final  String previewURLsApi=ARTEMIS_PATH+"/api/resource/v1/park/search";
        final String previewURLsApi=ARTEMIS_PATH+"/api/resource/v1/person/condition/personInfo";
//        final String previewURLsApi=ARTEMIS_PATH+"/api/irds/v2/deviceResource/resources";
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", previewURLsApi);//根据现场环境部署确认是http还是https
            }
        };

        /**
         * STEP4：设置参数提交方式
         */
        String contentType = "application/json";

        /**
         * STEP5：组装请求参数
         */
        JSONObject jsonBody = new JSONObject();
//        jsonBody.put("parentResourceType","parking");
//        jsonBody.put("pageSize",100);
//        jsonBody.put("pageNo",1);
//        jsonBody.put("syscode",);
//        jsonBody.put("nodeType",1);
//        jsonBody.put("deviceType","displayDevice");
        //jsonBody.put("deviceSyscode", "71cfcb55b1dc48449f8016d6ffaa690b");

//        String[] messageIndex = StrUtil.split("test test", 4);
//        List<JSONObject> dataList = new ArrayList<>();
//        //for (int i = 0; i < messageIndex.length; i++) {
//            //final int line = i+1;
//            dataList.add(new JSONObject(){{
//                put("line",1);
//                put("fontConfig","[1,1,1]");
//                put("showConfig","testtest");
//            }});
////        }
//        jsonBody.put("ledContent",dataList);

//        jsonBody.put("streamType", 0);
//        jsonBody.put("protocol", "rtsp");
//        jsonBody.put("transmode", 1);
//        jsonBody.put("expand", "streamform=ps");
       // Map<String, String> path = HiKBaseUtil.getPath("/api/resource/v1/person/condition/personInfo");
        //String contentType = "application/json";
       // JSONObject jsonBody = new JSONObject();
        jsonBody.put("paramName","personId");
        jsonBody.put("paramValue",new String[]{"410727199909152010"});
//        jsonBody.put("pageNo",1);
//        jsonBody.put("pageSize",100);
//        jsonBody.put("resourceType","acsDevice");
        String result = ArtemisHttpUtil.doPostStringArtemis(path, jsonBody.toJSONString(), null, null, contentType , null);// post请求application/json类型参数
        //log.info("根据id查询,获取到的信息为>>>>>>>>>>>>>>>>>{}",result);
        JSONObject json = JSON.parseObject(result);
//        if("0".equals(json.getString("code"))){
//            return json;
//        }
        return null;

       // String body = jsonBody.toJSONString();
        /**
         * STEP6：调用接口
         */
        //String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, contentType , null);// post请求application/json类型参数
        //return result;
    }

    public static void main(String[] args) {
        String result = GetCameraPreviewURL();
        System.out.println("result结果示例: " + result);
    }
}

