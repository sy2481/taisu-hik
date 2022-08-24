package com.nbhy.modules.system.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nbhy.base.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
public class UserDTO extends BaseDTO implements Serializable {

    @ApiModelProperty("用户ID")
    private Long id;

    @ApiModelProperty("角色集合")
    private Set<RoleSmallDTO> roles;

    @ApiModelProperty("登录用户名")
    private String username;

    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    @ApiModelProperty(value = "邮箱")
    private String email;


    @ApiModelProperty(value = "电话号码")
    private String phone;

    @ApiModelProperty(value = "用户性别")
    private String gender;

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private String password;

    @ApiModelProperty(value = "是否启用")
    private Boolean enabled;

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private Boolean isAdmin = false;

    @ApiModelProperty(value = "最后一次登录时间")
    private Date pwdResetTime;

    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

}
