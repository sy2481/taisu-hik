package com.nbhy.modules.upload.config;

import com.nbhy.modules.upload.constant.UploadModeEnum;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Data
@Component("myFileProperties")
public class FileProperties {

    @Autowired
    private  OssProperties ossProperties;

    @Autowired
    private  LocalStorageProperties localStorageProperties;

    @Value("${storage.type}")
    private UploadModeEnum uploadModeEnum;

    @Value("${storage.quartz}")
    private Boolean quartz;


    @PostConstruct
    public void init(){
        System.out.println("afdadfasdf");
    }

    /**
     * 根据配置选择指定的属性
     * @return
     */
    public Long getMaxSize(){
        if(uploadModeEnum == UploadModeEnum.oss){
            return ossProperties.getMaxSize();
        }else if(uploadModeEnum == UploadModeEnum.localStorage){
            return localStorageProperties.getMaxSize();
        }else {
            return null;
        }
    }


    public boolean isOss(){
        if( uploadModeEnum == UploadModeEnum.oss){
            return true;
        }
        return false;
    }

}
