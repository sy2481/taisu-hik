
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.hikvision.artemis.sdk.config.ArtemisConfig;
import com.nbhy.exception.BadRequestException;
import com.nbhy.modules.hik.exception.HikException;
import com.nbhy.modules.hik.util.HaiKangTaskUtil;
import com.nbhy.modules.hik.util.HiKBaseUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventSubscriptionView {

    public static String eventSubscriptionView() {

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
        final String previewURLsApi = ARTEMIS_PATH + "/api/eventService/v1/eventSubscriptionView";
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
        String body = jsonBody.toJSONString();
        /**
         * STEP6：调用接口
         */
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, contentType, null);// post请求application/json类型参数
        return result;
    }

    public static String task() {

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
        final String previewURLsApi = ARTEMIS_PATH + "/api/acps/v1/authDownload/task/addition";
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
        jsonBody.put("taskType", 5);
        String body = jsonBody.toJSONString();
        /**
         * STEP6：调用接口
         */
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, contentType, null);// post请求application/json类型参数
        return result;
    }


    public static String addtask(String taskId) {

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
        final String previewURLsApi = ARTEMIS_PATH + "/api/acps/v1/authDownload/data/addition";
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
        jsonBody.put("taskId", taskId);

        JSONArray resourceInfos = new JSONArray();
        List<String> auth = new ArrayList<>();
        auth.add("8c851665d3f4442d9bea083e0233483f");
        auth.stream().forEach(obj -> {
            JSONObject resourceInfo = new JSONObject();
            resourceInfo.put("resourceIndexCode", obj);
            resourceInfo.put("resourceType", "acsDevice");
            JSONArray channelNos = new JSONArray();
            channelNos.add(1);
            resourceInfo.put("channelNos", channelNos);
            resourceInfos.add(resourceInfo);
        });

        List<PersonInfo> personInfos=new ArrayList<>();
        PersonInfo personInfo = new PersonInfo();
        personInfo.setPersonId("330203198903010617");
        personInfo.setOperatorType(2);
        DateTime time = new DateTime();
        DateTime startTime = DateUtil.offset(time, DateField.YEAR, -2);
        DateTime endTime = DateUtil.offset(startTime, DateField.YEAR, 30);
        personInfo.setStartTime(DateUtil.format(startTime, "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
        personInfo.setEndTime(DateUtil.format(endTime, "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
        personInfos.add(personInfo);
        PersonInfo personInfo1 = new PersonInfo();
        personInfo1.setPersonId("372922198510284437");
        personInfo1.setOperatorType(1);

        personInfo1.setStartTime(DateUtil.format(startTime, "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
        personInfo1.setEndTime(DateUtil.format(endTime, "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
        //personInfos.add(personInfo1);
        jsonBody.put("resourceInfos", resourceInfos);
        jsonBody.put("personInfos",personInfos);
//        jsonBody.put("personInfos", new Object[]{
//                new HashMap<String, Object>(4) {
//                    {
//                        put("personId", "330203198903010617");
//                        put("operatorType", 1);
//                        DateTime time = new DateTime();
//                        DateTime startTime = DateUtil.offset(time, DateField.YEAR, -9);
//                        DateTime endTime = DateUtil.offset(startTime, DateField.YEAR, 100);
//                        if (startTime != null && endTime != null) {
//                            put("startTime", DateUtil.format(startTime, "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
//                            put("endTime", DateUtil.format(endTime, "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
//                        }
//                    }
//                }
//        });
        String body = jsonBody.toJSONString();
        System.out.println("String body===>" + body);
        /**
         * STEP6：调用接口
         */
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, contentType, null);// post请求application/json类型参数
        System.out.println("task result" + result);
        return result;
    }

    public static String starttask(String taskId) {

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
        final String previewURLsApi = ARTEMIS_PATH + "/api/acps/v1/authDownload/task/start";
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
        jsonBody.put("taskId", taskId);


        String body = jsonBody.toJSONString();
        /**
         * STEP6：调用接口
         */
        String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, contentType, null);// post请求application/json类型参数
        return result;
    }


    public static void main(String[] args) {
//        String result = eventSubscriptionView();
//        System.out.println("result结果示例: " + result);

        String result = task();
        JSONObject json = JSON.parseObject(result);
        if ("0".equals(json.get("code"))) {
            String taskId = json.getJSONObject("data").get("taskId").toString();
            System.out.println(taskId);
            addtask(taskId);
            starttask(taskId);
        } else {

        }

        //创建任务
        //String taskId = HaiKangTaskUtil.createTasks(5);
        String taskId = null;
//        Map<String, String> path = HiKBaseUtil.getPath("/api/acps/v1/authDownload/task/addition");
//        String contentType = "application/json";
//        JSONObject jsonBody = new JSONObject();
//        jsonBody.put("taskType",5);
//        //log.info("taskType：{}",jsonBody.toString());
//        String result = ArtemisHttpUtil.doPostStringArtemis(path, jsonBody.toJSONString(), null, null, contentType , null);// post请求application/json类型参数


    }

    public static class PersonInfo {
        private String personId;
        private int operatorType;
        private String startTime;
        private String endTime;

        public String getPersonId() {
            return personId;
        }

        public void setPersonId(String personId) {
            this.personId = personId;
        }

        public int getOperatorType() {
            return operatorType;
        }

        public void setOperatorType(int operatorType) {
            this.operatorType = operatorType;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }
    }
}
