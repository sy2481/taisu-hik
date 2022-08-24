package com.nbhy.modules.hik.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.nbhy.modules.hik.constant.HiKConfigConstant;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class HiKBaseUtil {
    /**
     * 获取接口地址
     * @param url 接口地址的相对路径
     * @return
     */
    public static Map<String,String> getPath(String url){

        /**
         * STEP3：设置接口的URI地址
         */
        final String previewURLsApi = HiKConfigConstant.ARTEMIS_PATH + url;
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put(HiKConfigConstant.HTTP_PREFIX, previewURLsApi);//根据现场环境部署确认是http还是https
            }
        };
        return path;
    }


    /**
     * 获取人脸的字节数组
     * @return
     */
    public static byte[] getImages(String path){
        String url = HiKConfigConstant.BASE_URL+path;
        HttpRequest get = HttpUtil.createGet(url);
        get.setReadTimeout(2000);
        get.setConnectionTimeout(2000);
        HttpResponse execute = get.execute();
        return execute.bodyBytes();
    }
}
