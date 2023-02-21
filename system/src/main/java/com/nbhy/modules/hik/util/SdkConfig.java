package com.nbhy.modules.hik.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SdkConfig {
    public static HCNetSDK hCNetSDK = null;
    public static Map<String,Integer> sdkInstance= new ConcurrentHashMap<>();
}
