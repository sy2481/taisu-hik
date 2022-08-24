package com.nbhy.modules.upload.service;

import cn.hutool.core.date.DateUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

public interface StorageHandler {
    /**
     * 存储文件
     * @param file 文件
     * @return 文件网络地址
     */
    String storage(MultipartFile file) throws IOException;


    String storage(byte[] file,String fileSuffix) throws IOException;


    void del(String path);


    default String getFilePath(String fileSuffix){
        StringBuffer localPath = new StringBuffer();
        localPath.append(DateUtil.format(new Date(),"yyyy-MM"))
                .append(File.separator)
                .append(UUID.randomUUID().toString().replace("-",""))
                .append(System.currentTimeMillis())
                .append(".")
                .append(fileSuffix);
        return localPath.toString();
    }
}
