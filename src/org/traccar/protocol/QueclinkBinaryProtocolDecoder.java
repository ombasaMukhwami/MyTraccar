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

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.traccar.BaseProtocolDecoder;
import org.traccar.DeviceSession;
import org.traccar.Protocol;
import org.traccar.helper.BitBuffer;
import org.traccar.helper.BitUtil;
import org.traccar.helper.DateBuilder;
import org.traccar.helper.UnitsConverter;
import org.traccar.model.CellTower;
import org.traccar.model.Network;
import org.traccar.model.Position;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class QueclinkBinaryProtocolDecoder extends BaseProtocolDecoder {

    private Gl200BinaryProtocolDecoder decoder;
    public QueclinkBinaryProtocolDecoder(Protocol protocol) {

        super(protocol);
        decoder = new Gl200BinaryProtocolDecoder(protocol);
    }



    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        ByteBuf buf = (ByteBuf) msg;

        switch (buf.readSlice(4).toString(StandardCharsets.US_ASCII)) {
            case "+RSP":
                return decoder.decodeLocation(channel, remoteAddress, buf);
            case "+INF":
                return decoder.decodeInformation(channel, remoteAddress, buf);
            case "+EVT":
                return decoder.decodeEvent(channel, remoteAddress, buf);
            default:
                return null;
        }
    }

}
