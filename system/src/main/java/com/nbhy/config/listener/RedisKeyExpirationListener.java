package com.nbhy.config.listener;

import com.nbhy.exception.BadRequestException;
import com.nbhy.modules.hik.constant.RedisConstant;
import com.nbhy.modules.plc.client.PlcClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

/**
 * @Author: SmallPang
 * @Description: 监听所有db的过期事件__keyevent@*__:expired"
 * @Date: 2020/5/12
 * @Param null:
 * @return: null
 **/
@Component
@Slf4j
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    @Autowired
    private PlcClient plcClient;

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    /**
     * @Author: SmallPang
     * @Description: 针对redis数据失效事件，进行数据处理
     * @Date: 2020/5/12
     * @Param message: 监听信息
     * @Param pattern:
     * @return: void
     **/
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // 用户做自己的业务处理即可,注意message.toString()可以获取失效的key
            String keyMessage = message.toString();
            if (keyMessage.startsWith(RedisConstant.SUBTITLE_MACHINE_CLEAN_KEY)) {
                String[] split = keyMessage.split(":");
                String ip =split[1];
                plcClient.subtitleClean(ip);
            }
        } catch (BadRequestException e) {
            e.printStackTrace();
        } catch (Exception e) {
            log.info("redis回调处理未知的异常");
            e.printStackTrace();
        }
    }
}
