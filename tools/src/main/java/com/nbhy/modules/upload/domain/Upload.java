package com.nbhy.modules.upload.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@ToString
@TableName("upload")
public class Upload {
    @ApiModelProperty(value = "ID", hidden = true)
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @TableField("file_path")
    @ApiModelProperty("图片地址")
    private String filePath;

    @TableField(value = "create_time",fill = FieldFill.INSERT)
    private Timestamp createTime;

    @TableField("enabled")
    @ApiModelProperty("是否有效。如果为false则表示图片无效")
    private Boolean enabled;

//    @TableField("storage_type")
//    @ApiModelProperty("存储类型,0代表本地存储 1代表oss存储")
//    private Byte storageType;
}
