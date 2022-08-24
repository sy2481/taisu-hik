package com.nbhy.modules.upload.service.impl;

import cn.hutool.json.JSONUtil;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbhy.exception.BadRequestException;
import com.nbhy.modules.upload.config.FileProperties;
import com.nbhy.modules.upload.config.OssProperties;
import com.nbhy.modules.upload.constant.UploadModeEnum;
import com.nbhy.modules.upload.domain.OssCallbackParam;
import com.nbhy.modules.upload.domain.OssCallbackResult;
import com.nbhy.modules.upload.domain.OssPolicyResult;
import com.nbhy.modules.upload.domain.Upload;
import com.nbhy.modules.upload.mapper.UploadMapper;
import com.nbhy.modules.upload.service.StorageHandler;
import com.nbhy.modules.upload.service.UploadService;
import com.nbhy.utils.FileUtil;
import com.nbhy.utils.SpringContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadServiceImpl extends ServiceImpl<UploadMapper,Upload> implements UploadService {

    private final FileProperties fileProperties;
    private final StorageHandler storageHandler;
    private final UploadMapper uploadMapper;
    private final OssProperties ossProperties;


    @Override
    public String create(MultipartFile file) {
        if(fileProperties.getUploadModeEnum() == UploadModeEnum.none){
            throw new BadRequestException("后台不支持文件存储");
        }
        FileUtil.checkSize(fileProperties.getMaxSize(),file.getSize());
        String filePath = null;
        try {
            filePath = storageHandler.storage(file);
        }catch (IOException e){
            e.printStackTrace();
            throw new BadRequestException("文件存储失败，请重新尝试");
        }
        Upload upload = new Upload();
        upload.setEnabled(false);
        upload.setFilePath(filePath);
        uploadMapper.insert(upload);
        return filePath;
    }


    /**
     * 签名生成
     */
    @Override
    public OssPolicyResult policy() {
        if(fileProperties.getUploadModeEnum() != UploadModeEnum.oss){
            throw new BadRequestException("当前存储方式不支持oss");
        }
        OssPolicyResult result = new OssPolicyResult();
        // 存储目录
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dir = ossProperties.getFilePrefix()+"/"+sdf.format(new Date());
        // 签名有效期
        long expireEndTime = System.currentTimeMillis() + ossProperties.getExpire() * 1000;
        Date expiration = new Date(expireEndTime);
        // 文件大小
        long maxSize = ossProperties.getMaxSize() * 1024 * 1024;
        // 回调
		OssCallbackParam callback = new OssCallbackParam();
		callback.setCallbackUrl(ossProperties.getCallback());
		callback.setCallbackBody("filename=${object}&size=${size}&mimeType=${mimeType}&height=${imageInfo.height}&width=${imageInfo.width}");
		callback.setCallbackBodyType("application/x-www-form-urlencoded");
        // 提交节点
        String action = "http://"   + ossProperties.getEndpoint();
        try {
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, maxSize);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);
            String postPolicy = SpringContextHolder.getBean(OSSClient.class).generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String policy = BinaryUtil.toBase64String(binaryData);
            String signature = SpringContextHolder.getBean(OSSClient.class).calculatePostSignature(postPolicy);
			String callbackData = BinaryUtil.toBase64String(JSONUtil.parse(callback).toString().getBytes("utf-8"));
            // 返回结果
            result.setAccessKeyId(SpringContextHolder.getBean(OSSClient.class).getCredentialsProvider().getCredentials().getAccessKeyId());
            result.setPolicy(policy);
            result.setSignature(signature);
            result.setDir(dir);
			result.setCallback(callbackData);
            result.setHost(action);
        } catch (Exception e) {
            log.error("签名生成失败", e);
        }
        return result;
    }



    @Override
    public OssCallbackResult callback(HttpServletRequest request) {
        OssCallbackResult result= new OssCallbackResult();
        String filename = request.getParameter("filename");
        filename = ossProperties.getBaseUrl().concat("/").concat(filename);
        result.setFilename(filename);
        result.setSize(request.getParameter("size"));
        result.setMimeType(request.getParameter("mimeType"));
        result.setWidth(request.getParameter("width"));
        result.setHeight(request.getParameter("height"));
        log.info(request.toString());
        Upload upload = new Upload();
        upload.setEnabled(false);
        upload.setFilePath(filename);
        uploadMapper.insert(upload);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void del(String path) {
        storageHandler.del(path);
        uploadMapper.delete(Wrappers.<Upload>lambdaUpdate().eq(Upload::getFilePath,path));
    }

    @Override
    public void updateEnabled(String path) {
        uploadMapper.update(null, Wrappers.<Upload>lambdaUpdate().
                set(Upload::getEnabled,true).
                eq(Upload::getFilePath,path));
    }


}
