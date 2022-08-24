package com.nbhy.modules.hik.domain.dto;

import lombok.Data;

@Data
public class HikAcsDevEquipment {
    private String indexCode;
    private String name;
    private String resourceType;
    private String devTypeCode;
    private String devTypeDesc;
    private String ip;
    private String port;
    private String userName;
    private String regionIndexCode;
    private String treatyType;
    private String capability;
    private int cardCapacity;
    private int fingerCapacity;
    private int faceCapacity;
    private int doorCapacity;
    private String netZoneId;
    private int isCascade;
    private String dataVersion;
    private String comId;
    private String createTime;
    private String updateTime;
    private String manufacturer;
    private String acsReaderVerifyModeAbility;
    private String devSerialNum;
    private int sort;
    private int disOrder;
    private String picFileSizeMax;
    private String modelAlgorithms;
    private String modelFileSizeMax;
    private String regionName;
    private String regionPath;
}
