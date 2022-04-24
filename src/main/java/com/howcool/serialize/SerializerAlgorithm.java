package com.howcool.serialize;

public interface SerializerAlgorithm {
    /**
     * json 序列化
     */
    byte JSON = 1;

    /**
     * google protoBuffer
     */
    byte PROTO_BUFFER = 2;
}
