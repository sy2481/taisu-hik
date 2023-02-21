import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.hikvision.artemis.sdk.config.ArtemisConfig;
import com.nbhy.modules.hik.util.HiKBaseUtil;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationView2 {

    public static String eventSubscriptionView2() {

        /**
         * STEP1：设置平台参数，根据实际情况,设置host appkey appsecret 三个参数.
         */
        ArtemisConfig.host = "192.168.10.16:443"; // artemis网关服务器ip端口
        ArtemisConfig.appKey = "28597477";  // 秘钥appkey
        ArtemisConfig.appSecret = "1ugtioooqS6uJDYaBlH1";// 秘钥appSecret

        /**
         * STEP2：设置OpenAPI接口的上下文
         */
        final String ARTEMIS_PATH = "/artemis";

        /**
         * STEP3：设置接口的URI地址
         */
        final String previewURLsApi = ARTEMIS_PATH + "/api/resource/v1/face/single/delete";
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

        jsonBody.put("faceId", "d28b0b93-be95-4735-ad38-d6d270ed1a16");
      //  jsonBody.put("paramValue",new String[]{"330203198903010617"});
//        jsonBody.put("pageNo","1");
//        jsonBody.put("pageSize","20");
        String body = jsonBody.toJSONString();

        /**
         * STEP6：调用接口
         */
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, contentType , null);// post请求application/json类型参数
        return result;
    }

    public static void main(String[] args) {
        String result = eventSubscriptionView2();

//        JSONObject json = JSON.parseObject(result);
//
//       // JSONObject json = HiKUserUtil.queryById(new String[]{personId});
//
//        JSONArray jsonArray = null;
//
//        if ("0".equals(json.getString("code"))) {
//            jsonArray = json.getJSONObject("data").getJSONArray("list");
//        }
//
//        String pic=null;
//
//        try {
//            pic = jsonArray.getJSONObject(0).getJSONArray("personPhoto").getJSONObject(0).getString("personPhotoIndexCode");
//        } catch (Exception e) {
//
//        }

//        Map<String, String> path = HiKBaseUtil.getPath("/api/resource/v1/face/single/delete");
//        String contentType = "application/json";
//        JSONObject jsonBody = new JSONObject();
//        jsonBody.put("faceId",pic);
//        //log.info("删除用户人脸传输的参数为>>>>>>>>>>{}",jsonBody.toJSONString());
//        String result1 = ArtemisHttpUtil.doPostStringArtemis(path, jsonBody.toJSONString(), null, null, contentType , null);// post请求application/json类型参数
//       // log.info("删除用户人脸获取到的信息为>>>>>>>>>>>>>>>>>{}",result);
//        JSONObject json1 = JSON.parseObject(result1);

        System.out.println("result结果示例: " + result);
    }
}
