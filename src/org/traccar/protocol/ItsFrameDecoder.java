package org.traccar.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.traccar.BaseFrameDecoder;
import org.traccar.helper.BufferUtil;

public class ItsFrameDecoder extends BaseFrameDecoder {

    private static final int MINIMUM_LENGTH = 20;

    private ByteBuf readFrame(ByteBuf buf, int delimiterIndex, int skip) {
        int headerIndex = buf.indexOf(buf.readerIndex() + 1, buf.writerIndex(), (byte) '$');
        if (headerIndex > 0 && headerIndex < delimiterIndex) {
            return buf.readRetainedSlice(headerIndex - buf.readerIndex());
        } else {
            ByteBuf frame = buf.readRetainedSlice(delimiterIndex - buf.readerIndex());
            buf.skipBytes(skip);
            return frame;
        }
    }

    @Override
    protected Object decode(
            ChannelHandlerContext ctx, Channel channel, ByteBuf buf) throws Exception {

        while (buf.isReadable() && buf.getByte(buf.readerIndex()) != '$') {
            buf.skipBytes(1);
        }

        int delimiterIndex = BufferUtil.indexOf("\r\n", buf);
        if (delimiterIndex > MINIMUM_LENGTH) {
            return readFrame(buf, delimiterIndex, 2);
        } else {
            delimiterIndex = buf.indexOf(buf.readerIndex(), buf.writerIndex(), (byte) '*');
            if (delimiterIndex > MINIMUM_LENGTH) {
                if (buf.writerIndex() > delimiterIndex + 1 && buf.getByte(delimiterIndex + 1) == '*') {
                    delimiterIndex += 1;
                }
                if (buf.getByte(delimiterIndex - 2) == ',') {
                    return readFrame(buf, delimiterIndex - 1, 2); // skip binary checksum
                } else {
                    return readFrame(buf, delimiterIndex, 1);
                }
            }
        }

        return null;
    }

}
