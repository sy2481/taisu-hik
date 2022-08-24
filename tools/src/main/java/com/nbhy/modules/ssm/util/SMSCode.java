package com.nbhy.modules.ssm.util;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.nbhy.exception.BadRequestException;
import com.nbhy.modules.ssm.config.SMSConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


public class SMSCode {


    private final static Logger logger = LoggerFactory.getLogger(SMSCode.class);

    /**
     * @param phone
     *            发送到的手机号
     * @param msg
     *            发送的内容
     */
    public static void sendCode(String phone, String msg){
        logger.info("发送短信>>>>>>>>>>"+phone+"内容是>>>>>>>>>>>"+msg);
        //此处放AccessKeyID和AccessKeySecret
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", SMSConstant.AccessKeyID, SMSConstant.AccessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        //此处放接收验证码的手机号
        request.putQueryParameter("PhoneNumbers", phone);
        //此处放签名名称（必须审核通过）
        request.putQueryParameter("SignName", SMSConstant.SignName);
        //此处放短信模板（必须审核通过）
        request.putQueryParameter("TemplateCode", SMSConstant.TemplateCode);
        //此处放验证码的内容（JSON格式\"表示转义。JSON格式：{"code":"666666"}）
        request.putQueryParameter("TemplateParam", "{\"code\":\"" + msg + "\"}");
        try {
            com.aliyuncs.CommonResponse response = client.getCommonResponse(request);
            logger.info(response.getData());
            if(response.getData().indexOf("isv.MOBILE_NUMBER_ILLEGAL")>0){
                logger.info("手机号码异常》》》》》》》》》》》》》》》"+phone);
                throw new BadRequestException("发送短信失败,手机号码异常，请查看手机号码是否填写正确");
            }
        }catch (Exception e){
            logger.info("发送短信失败");
            e.printStackTrace();
            throw new BadRequestException("发送短信失败，请与管理员联系");
        }
    }

}
