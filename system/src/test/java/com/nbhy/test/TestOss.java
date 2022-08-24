package com.nbhy.test;

import com.nbhy.AppRun;
import com.nbhy.modules.upload.service.StorageHandler;
import com.nbhy.modules.upload.util.OssBootUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest(classes = {AppRun.class})
@RunWith(value = SpringJUnit4ClassRunner.class)
public class TestOss {

    @Autowired
    private StorageHandler storageHandler;


    @Test
    public void del(){
        OssBootUtil.deleteUrl("http://oss.nbhuiyue.cn/2021-04");
    }



    @Test
    public void delLocalStorage(){
        storageHandler.del("http://localhost:8000/file/2021-04/00bc503ba1ff4b988ba6c2a119ffbbf4.jpg");
    }
}
