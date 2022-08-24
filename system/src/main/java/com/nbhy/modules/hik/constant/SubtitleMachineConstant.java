package com.nbhy.modules.hik.constant;

/**
 * @Author: xcjx
 * @Email: nizhaobudaowo@163.com
 * @Company: nbhy
 * @Date: Created in 14:09 2022/3/16
 * @ClassName: SubtitleMachineConstant
 * @Description: 字幕机常量
 * @Version: 1.0
 */
public class SubtitleMachineConstant {

    /**
     *
     * 02 41 31 A1 01 04 41 C0 20 20 20 20 20 20 20 20 20 20 52 07 03
     * 1:包头
     * 2-3:站号
     * 4：表示传输文字
     * 5：行号
     * 6:  停留时间 s
     * 7：前功能码
     * 8：文字模式码
     * 9-18: 文字
     * 19：后功能码
     * 20：XOR校验
     * 21： 包尾
     */

    /**
     * 包头
     */
    public static final byte HEAD = (byte) 0x02;

    /**
     * 包尾
     */
    public static final byte END = (byte) 0x03;


    /**
     * 默认站号
     */

    public static final byte[] DEFAULT_STATION_NO = {(byte) 0x41,(byte) 0x31};


    /**
     * 设置站号
     */
    public static final byte[] SET_THE_STATION_NUMBER =  {HEAD,(byte) 0x41,(byte) 0x31,(byte) 0xAD,(byte) 0x41,(byte) 0x31,(byte) 0xAD, END};


    /**
     * 恢复行号
     */
    public static final byte[] RESTORE_LINE_NUMBER =  {HEAD,(byte) 0x41,(byte) 0x31,(byte) 0xA6,(byte) 0x01,(byte) 0x00,(byte) 0xD7,END};


    /**
     * 前置功能码
     * N 前功能
     */
    public static final byte DEFAULT_FORMER_FUNCTION = 0x4E;


    /**
     * 后置功能码
     * R 續幕
     */
    public static final byte DEFAULT_BACK_FUNCTION = 0x52;


    /**
     * 文字模式码
     */
    public static final byte FORNT_MODEL_CODE = (byte) 0xE0;


    /**
     * 消息默认格式
     * 02 41 31 A1 01 04 41 C0
     */
    public static final byte[] DEFAULT_SEND_FORMAT = {HEAD,DEFAULT_STATION_NO[0],DEFAULT_STATION_NO[1],(byte) 0xA1,(byte) 0x01,(byte) 0x04,DEFAULT_FORMER_FUNCTION,FORNT_MODEL_CODE};






}
