package org.traccar.protocol;


import org.traccar.BaseFrameDecoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SaboFrameDecoder extends BaseFrameDecoder {

    private static final int MINIMUM_LENGTH = 11;

    private static final Set<String> BINARY_HEADERS = new HashSet<>(
            Arrays.asList("??"));

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
            return buf.readRetainedSlice(buf.readableBytes());
        }

    }

}
