package com.nbhy.modules.upload.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.nbhy.modules.upload.config.LocalStorageProperties;
import com.nbhy.modules.upload.service.StorageHandler;
import com.nbhy.utils.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;


public class LocalStorageHandler implements StorageHandler {

    private final LocalStorageProperties localStorageProperties;

    public LocalStorageHandler(LocalStorageProperties localStorageProperties){
        this.localStorageProperties = localStorageProperties;
    }

    @Override
    public String storage(MultipartFile file) throws IOException {
        StringBuffer localPath = new StringBuffer();
        localPath.append(localStorageProperties.getPath().getPath())
                .append(getFilePath(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1)));
        File imgFile = FileUtil.touch(localPath.toString());
        try{
            file.transferTo(imgFile);
            return localStorageProperties.getBaseUrl()+localPath.toString().
                    replace(localStorageProperties.getPath().getPath(),"").
                    replace("\\","/");
        }catch (IOException e){
            FileUtil.del(imgFile);
            throw e;
        }
    }

    @Override
    public String storage(byte[] file,String fileSuffix) throws IOException {
        StringBuffer localPath = new StringBuffer();
        localPath.append(localStorageProperties.getPath().getPath())
                .append(getFilePath(fileSuffix));
        File imgFile = FileUtil.touch(localPath.toString());
        FileUtil.writeBytes(file,imgFile);
        return localStorageProperties.getBaseUrl()+localPath.toString().
                replace(localStorageProperties.getPath().getPath(),"").
                replace("\\","/");
    }

    @Override
    public void del(String path) {
        if(StringUtils.isEmpty(path))
            return;
        path = localStorageProperties.getPath().getPath() + path.replace(localStorageProperties.getBaseUrl(),"");
        path = path.replace("/",File.separator);
        if(FileUtil.isFile(path)){
            FileUtil.del(path);
        }
    }


}
