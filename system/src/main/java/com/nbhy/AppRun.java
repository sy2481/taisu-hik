package com.nbhy;

import com.nbhy.annotation.RepeatSubmit;
import com.nbhy.annotation.rest.AnonymousGetMapping;
import com.nbhy.modules.hik.util.Alarm;
import com.nbhy.modules.plc.client.PlcClient;
import com.nbhy.utils.SpringContextHolder;
import io.netty.channel.ChannelFuture;
import io.swagger.annotations.Api;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;

import java.security.Security;

@EnableAsync
@RestController
@Api(hidden = true)
@EnableScheduling
@SpringBootApplication
@EnableTransactionManagement
public class AppRun {

    @Autowired
    private PlcClient plcClient;


    public static void main(String[] args) {
        Security.setProperty("jdk.tls.disabledAlgorithms","SSLv3, RC4, DES, MD5withRSA, DH keySize < 1024,EC keySize < 224, anon, NULL");
        SpringApplication.run(AppRun.class, args);
    }

    @Bean
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }

    @Bean
    public ServletWebServerFactory webServerFactory() {
        TomcatServletWebServerFactory fa = new TomcatServletWebServerFactory();
        fa.addConnectorCustomizers(connector -> connector.setProperty("relaxedQueryChars", "[]{}"));
        return fa;
    }


    /**
     * 访问首页提示
     *
     * @return /
     */
    @AnonymousGetMapping("/")
    public String index() {
        return "Backend service started successfully";
    }

    @Configuration
    public class ApplicationService implements DisposableBean {
        @Override
        public void destroy() throws Exception{
            Alarm.Logout();
        }
    }
}
