package com.nbhy.modules.erp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author: xcjx
 * @Email: nizhaobudaowo@163.com
 * @Company: nbhy
 * @Date: Created in 21:11 2022/3/13
 * @ClassName: InAndOutLog
 * @Description: 进出日志
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
public class InAndOutLog {
    /**
     * 身份证号
     */
    private String idCardNo;

    /**
     * 定位卡编号
     */
    private String locationCardNo;

    /**
     * 设备IP
     */
    private String equipmentIp;

    /**
     * 0-入场，1-离场
     */
    private String logType;

    /**
     * 車牌號或車卡
     */
    private String carParam;
}
