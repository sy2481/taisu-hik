package com.nbhy.modules.upload.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nbhy.modules.upload.domain.OssCallbackResult;
import com.nbhy.modules.upload.domain.OssPolicyResult;
import com.nbhy.modules.upload.domain.Upload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface UploadService extends IService<Upload> {

    /**
     * 新增文件
     * @param file
     * @return
     */
    String create(MultipartFile file);


    /**
     * oss上传策略生成
     */
    OssPolicyResult policy();


    /**
     * oss上传成功回调
     */
    OssCallbackResult callback(HttpServletRequest request);


    void del(String path);

    /**
     * 更新图片，设置图片为启用状态。
     * @param path
     */
    @Async
    void updateEnabled(String path);
}
