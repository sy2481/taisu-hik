package com.nbhy.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;
import java.io.File;

@Configuration
public class MultipartConfig {


   @Bean
   MultipartConfigElement multipartConfigElement() {
      MultipartConfigFactory factory = new MultipartConfigFactory();
      String location = System.getProperty("user.home") + "/.nbhy/file/tmp";
      File tmpDirFile = new File(location);
      // 判断文件夹是否存在
      if (!tmpDirFile.exists()) {
         tmpDirFile.mkdirs();
      }
      factory.setLocation(location);
      return factory.createMultipartConfig();
   }
}
