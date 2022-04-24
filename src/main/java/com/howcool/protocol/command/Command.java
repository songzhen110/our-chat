package com.howcool.protocol.command;

public interface Command {

    // 登陆请求
    Byte LOGIN_REQUEST = 1;

    // 登陆响应
    Byte LOGIN_RESPONSE = 2;

    // 发送消息
    Byte MESSAGE_REQUEST = 3;

    // 响应消息
    Byte MESSAGE_RESPONSE = 4;
}