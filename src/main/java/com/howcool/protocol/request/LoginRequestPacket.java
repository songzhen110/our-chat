package com.howcool.protocol.request;

import com.howcool.protocol.Packet;
import lombok.Data;
import static com.howcool.protocol.command.Command.LOGIN_REQUEST;

@Data
public class LoginRequestPacket extends Packet {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    @Override
    public Byte getCommand() {
        return LOGIN_REQUEST;
    }
}