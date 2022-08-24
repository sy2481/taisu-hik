package com.nbhy.modules.hik.constant;

/**
 * @Author: yyf
 * @Date: Created in 15:10 2022/1/24
 * @ClassName: RedisConstant
 * @Description:
 */
public class RedisConstant {

    /**
     * 厂商绑定定位卡key
     */
    public static final String MANUFACTURER_BOUND_POSITIONING_CARD_KEY = "MANUFACTURER_BOUND_POSITIONING_CARD:";

    /**
     * 厂商绑定定位卡有效期
     */
    public static final int MANUFACTURER_BOUND_POSITIONING_CARD_TIME = 60;


    /**
     * redis进出场(人员)的比对
     * 结构为 hash
     * key+人脸设备编号
     * face: personId
     * card: carNumber
     */
    public static final String IN_AND_OUT_PERSON_KEY = "IN_AND_OUT_PERSON_KEY:";

    /**
     * 进出人脸
     */
    public static final String IN_AND_OUT_PERSON_FACE = "face";

    /**
     * 进出定位卡
     */
    public static final String IN_AND_OUT_PERSON_CARD = "card";

    /**
     * 进出人员类型
     */
    public static final String IN_AND_OUT_PERSON_PERSON_TYPE = "personType";


    /**
     * 人员进出key的有效期
     */
    public static final int IN_AND_OUT_PERSON_KEY_VALIDITY_PERIOD = 60*10;

    /**
     * 再次处理同一个人/同一辆车的识别信息的时间
     */
    public static final int ANGIN_PERSON_KEY_VALIDITY_PERIOD = 15;

    /**
     * redis进出场(车辆)的比对
     * 结构为 hash
     * key+人脸设备编号
     * key的属性有以下：
     * face: personId
     * card: carNumber
     * personType： 人员类型
     * car: 车牌
     * carCard：车卡
     * orderSn: 厂商工单
     */
    public static final String IN_AND_OUT_CAR_KEY = "IN_AND_OUT_CAR_KEY:";

    /**
     * 进出人员id，用来判断是否15秒内多次刷
     */
    public static final String IN_AND_OUT_LIMITTIME_KEY = ":";


    /**
     * 进出人脸
     */
    public static final String IN_AND_OUT_CAR_FACE = "face";

    /**
     * 进出定位卡
     */
    public static final String IN_AND_OUT_CAR_CARD = "card";

    /**
     * 进出人员类型
     */
    public static final String IN_AND_OUT_CAR_PERSON_TYPE = "personType";


    /**
     * 车辆进出key的有效期
     */
    public static final int IN_AND_OUT_CAR_KEY_VALIDITY_PERIOD = 60*10;

    /**
     * 进出车牌
     */
    public static final String IN_AND_OUT_KEY_CAR = "car";

    /**
     * 进出车卡
     */
    public static final String IN_AND_OUT_KEY_CAR_CARD = "carCard";


    /**
     * 如果是内部员工，存储内部员工personId
     * 如果是厂商员工，存储工单号
     */
    public static final String IN_AND_OUT_KEY_CAR_SN = "carSn";








    private static final String delimiter = "<->";
    //工安
    public static final String PROFSID = "XT";

    //PLC-Car  key:"PLC-IP"+车牌号 value：
    public static final String PLC_IP_CAR_PASS = "PLC-IP";

    //进出信息
    public static final String PLC_IN_OUT = "PLC-InOut";


    /**
     * 字幕机清除命令KEY
     */
    public static final String SUBTITLE_MACHINE_CLEAN_KEY = "SUBTITLE_MACHINE_CLEAN_KEY:";

    /**
     * 字幕机过期时间
     */
    public static final long SUBTITLE_MACHINE_CLEAN_TIME = 5;





    public static String getVhNoKey(String VhNo,String date){
        return VhNo+delimiter+date;
    }

    public static String getPlcIpValue(String carNo,String indexCode){
        return carNo+delimiter+indexCode;
    }

    public static String getPlcIpKey(String carNo){
        return PLC_IP_CAR_PASS+delimiter+carNo;
    }

    public static String getPlcIndexCode(String value){
        String[] split = value.split(delimiter);
        if (split.length<2){
            return null;
        }
        return split[1];
    }

    public static String getCarNo(String value){
        String[] split = value.split(delimiter);
        if (split.length<2){
            return null;
        }
        return split[0];
    }


    public static String getPlcInOutKey(String eqIp){
        return PLC_IN_OUT+delimiter+eqIp;
    }

    public static String getProfsIdKey(String eqIp){
        return PROFSID+delimiter+eqIp;
    }

}
