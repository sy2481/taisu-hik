package com.nbhy.modules.upload.service.impl;

import com.nbhy.exception.BadRequestException;
import com.nbhy.modules.upload.service.StorageHandler;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class NoneStorageHandler implements StorageHandler {
    @Override
    public String storage(MultipartFile file) throws IOException {
        throw new BadRequestException("后台不支持存储");
    }

    @Override
    public String storage(byte[] file, String fileSuffix) throws IOException {
        throw new BadRequestException("后台不支持存储");
    }

    @Override
    public void del(String path) {

    }
}
