package com.nbhy.modules.upload.config;

import com.nbhy.modules.upload.constant.UploadModeEnum;
import com.nbhy.utils.ElAdminConstant;
import com.nbhy.utils.StringUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Data
//@Configuration
//@ConfigurationProperties(prefix = "file")
@Component
public class LocalStorageProperties {


    @Autowired
    private Environment environment;

    @Value("${storage.type}")
    private UploadModeEnum uploadModeEnum;
    /** 文件大小限制 */
    private Long maxSize;

    private String baseUrl;

    private ElPath linux;

    private ElPath windows;

    public ElPath getPath(){
        String os = System.getProperty("os.name");
        if(os.toLowerCase().startsWith(ElAdminConstant.WIN)) {
            return windows;
        }
        return linux;
    }

    @Data
    public static class ElPath{
        private String path;
    }


    @PostConstruct
    public void init(){
        if(uploadModeEnum == UploadModeEnum.localStorage){
            String maxSize = environment.getProperty("file.maxSize");
            if(!StringUtils.isEmpty(maxSize)) {
                this.maxSize = Long.valueOf(maxSize);
            }
            this.baseUrl = environment.getProperty("file.baseUrl");

            linux = new ElPath();
            linux.setPath(environment.getProperty("file.linux.path"));

            windows = new ElPath();
            windows.setPath(environment.getProperty("file.windows.path"));
        }
    }
}
