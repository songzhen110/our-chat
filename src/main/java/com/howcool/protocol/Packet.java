package com.howcool.protocol;

import lombok.Data;

/***
 * 抽象定义客户端与服务端通信的基本 Java 对象
 *  包含 版本号、指令
 */
@Data
public abstract class Packet {
    /**
     * 协议版本号(1byte)
     */
    private Byte version = 1;

    /**
     * 协议指令(1byte)
     */
    public abstract Byte getCommand();

}