package org.traccar.protocol;

import org.traccar.BaseProtocolDecoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.traccar.Protocol;

import java.net.SocketAddress;

public class PaymaticProtocolDecoder extends BaseProtocolDecoder {

    private final PaymaticTextProtocolDecoder textProtocolDecoder;
    private final HuabaoProtocolDecoder binaryProtocolDecoder; //Huabao1BinaryProtocolDecoder

    public PaymaticProtocolDecoder(Protocol protocol) {
        super(protocol);
        textProtocolDecoder = new PaymaticTextProtocolDecoder(protocol);
        binaryProtocolDecoder = new HuabaoProtocolDecoder(protocol);
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        ByteBuf buf = (ByteBuf) msg;
        boolean isBinary = PaymaticFrameDecoder.isBinary(buf);
        if (isBinary) {
            return textProtocolDecoder.decode(channel, remoteAddress, msg);
        } else {
            return binaryProtocolDecoder.decode(channel, remoteAddress, msg);
        }
    }

}
