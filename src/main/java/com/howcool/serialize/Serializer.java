package com.howcool.serialize;

import com.howcool.serialize.impl.JDKSerializer;
import com.howcool.serialize.impl.JSONSerializer;

public interface Serializer {

    Serializer DEFAULT_SERIALIZER = new JDKSerializer();

    Serializer JSON_SERIALIZER = new JSONSerializer();

    /**
     * 序列化算法
     */
    byte getSerializerAlgorithm();

    /**
     * java 对象转换成二进制
     */
    byte[] serialize(Object object);

    /**
     * 二进制转换成 java 对象
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);
}
