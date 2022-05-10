package com.howcool.codec;

import com.howcool.protocol.PacketCodeC;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

/***
 * 基于长度域拆包器 LengthFieldBasedFrameDecoder
 */
@Slf4j
public class Spliter extends LengthFieldBasedFrameDecoder {
    private static final int LENGTH_FIELD_OFFSET = 7;
    private static final int LENGTH_FIELD_LENGTH = 4;

    public Spliter() {
        super(Integer.MAX_VALUE, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        // ByteBuf.readerIndex() 返回当前的读指针 ，ByteBuf.getInt(offset) 不会改变读写指针
        if (in.getInt(in.readerIndex()) != PacketCodeC.MAGIC_NUMBER) {
            log.error("拦截非法请求");
            ctx.channel().close();
            return null;
        }

        return super.decode(ctx, in);
    }




}
