/*
 * Copyright 2017 - 2018 Anton Tananaev (anton@traccar.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.protocol;

import org.traccar.BaseProtocolDecoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.traccar.Protocol;

import java.net.SocketAddress;

public class QueclinkProtocolDecoder extends BaseProtocolDecoder {

    private final QueclinkTextProtocolDecoder textProtocolDecoder;
    private final QueclinkBinaryProtocolDecoder binaryProtocolDecoder;

    public QueclinkProtocolDecoder(Protocol protocol) {
        super(protocol);
        textProtocolDecoder = new QueclinkTextProtocolDecoder(protocol);
        binaryProtocolDecoder = new QueclinkBinaryProtocolDecoder(protocol);
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        ByteBuf buf = (ByteBuf) msg;

        if (QueclinkFrameDecoder.isBinary(buf)) {
            return binaryProtocolDecoder.decode(channel, remoteAddress, msg);
        } else {
            return textProtocolDecoder.decode(channel, remoteAddress, msg);
        }
    }

}
