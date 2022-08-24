package com.nbhy.modules.upload.constant;

import cn.hutool.core.collection.CollectionUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FileType {

    //图片类型
     public static final Set<String> IMAGE = new HashSet<>();




    //视频类型
    public static final Set<String> VIDEO = new HashSet<>();

    //音频类型
    public static final Set<String> AUDIO = new HashSet<>();

    //视频类型
    public static final Set<String> DOCUMENT = new HashSet<>();

    static {
        IMAGE.add("jpg");
        IMAGE.add("png");
        IMAGE.add("gif");
        IMAGE.add("tif");
        IMAGE.add("bmp");
        IMAGE.add("dwg");
        IMAGE.add("psd");

        DOCUMENT.add("rtf");
        DOCUMENT.add("pdf");
        DOCUMENT.add("xls");
        DOCUMENT.add("zip");
        DOCUMENT.add("chm");
        DOCUMENT.add("wpd");

        VIDEO.add("rmvb");
        VIDEO.add("flv");
        VIDEO.add("mp4");
        VIDEO.add("mpg");
        VIDEO.add("wmv");
        VIDEO.add("wav");
        VIDEO.add("avi");
        VIDEO.add("mxp");
        VIDEO.add("mov");


        AUDIO.add("mp3");
        AUDIO.add("mid");

        Collections.unmodifiableSet(IMAGE);
        Collections.unmodifiableSet(DOCUMENT);
        Collections.unmodifiableSet(VIDEO);
        Collections.unmodifiableSet(AUDIO);
    }



    public static final String IMAGE_MESSAGE = CollectionUtil.join(IMAGE,",")+"格式";

    public static final String DOCUMENT_MESSAGE = CollectionUtil.join(DOCUMENT,",")+"格式";


    public static final String VIDEO_MESSAGE = CollectionUtil.join(VIDEO,",")+"格式";


    public static final String AUDIO_MESSAGE = CollectionUtil.join(AUDIO,",")+"格式";




}
