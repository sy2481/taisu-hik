import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nbhy.modules.hik.util.HiKUserUtil;

public class JsonTest {
    public static void main(String[] args) {
//        String result="{\"code\":\"0\",\"msg\":\"success\",\"data\":{\"total\":1,\"list\":[{\"personId\":\"41232519630520483X\",\"personName\":\"馮傳軍\",\"gender\":0,\"orgIndexCode\":\"5682f227-f8c8-48a0-b71c-14b96b6ee677\",\"certificateType\":990,\"certificateNo\":\"1553272221751971840\",\"age\":0,\"marriaged\":0,\"lodge\":0,\"syncFlag\":0,\"pinyin\":\"fengchuanjun\",\"createTime\":\"2022-07-30T14:48:43.194+08:00\",\"updateTime\":\"2022-07-30T14:48:43.188+08:00\",\"faceNum\":1,\"fingerprintNum\":0,\"orgName\":\"厂商员工\",\"orgPath\":\"@root000000@5682f227-f8c8-48a0-b71c-14b96b6ee677@\",\"orgPathName\":\"台塑宁波/厂商员工\",\"orgList\":[\"5682f227-f8c8-48a0-b71c-14b96b6ee677\"],\"personPhoto\":[{\"serverIndexCode\":\"97be2da4-e5f5-40c1-9f79-7244dce30679\",\"personPhotoIndexCode\":\"bd4994c6-536e-469a-9343-4aa560da971a\",\"picUri\":\"/pic?0dbf00=1710ip-4eo221-233*9o7=4=4318*3l2397921166*5t7=0*2ps==610b*=134c*6e63b4243-91393c-4*l1bcod053e1=004\"}]}]}}";
//        JSONObject json = JSON.parseObject(result);
//        JSONArray jsonArray = null;
//        if("0".equals(json.getString("code"))){
//            jsonArray = json.getJSONObject("data").getJSONArray("list");
//        }
//        //String string = jsonArray.getJSONObject(0).getJSONArray("personPhoto").getJSONObject(0).getString("serverIndexCode");
//
//        String string1 = jsonArray.getJSONObject(0).getJSONArray("personPhoto").getJSONObject(0).getString("serverIndexCode");
//        String string = jsonArray.getJSONObject(0).getJSONObject("personPhoto").getString("serverIndexCode");
//
//        //hikPerson.setFaceId(string);
//        System.out.println(string1);
//
//        System.out.println(json.getJSONArray("personPhoto").getJSONObject(0).getString("serverIndexCode"));

        String ip1="192.168.100.151";
        String ip2="192.168.60.151";
        String ip3="192.168.70.151";
        String ip4="192.168.80.151";
        String ip5="192.168.90.151";
        String ip6="192.168.110.151";
        String ip7="192.168.50.151";
        changeIP(ip1);
        changeIP(ip2);
        changeIP(ip3);
        changeIP(ip4);
        changeIP(ip5);
        changeIP(ip6);
        changeIP(ip7);
        //changeIP(ip1);
    }

    public static String changeIP(String ip){
       String r= ip.replace("0.","").replace(".","").substring(6);
      String a= r.substring(0,2)+r.substring(3);
        System.out.println( r);
        return r;
    }
}
