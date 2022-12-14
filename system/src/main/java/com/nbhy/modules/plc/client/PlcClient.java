package com.nbhy.modules.plc.client;

import cn.hutool.core.util.StrUtil;
import com.nbhy.modules.erp.util.EquipmentUtil;
import com.nbhy.modules.hik.constant.RedisConstant;
import com.nbhy.modules.hik.constant.SubtitleMachineConstant;
import com.nbhy.modules.hik.util.CrcUtils;
import com.nbhy.modules.hik.util.SubtitleMachineUtil;
import com.nbhy.modules.plc.channel.PlcClientHandler;
import com.nbhy.modules.plc.constant.PlcCommandConstant;
import com.nbhy.utils.RedisUtils;
import com.nbhy.utils.StringUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.ConnectException;
import java.nio.channels.ClosedChannelException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
public class PlcClient {
    private Map<String,ChannelFuture> CHANNEL_CACHE = new ConcurrentHashMap<>();
    private Map<String, Lock> LOCK_CACHE = new ConcurrentHashMap<>();
    private EventLoopGroup workerGroup;
    private Bootstrap boot;
    private final static Logger logger = LoggerFactory.getLogger(PlcClient.class);


    @Autowired
    private RedisUtils redisUtils;

    public ChannelFuture getChannelFuture(String ip){
        return CHANNEL_CACHE.get(ip);
    }

    public Boolean hasChannelFuture(String ip){
        if(StringUtils.isEmpty(ip)){
            return false;
        }
        return (CHANNEL_CACHE.get(ip) == null || CHANNEL_CACHE.get(ip).isVoid()) ? false : true;
    }

    public void setClientChannelFuture(String ip,ChannelFuture future){
        CHANNEL_CACHE.put(ip,future);
        LOCK_CACHE.put(ip,new ReentrantLock());
    }

    public void removeClientChannelFuture(String ip){
        CHANNEL_CACHE.remove(ip);
        LOCK_CACHE.remove(ip);
    }

    public Map<String,ChannelFuture> getAllChannelFuture(){
        return this.CHANNEL_CACHE;
    }


    public PlcClient(){
        int bossGroupThreadNum = Math.min(2, NettyRuntime.availableProcessors()/2);
        workerGroup = new NioEventLoopGroup(bossGroupThreadNum);
        boot = new Bootstrap();
        boot.group(workerGroup);
        boot.channel(NioSocketChannel.class);
        boot.option(ChannelOption.SO_KEEPALIVE, true);
        boot.handler(new ClientHandlerInitializer(this));
    }

    public void run(String ipAddress,Integer port){
        try {
            log.info("26666666666");
            ChannelFuture future = boot.connect(ipAddress, port);
            boolean notTimeout = future.awaitUninterruptibly(30, TimeUnit.SECONDS);
            Channel clientChannel = future.channel();
            if (notTimeout) {
                if (clientChannel != null && clientChannel.isActive()) {
                    log.info("netty client started !!! {} connect to server", clientChannel.localAddress());
                    this.setClientChannelFuture(ipAddress,future);
//                    boolean equipmentLog = EquipmentUtil.getEquipmentLog(ipAddress, "0");
//                    if(equipmentLog){
//                        log.info("????????????????????????");
//                    }else{
//                        log.info("????????????????????????");
//                    }
                }
                Throwable cause = future.cause();
                if (cause != null) {
//                    boolean equipmentLog = EquipmentUtil.getEquipmentLog(ipAddress, "1");
//                    if(equipmentLog){
//                        log.info("????????????????????????");
//                    }else{
//                        log.info("????????????????????????");
//                    }
                    exceptionHandler(cause);
                }
            } else {
//                boolean equipmentLog = EquipmentUtil.getEquipmentLog(ipAddress, "1");
//                if(equipmentLog){
//                    log.info("????????????????????????");
//                }else{
//                    log.info("????????????????????????");
//                }
                log.warn("connect remote host[{}] timeout {}s", clientChannel.remoteAddress(), 30);
            }
        }catch (Exception e){
            log.error(ipAddress+"???????????????");
            exceptionHandler(e);
        }
    }


    public void connectAsync(String host,Integer port) {
        log.info("????????????????????????: {}:{}",host,port);
        ChannelFuture channelFuture = boot.connect(host, port);
        channelFuture.addListener((ChannelFutureListener) future -> {
            Throwable cause = future.cause();
            if (cause != null) {
                exceptionHandler(cause);
                log.info("?????????????????????>>>>>>");
                channelFuture.channel().eventLoop().schedule(new Runnable() {
                    @Override
                    public void run() {
                        connectAsync(host,port);
                    }
                }, 5, TimeUnit.SECONDS);
            } else {
                Channel clientChannel = channelFuture.channel();
                if (clientChannel != null && clientChannel.isActive()) {
                    log.info("Netty client started !!! {} connect to server", clientChannel.localAddress());
                    this.setClientChannelFuture(host,channelFuture);
//                    boolean equipmentLog = EquipmentUtil.getEquipmentLog(host, "0");
//                    if(equipmentLog){
//                        log.info("????????????????????????");
//                    }else{
//                        log.info("????????????????????????");
//                    }

                }
            }
        });
    }


