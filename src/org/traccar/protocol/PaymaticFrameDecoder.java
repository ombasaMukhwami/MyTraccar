package org.traccar.protocol;

import io.netty.buffer.Unpooled;
import org.traccar.BaseFrameDecoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PaymaticFrameDecoder extends BaseFrameDecoder {

    private static final int MINIMUM_LENGTH = 11;

    private static final Set<String> BINARY_HEADERS = new HashSet<>(
            Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"));

    public static boolean isBinary(ByteBuf buf) {
        String header = buf.toString(buf.readerIndex(), 2, StandardCharsets.US_ASCII);
        return BINARY_HEADERS.contains(header);
    }

    @Override
    protected Object decode(
            ChannelHandlerContext ctx, Channel channel, ByteBuf buf) throws Exception {

        if (buf.readableBytes() < MINIMUM_LENGTH) {
            return null;
        }
        if (isBinary(buf)) {
            return buf.readRetainedSlice(buf.readableBytes());
        } else {
            int index = buf.indexOf(buf.readerIndex() + 1, buf.writerIndex(), (byte) 0x7e);
            if (index != -1) {
                ByteBuf result = Unpooled.buffer(index + 1 - buf.readerIndex());

                while (buf.readerIndex() <= index) {
                    int b = buf.readUnsignedByte();
                    if (b == 0x7d) {
                        int ext = buf.readUnsignedByte();
                        if (ext == 0x01) {
                            result.writeByte(0x7d);
                        } else if (ext == 0x02) {
                            result.writeByte(0x7e);
                        }
                    } else {
                        result.writeByte(b);
                    }
                }

                return result;
            }
        }

        return null;
    }

}
