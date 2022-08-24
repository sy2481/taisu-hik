package com.nbhy.modules.hik.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: yyf
 * @Date: Created in 17:36 2022/1/24
 * @ClassName: TsPlcIpUtil
 * @Description:
 */
public class TsPlcIpUtil {
    public static List<String> getIpList(String ipString){
        String[] split = ipString.split(",");
        if (split.length<=0) {
            return new ArrayList<String>();
        }
        List<String> realIps = Arrays.stream(split).map(ip -> {
            String realIp = ip.replace(".7.", ".8.");
            return realIp;
        }).collect(Collectors.toList());
        return realIps;
    }
}
