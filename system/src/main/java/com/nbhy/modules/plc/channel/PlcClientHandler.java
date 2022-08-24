package com.nbhy.modules.plc.channel;

import com.nbhy.modules.hik.util.CrcUtils;
import com.nbhy.modules.plc.client.PlcClient;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PlcClientHandler extends ChannelInboundHandlerAdapter {
    private PlcClient plcClient;

    public PlcClientHandler(PlcClient client) {
        this.plcClient = client;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String ipAddress = getIpAddress(ctx);
        ByteBuf buf = (ByteBuf) msg;
        log.info(ipAddress + ":" + CrcUtils.bytesToHexString(buf));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof IOException) {
            log.warn("exceptionCaught:客户端[{}]和远程断开连接", ctx.channel().localAddress());
        } else {
            log.error(cause.getMessage());
        }
        ctx.pipeline().remove(this);
        reconnectionAsync(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("channelInactive:{}", ctx.channel().localAddress());
        ctx.channel().close();
        String ipAddress = this.getIpAddress(ctx);
        plcClient.removeClientChannelFuture(ipAddress);
        reconnectionAsync(ctx);
    }


    private void reconnectionAsync(ChannelHandlerContext ctx) {
        log.info("5s之后重新建立连接");
        String ipAddress =  this.getIpAddress(ctx);
        Integer port =  this.getPort(ctx);

        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                plcClient.connectAsync(ipAddress,port);
            }
        }, 5, TimeUnit.SECONDS);
    }


    /**
     * 通过ip地址获取key
     * @param ctx
     * @return
     */
    private String getIpAddress(ChannelHandlerContext ctx){
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        return socketAddress.getAddress().getHostAddress();
    }

    /**
     * 通过ip地址获取key
     * @param ctx
     * @return
     */
    private Integer getPort(ChannelHandlerContext ctx){
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        return socketAddress.getPort();
//        return null;
    }

    /**
     * 通过ip地址获取key
     * @param ctx
     * @return
     */
    private String getLocalAddress(ChannelHandlerContext ctx){
        String address = ctx.channel().localAddress().toString();
        return address.substring(1, address.lastIndexOf(":"));
    }
}
