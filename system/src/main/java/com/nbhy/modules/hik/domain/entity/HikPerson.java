package com.nbhy.modules.hik.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * @Author: xcjx
 * @Email: nizhaobudaowo@163.com
 * @Company: nbhy
 * @Date: Created in 15:46 2022/3/10
 * @ClassName: HikPerson
 * @Description: 海康人员管理类
 * @Version: 1.0
 */
@Getter
@Setter
@TableName("hik_person")
public class HikPerson {

    @TableId(value = "person_id",type = IdType.INPUT)
    @ApiModelProperty("海康人员ID")
    private String personId;

    @TableField("person_name")
    @ApiModelProperty(value = "人员名称，1~32个字符；不能包含 ' / \\ : * ? \" < > | 这些特殊字符",required = true)
    private String personName;


    @TableField("gender")
    @ApiModelProperty("海康性别1：男；2：女；0：未知 ,不传默认0")
    private String gender = "0";

    @TableField("birthday")
    @ApiModelProperty("出生日期，举例：1992-09-12 不必填写")
    private String birthday;

    @TableField("phone_no")
    @ApiModelProperty(value = "手机号，1-20位数字,不必填写")
    private String phoneNo;

    @TableField("email")
    @ApiModelProperty(value = "邮箱，举例：hic@163.com")
    private String email;

    @TableField("certificate_type")
    @ApiModelProperty(value = "证件类型，111:身份证,414:护照,113:户口簿,335:驾驶证 131:工作证,133:学生证,990:其他 平台上人员信息实名标识选择为身份证件时必填")
    private String certificateType = "990";

    @TableField("certificate_no")
    @ApiModelProperty(value = "证件号码，1-20位数字字母，平台上人员信息实名标识选择为身份证件时必填")
    private String certificateNo;

    @TableField("job_no")
    @ApiModelProperty(value = "工号")
    private String jobNo;

    @TableField("face_id")
    @ApiModelProperty(value = "人脸ID")
    private String faceId;

    @TableField("person_type")
    @ApiModelProperty(value = "人员类型 0-内部员工、1-厂商员工、2-危化品")
    private Integer personType;


    @TableField("order_sn")
    @ApiModelProperty(value = "厂商订单号,当为厂商员工的时候必填")
    private String orderSn;


    @ApiModelProperty(value = "创建时间", hidden = true)
    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private Timestamp createTime;

}
