package org.traccar.protocol;

import org.traccar.BaseProtocolDecoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.traccar.Protocol;

import java.net.SocketAddress;

public class SaboProtocolDecoder extends BaseProtocolDecoder {

    private final SaboTextProtocolDecoder textProtocolDecoder;
    private final Tr11ProtocolDecoder binaryProtocolDecoder;

    public SaboProtocolDecoder(Protocol protocol) {
        super(protocol);
        textProtocolDecoder = new SaboTextProtocolDecoder(protocol);
        binaryProtocolDecoder = new Tr11ProtocolDecoder(protocol);
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
