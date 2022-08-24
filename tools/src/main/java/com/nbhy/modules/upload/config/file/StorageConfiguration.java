package com.nbhy.modules.upload.config.file;

import com.nbhy.modules.upload.config.FileProperties;
import com.nbhy.modules.upload.config.LocalStorageProperties;
import com.nbhy.modules.upload.config.OssProperties;
import com.nbhy.modules.upload.constant.UploadModeEnum;
import com.nbhy.modules.upload.service.StorageHandler;
import com.nbhy.modules.upload.service.impl.LocalStorageHandler;
import com.nbhy.modules.upload.service.impl.NoneStorageHandler;
import com.nbhy.modules.upload.service.impl.OssStorageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class StorageConfiguration {
    private final FileProperties fileProperties;
    private final LocalStorageProperties localStorageProperties;
    private final OssProperties ossProperties;



    @Bean
    public StorageHandler storageHandler(){
        if(fileProperties.getUploadModeEnum() == UploadModeEnum.oss){
            return new OssStorageHandler(ossProperties);
        }else if(fileProperties.getUploadModeEnum() == UploadModeEnum.localStorage){
            return new LocalStorageHandler(localStorageProperties);
        }
        return new NoneStorageHandler();
    }
}
