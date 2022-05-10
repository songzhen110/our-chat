package com.howcool.protocol.response;

import lombok.Data;
import com.howcool.protocol.Packet;
import lombok.NoArgsConstructor;

import static com.howcool.protocol.command.Command.LOGIN_RESPONSE;

@Data
@NoArgsConstructor
public class LoginResponsePacket extends Packet {

    private String userId;
    private String userName;
    private boolean success;
    private String reason;

    @Override
    public Byte getCommand() {
        return LOGIN_RESPONSE;
    }
}
