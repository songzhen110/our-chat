package com.howcool.protocol;

import com.howcool.protocol.request.LoginRequestPacket;
import com.howcool.protocol.request.MessageRequestPacket;
import com.howcool.protocol.response.LoginResponsePacket;
import com.howcool.protocol.response.MessageResponsePacket;
import com.howcool.serialize.Serializer;
import com.howcool.serialize.impl.JDKSerializer;
import com.howcool.serialize.impl.JSONSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import static com.howcool.protocol.command.Command.*;

import java.util.HashMap;
import java.util.Map;

/***
 * 编解码器
 *
 * 自定义协议格式如下：魔数(4byte)+版本号(1byte)+序列化方式(1byte)+命令(1byte)+数据长度(4byte)+数据(N bytes)
 */
public class PacketCodeC {
    public static final int MAGIC_NUMBER = 0x7e7e7e7e;

    private final Map<Byte, Class<? extends Packet>> packetTypeMap;
    private final Map<Byte, Serializer> serializerMap;
    public static final PacketCodeC INSTANCE = new PacketCodeC();

    private PacketCodeC() {
        // 初始化算法
        serializerMap = new HashMap<>();
        Serializer jdkSerializer = new JDKSerializer();
        Serializer jsonSerializer = new JSONSerializer();
        serializerMap.put(jdkSerializer.getSerializerAlgorithm(), jdkSerializer);
        serializerMap.put(jsonSerializer.getSerializerAlgorithm(), jsonSerializer);

        // 初始化命令
        packetTypeMap = new HashMap<>();
        packetTypeMap.put(LOGIN_REQUEST, LoginRequestPacket.class);
        packetTypeMap.put(LOGIN_RESPONSE, LoginResponsePacket.class);
        packetTypeMap.put(MESSAGE_REQUEST, MessageRequestPacket.class);
        packetTypeMap.put(MESSAGE_RESPONSE, MessageResponsePacket.class);

    }

    public ByteBuf encode(ByteBuf byteBuf, Packet packet) {

        // 2. 序列化 Java 对象
        byte[] bytes = Serializer.JSON_SERIALIZER.serialize(packet);

        // 3. 实际编码过程
        byteBuf.writeInt(MAGIC_NUMBER);
        byteBuf.writeByte(packet.getVersion());
        byteBuf.writeByte(Serializer.JSON_SERIALIZER.getSerializerAlgorithm());
        byteBuf.writeByte(packet.getCommand());
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

        return byteBuf;
    }

    @Deprecated
    public ByteBuf encodeOld(Packet packet) {
        // 1. 创建 ByteBuf 对象(可以使用heapBuffer或directBuffer)
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.directBuffer();

        // 2. 序列化 Java 对象
        byte[] bytes = Serializer.DEFAULT_SERIALIZER.serialize(packet);

        // 3. 实际编码过程
        byteBuf.writeInt(MAGIC_NUMBER);
        byteBuf.writeByte(packet.getVersion());
        byteBuf.writeByte(Serializer.DEFAULT_SERIALIZER.getSerializerAlgorithm());
        byteBuf.writeByte(packet.getCommand());
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

        return byteBuf;
    }

    public Packet decode(ByteBuf byteBuf) {
        // 跳过MAGIC_NUMBER
        byteBuf.skipBytes(4);

        // 跳过版本号
        byteBuf.skipBytes(1);

        // 序列化算法标识
        byte serializeAlgorithm = byteBuf.readByte();

        // 指令
        byte command = byteBuf.readByte();

        // 数据包长度
        int length = byteBuf.readInt();

        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);

        Class<? extends Packet> requestType = getRequestType(command);
        Serializer serializer = getSerializer(serializeAlgorithm);

        if (requestType != null && serializer != null) {
            return serializer.deserialize(requestType, bytes);
        }

        return null;
    }

    private Serializer getSerializer(byte serializeAlgorithm) {

        return serializerMap.get(serializeAlgorithm);
    }

    private Class<? extends Packet> getRequestType(byte command) {

        return packetTypeMap.get(command);
    }

}
