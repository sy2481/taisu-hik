package com.nbhy.modules.hik.domain.dto;

import lombok.Data;

@Data
public class HikDept {
    private String orgIndexCode;
    private String orgNo;
    private String orgName;
    private String orgPath;
    private String parentOrgIndexCode;
    private String parentOrgName;
}
