package com.nbhy.modules.hik.service;

import com.nbhy.modules.hik.domain.vo.*;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * @Author: xcjx
 * @Email: nizhaobudaowo@163.com
 * @Company: nbhy
 * @Date: Created in 16:00 2022/3/11
 * @ClassName: HikPersonService
 * @Description: 海康人员
 * @Version: 1.0
 */
public interface HikPersonService {

    /**
     * 海康下发人员
     * @param personVO
     */
    void createPerson(PersonVO personVO);

    void createPersonSysUser(PersonVO personVO);

    void createPersonManFactory(PersonVO personVO);

    void createPersonOnlyFace(PersonVO personVO);
    /**
     * 重新海康下发人员
     * @param personVO
     */
    void reCreatePerson(PersonVO personVO);

    /**
     * 删除海康人员
     * @param personId
     */
    void deleteById(String personId);

    /**
     * 删除海康人员人脸
     * @param personId
     */
    void deleteFaceById(String personId);

    /**
     * 批量删除海康人员
     */
    void deleteByIds();


    /**
     * 更新人员信息
     * @param personUpdateVO
     */
    void updateById(PersonUpdateVO personUpdateVO);


    /**
     * 绑定定位卡
     * @param cardBindVO
     */
    void bindCard(CardBindVO cardBindVO);


    /**
     * 解绑定位卡
     * @param cardNumber
     */
    void untieCard(String cardNumber);

    /**
     * 下发人员权限
     * @param personAuthVO
     */
    void issueAuth(PersonAuthVO personAuthVO);
    void issueAuthSysUser(PersonAuthVO personAuthVO);

    /**
     * 定时删除海康厂商人员
     */
    void syncDeletePerson();

    /**
     * 下发人脸
     */
    void issueFace(FaceVO faceVO);
    void issueFaceUpdate(FaceVO faceVO);
}
