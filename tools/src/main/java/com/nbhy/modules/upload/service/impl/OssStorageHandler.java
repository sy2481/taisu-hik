package com.nbhy.modules.upload.service.impl;

import cn.hutool.core.date.DateUtil;
import com.nbhy.modules.upload.config.OssProperties;
import com.nbhy.modules.upload.service.StorageHandler;
import com.nbhy.modules.upload.util.OssBootUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

public class OssStorageHandler implements StorageHandler {


    private final OssProperties ossProperties;

    public OssStorageHandler(OssProperties ossProperties){
        this.ossProperties = ossProperties;
    }

    @Override
    public String storage(MultipartFile file) throws IOException{
        StringBuffer localPath = new StringBuffer();
        localPath.append(ossProperties.getFilePrefix());
        localPath.append(getFilePath(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1)));
        return OssBootUtil.upload(file.getInputStream(), localPath.toString().replace(File.separatorChar,'/'));

    }

    @Override
    public String storage(byte[] file, String fileSuffix) throws IOException {
        StringBuffer localPath = new StringBuffer();
        localPath.append(ossProperties.getFilePrefix());
        localPath.append(getFilePath(fileSuffix));
        InputStream stream = new ByteArrayInputStream(file);
        return OssBootUtil.upload(stream, localPath.toString());
    }

    @Override
    public void del(String path) {
        OssBootUtil.deleteUrl(path);
    }
}
