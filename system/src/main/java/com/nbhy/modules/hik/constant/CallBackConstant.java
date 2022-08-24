package com.nbhy.modules.hik.constant;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: yyf
 * @Date: Created in 14:55 2022/1/21
 * @ClassName: CallBackConstant
 * 人脸认证通过 196893
 * 合法卡比对通过 198914
 * 人证比对通过 197162
 * @Description:
 */
public class CallBackConstant {
    public static final String ACCESS_CONTROL_EVENT_CALLBACK = "event_acs";


    public static final String CAR_EVENT_CALLBACK = "event_pms";

    public static final String FMS_CAR_EVENT_CALLBACK = "event_pms";


    public static final String VISIT_EVENT_CALLBACK = "event_visitor";

    /**
     * 人脸验证通过事件
     */
    public static final int ACCESS_FACE_EVENT_TYPE = 196893;


    /**
     * 卡加密失败事件
     */
    public static final int CARD_ENCRYPTION_FAILED_EVENT_TYPE = 261952;


    /**
     * 无此卡号
     */
    public static final int NO_SUCH_CARD_NUMBER_EVENT_TYPE = 197634;

}
