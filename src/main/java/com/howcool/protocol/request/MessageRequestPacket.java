package com.howcool.protocol.request;

import com.howcool.protocol.Packet;
import lombok.Data;
import static com.howcool.protocol.command.Command.MESSAGE_REQUEST;

@Data
public class MessageRequestPacket extends Packet {

    private String message;

    @Override
    public Byte getCommand() {
        return MESSAGE_REQUEST;
    }
}