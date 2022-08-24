package com.nbhy.config.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.nbhy.utils.SecurityUtils;
import com.nbhy.utils.StringUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", Timestamp.class, new Timestamp(System.currentTimeMillis()));
        String currentUsername = null;
        try {
            currentUsername = SecurityUtils.getCurrentUsername();
        }catch (Exception e){}
        if(StringUtils.isNotBlank(currentUsername)){
            this.strictInsertFill(metaObject, "createBy", String.class,currentUsername);
        }else{
            this.strictInsertFill(metaObject, "createBy", String.class,"SYSTEM");
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", Timestamp.class, new Timestamp(System.currentTimeMillis()));
        String currentUsername = null;
        try {
            currentUsername = SecurityUtils.getCurrentUsername();
        }catch (Exception e){}
        if(StringUtils.isNotBlank(currentUsername)){
            this.strictUpdateFill(metaObject, "updateBy", String.class,currentUsername);
        }else{
            this.strictUpdateFill(metaObject, "updateBy", String.class,"SYSTEM");
        }

    }


}