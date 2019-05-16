package org.traccar.protocol;

import org.traccar.BaseProtocolDecoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.traccar.Protocol;

import java.net.SocketAddress;

public class Huabao1ProtocolDecoder extends BaseProtocolDecoder {

    private final Huabao1TextProtocolDecoder textProtocolDecoder;
    private final HuabaoProtocolDecoder binaryProtocolDecoder;

    public Huabao1ProtocolDecoder(Protocol protocol) {
        super(protocol);
        textProtocolDecoder = new Huabao1TextProtocolDecoder(protocol);
        binaryProtocolDecoder = new HuabaoProtocolDecoder(protocol);
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        ByteBuf buf = (ByteBuf) msg;
        boolean isBinary = Huabao1FrameDecoder.isBinary(buf);
        if (isBinary) {
            return textProtocolDecoder.decode(channel, remoteAddress, msg);
        } else {
            return binaryProtocolDecoder.decode(channel, remoteAddress, msg);
        }
    }

}

