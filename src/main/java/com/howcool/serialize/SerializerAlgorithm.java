package com.howcool.serialize;

public interface SerializerAlgorithm {

    /**
     * JDK 序列化
     */
    byte JDK = 1;

    /**
     * json 序列化
     */
    byte JSON = 2;

    /**
     * google protoBuffer
     */
    byte PROTO_BUFFER = 3;
}
