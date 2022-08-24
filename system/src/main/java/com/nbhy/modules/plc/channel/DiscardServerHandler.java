package com.nbhy.modules.plc.channel;

import com.nbhy.modules.plc.client.PlcClient;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        System.out.println(in.toString(io.netty.util.CharsetUtil.US_ASCII));
        ctx.writeAndFlush(msg); // (1)
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }

    private final static Logger logger = LoggerFactory.getLogger(DiscardServerHandler.class);
    private final static PlcClient plcClient = new PlcClient();
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception{

        // 我这里用了map来保存链接，方便给终端发消息用
        String mapKey = "";
        Map<String, ChannelFuture> allChannelFuture = plcClient.getAllChannelFuture();
        // 通过对比channel的id，知道哪个设备掉线了，然后删除
        for(Map.Entry<String, ChannelFuture> entry : allChannelFuture.entrySet()){
            mapKey = entry.getKey();
            ChannelFuture mapValue = entry.getValue();
            log.info("客户端{}读取超时,{}", mapKey,mapValue.channel().id());
            if(mapValue.channel().id()  == ctx.channel().id())
            {
                log.info("成功了{}，{}", mapKey,mapValue.channel().id());
            }
        }

        //这里执行客户端断开连接后的操作
        log.error("断开了链接");
        ctx.close();

        if(!mapKey.equals(""))
        {
            allChannelFuture.remove(mapKey);
            plcClient.removeClientChannelFuture(mapKey);
        }
    }
}



/*
*
* @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
    }
*
*
* */


