package com.nbhy.modules.hik.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 海康门禁记录查询条件
 */
@Data
@Builder
public class DoorRecordQueryCondition {
    //事件开始时间，采用ISO8601时间格式，与endTime配对使用，不能单独使用，时间范围最大不能超过3个月
    private String startTime;

    //事件结束时间，采用ISO8601时间格式，最大长度32个字符，与startTime配对使用，不能单独使用，时间范围最大不能超过3个月
    private String endTime;

    //事件类型
    private int eventType;

    //人员姓名
    private String personName;

    //人员数组人员Id，最大支持10个人员搜索
    private List<String> personIds;

    //门禁点名称
    private String doorName;

    //门禁点唯一标识 最大支持10个门禁点
    private List<String> doorIndexCodes;

    //门禁点所在区域
    private String doorRegionIndexCode;

    //支持personName、doorName、eventTime填写排序的字段名称，例如：”personName”
    private String sort;

    //指定排序字段是使用升序（asc）还是降序（desc），例如：”asc”
    private String order;

    //pageNo>0
    private int pageNo;

    //0<pageSize<=1000
    private int pageSize;

}
