package com.nbhy.modules.upload.util;

import com.nbhy.exception.BadRequestException;
import com.nbhy.exception.FileTypeException;
import com.nbhy.modules.upload.constant.FileEnum;
import com.nbhy.modules.upload.constant.FileType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;


public class FileTypeUtil {

    /**
     * 校验文件的类型
     * @param file 文件
     * @param fileType 文件类型
     * @throws FileTypeException
     */
    public static void check(MultipartFile file,String fileType) throws FileTypeException {
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        }catch (IOException e) {
            e.printStackTrace();
        }
        if(inputStream == null){
            throw new BadRequestException("解析文件异常，请重新尝试");
        }
        switch (FileEnum.getByValue(fileType)){
            case IMAGE:
                if(!FileType.IMAGE.contains(cn.hutool.core.io.FileTypeUtil.getType(inputStream))){
                    throw new FileTypeException("文件格式不正确。只支持"+FileType.IMAGE_MESSAGE);
                }
                break;
            case AUDIO:
                if(!FileType.AUDIO.contains(cn.hutool.core.io.FileTypeUtil.getType(inputStream))){
                    throw new FileTypeException("文件格式不正确。只支持"+FileType.AUDIO_MESSAGE);
                }
                break;
            case VIDEO:
                if(!FileType.VIDEO.contains(cn.hutool.core.io.FileTypeUtil.getType(inputStream))){
                    throw new FileTypeException("文件格式不正确。只支持"+FileType.VIDEO_MESSAGE);
                }
                break;
            case DOCUMENT:
                if(!FileType.DOCUMENT.contains(cn.hutool.core.io.FileTypeUtil.getType(inputStream))){
                    throw new FileTypeException("文件格式不正确。只支持"+FileType.DOCUMENT_MESSAGE);
                }
                break;
            default:
                throw new FileTypeException("文件类型不存在。fileType只能为image、audio、video、document");
        }
    }
}
