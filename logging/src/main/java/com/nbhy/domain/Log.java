package com.nbhy.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;
import java.sql.Timestamp;

@Getter
@Setter
@TableName("sys_log")
@NoArgsConstructor
public class Log  implements Serializable {

    @TableId(value = "log_id",type = IdType.AUTO)
    private Long id;

    /** 操作用户 */
    @TableField("username")
    private String username;

    /** 描述 */
    @TableField("description")
    private String description;

    /** 方法名 */
    @TableField("method")
    private String method;

    /** 参数 */
    @TableField("params")
    private String params;

    /** 日志类型 */
    @TableField("log_type")
    private String logType;

    /** 请求ip */
    @TableField("request_ip")
    private String requestIp;

    /** 地址 */
    @TableField("address")
    private String address;

    /** 浏览器  */
    @TableField("browser")
    private String browser;

    /** 请求耗时 */
    @TableField("time")
    private Long time;

    /** 异常详细  */
    @TableField(value = "exception_detail",jdbcType = JdbcType.VARBINARY)
    private byte[] exceptionDetail;

    /** 创建日期 */
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private Timestamp createTime;

    public Log(String logType, Long time) {
        this.logType = logType;
        this.time = time;
    }
}
