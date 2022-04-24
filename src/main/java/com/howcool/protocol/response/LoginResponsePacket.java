package com.howcool.protocol.response;

import lombok.Data;
import com.howcool.protocol.Packet;

import static com.howcool.protocol.command.Command.LOGIN_RESPONSE;

@Data
public class LoginResponsePacket extends Packet {
    private boolean success;

    private String reason;


    @Override
    public Byte getCommand() {
        return LOGIN_RESPONSE;
    }
}
