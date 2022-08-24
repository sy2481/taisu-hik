package com.nbhy.modules.ssm.service;

/**
 * @Author: xcjx
 * @Email: nizhaobudaowo@163.com
 * @Company: nbhy
 * @Date: Created in 16:25 2021/12/29
 * @ClassName: NoteService
 * @Description: 短信服务类
 * @Version: 1.0
 */
public interface NoteService {


    /**
     * 发送短信
     * @param data
     */
    void sendNote(String data);

}
