package org.traccar.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.traccar.BaseFrameDecoder;

import java.nio.charset.StandardCharsets;

public class FifotrackFrameDecoder extends BaseFrameDecoder {

    @Override
    protected Object decode(
            ChannelHandlerContext ctx, Channel channel, ByteBuf buf) throws Exception {

        if (buf.readableBytes() < 10) {
            return null;
        }

        int index = buf.indexOf(buf.readerIndex(), buf.writerIndex(), (byte) ',');
        if (index != -1) {
            int length = index - buf.readerIndex() + 3 + Integer.parseInt(
                    buf.toString(buf.readerIndex() + 2, index - buf.readerIndex() - 2, StandardCharsets.US_ASCII));
            if (buf.readableBytes() >= length + 2) {
                ByteBuf frame = buf.readRetainedSlice(length);
                buf.skipBytes(2); // delimiter
                return frame;
            }
        }

        return null;
    }

}
