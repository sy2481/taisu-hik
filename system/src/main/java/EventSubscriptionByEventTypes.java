
import com.alibaba.fastjson.JSONObject;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.hikvision.artemis.sdk.config.ArtemisConfig;
import java.util.HashMap;
import java.util.Map;

public class EventSubscriptionByEventTypes {

    public static String eventSubscriptionByEventTypes() {

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
        final String previewURLsApi = ARTEMIS_PATH + "/api/eventService/v1/eventSubscriptionByEventTypes";
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
        int[] arr = new int[]{261952,197634,196893};
        jsonBody.put("eventTypes", arr);
        jsonBody.put("eventDest", "http://192.168.10.44:11190/hik/event/door/callback");
//        int[] arr2=new  int[]{771760133,771760130};
//        jsonBody.put("eventTypes", arr2);
//        jsonBody.put("eventDest","http://192.168.10.44:11190/hik/event/car/callback");
        String body = jsonBody.toJSONString();
        /**
         * STEP6：调用接口
         */
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, contentType , null);// post请求application/json类型参数
        return result;
    }

    public static void main(String[] args) {
        String result = eventSubscriptionByEventTypes();
        System.out.println("result结果示例: " + result);
    }
}