    private void exceptionHandler(Throwable cause) {
        if (cause instanceof ConnectException) {
            log.error("????????????:{}", cause.getMessage());
        } else if (cause instanceof ClosedChannelException) {
            log.error("connect error:{}", "client has destroy");
        } else {
            log.error("connect error:", cause);
        }
    }


    /**
     * ????????????
     * @param ipAddress
     * @param command
     */
    public void send(String ipAddress,String command){
        this.getChannelFuture(ipAddress).channel().writeAndFlush(Unpooled.copiedBuffer(CrcUtils.hexStringToByte(command)));
    }


    /**
     * ?????????????????????
     * @param ipAddress
     * @param message
     */
    public void sendMes(String ipAddress,String message){
        if(!this.hasChannelFuture(ipAddress)){
            return;
        }
        Lock lock = LOCK_CACHE.get(ipAddress);
        lock.lock();
        try {
            String[] messages = StrUtil.split(message, 5);
            for (int i = 0; i < messages.length; i++) {
                this.getChannelFuture(ipAddress).channel().writeAndFlush(Unpooled.copiedBuffer(SubtitleMachineUtil.getCommand(messages[i])));
                //?????????????????????????????????
                if(i == (message.length() -1) ){
                    continue;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//            if(message.length() <= 5){
//                this.getChannelFuture(ipAddress).channel().writeAndFlush(Unpooled.copiedBuffer(SubtitleMachineUtil.getCommand(message)));
//            }else{
//                for (String msg : messages) {
//                    this.getChannelFuture(ipAddress).channel().writeAndFlush(Unpooled.copiedBuffer(SubtitleMachineUtil.getCommand(msg)));
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
            redisUtils.set(RedisConstant.SUBTITLE_MACHINE_CLEAN_KEY+ipAddress,1,RedisConstant.SUBTITLE_MACHINE_CLEAN_TIME,TimeUnit.SECONDS);
        }finally {
            lock.unlock();
        }
    }


    /**
     * ???????????????????????????
     * @param ip
     */
    public void subtitleClean(String ip){
        Lock lock = LOCK_CACHE.get(ip);
        lock.lock();
        try {
            Object o = redisUtils.get(RedisConstant.SUBTITLE_MACHINE_CLEAN_KEY + ip);
            if(o != null){
                return;
            }
            //??????????????????
            this.getChannelFuture(ip).channel().writeAndFlush(Unpooled.copiedBuffer(SubtitleMachineUtil.getCommand("")));
        }finally {
            lock.unlock();
        }
    }


    /**
     * ????????????
     * @param ipAddress
     * @param index
     */
    public void openDoor(String ipAddress,String index){
        log.info("?????????plc?????????>>>>>>>>>>{}",index);
        ChannelFuture channelFuture = this.getChannelFuture(ipAddress);
        //???????????????
        channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer(CrcUtils.hexStringToByte(String.format(PlcCommandConstant.CLOSE_DOOR_COMMAND,index))));
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //????????????
        channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer(CrcUtils.hexStringToByte(String.format(PlcCommandConstant.OPEN_DOOR_COMMAND,index))));
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //????????????
        channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer(CrcUtils.hexStringToByte(String.format(PlcCommandConstant.CLOSE_DOOR_COMMAND,index))));
    }

    /**
     * ????????????
     */
    public void destroy() {
        logger.info("Shutdown Netty Server...");
        for (ChannelFuture value : CHANNEL_CACHE.values()) {
            if(value != null) {
                value.channel().close();
            }
        }
        workerGroup.shutdownGracefully();
        logger.info("Shutdown Netty Server Success!");
    }

    /**
     * ????????????
     */
    public void destroyChannel() {
//        for (ChannelFuture value : CHANNEL_CACHE.values()) {
//            if(value != null) {
//                value.channel().close();
//                logger.info(value.channel().localAddress());
//            }
//        }
        logger.info("close channel start");
        for (String s : CHANNEL_CACHE.keySet()) {
            ChannelFuture channelFuture = CHANNEL_CACHE.get(s);
            if(channelFuture != null) {
                channelFuture.channel().close();
                logger.info(s + ">>>>>>??????????????????");
            }
        }
        workerGroup.shutdownGracefully();
        logger.info("close channel Success!");
    }


    public void initConnect(String ip,int port){
        log.info("25555555555");
        run(ip,port);
    }


    static class ClientHandlerInitializer extends ChannelInitializer<SocketChannel> {
        private static final InternalLogger log = InternalLoggerFactory.getInstance(PlcClient.class);
        private PlcClient client;

        public ClientHandlerInitializer(PlcClient client) {
            this.client = client;
        }

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();

            pipeline.addLast(new PlcClientHandler(client));
        }
    }



}
