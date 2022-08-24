package com.nbhy.modules.plc.constant;

/**
 * @Author: xcjx
 * @Email: nizhaobudaowo@163.com
 * @Company: nbhy
 * @Date: Created in 10:18 2022/3/14
 * @ClassName: PlcCommandConstant
 * @Description: plc指令常量
 * @Version: 1.0
 */
public class PlcCommandConstant {

//    public static final String OPEN_DOOR_COMMAND = "00FF0A00%s000000204D0100";


    /**
     * 开门指令
     * 必须先下发关门指令，然后再下发开门指令
     */
//    public static final String OPEN_DOOR_COMMAND = "02FF0A00%s000000204D010010";
    public static final String OPEN_DOOR_COMMAND = "02FF0A00%s0000204D010010";

    /**
     * 关门指令
     * 复位指令。复位继电器的状态
     */
//    public static final String CLOSE_DOOR_COMMAND = "02FF0A00%s000000204D010000";
    public static final String CLOSE_DOOR_COMMAND = "02FF0A00%s0000204D010000";
}
