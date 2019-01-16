/*
 * Copyright 2014 - 2018 Anton Tananaev (anton@traccar.org)
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
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.traccar.BaseProtocolDecoder;
import org.traccar.NetworkMessage;
import org.traccar.Protocol;
import org.traccar.helper.Checksum;

import java.net.SocketAddress;
public class Tr11ProtocolDecoder extends BaseProtocolDecoder {
    public Tr11ProtocolDecoder(Protocol protocol) {
        super(protocol);
    }

    public static final int MSG_LOCATING = 0x30;
    public static final int MSG_SHAKE_HAND_RESPONSE = 0x21;
    public static final int MSG_SETUP_ACC = 0x34;
    public static final int MSG_DISABLE_IMMOBILIZER = 0x38;
    public static final int MSG_IMMOBILIZER = 0x39;
    public static final int MSG_VOICE_MONITOR = 0x3E;
    public static final int MSG_CANCEL_ALARM = 0x37;
    public static final int MSG_SHAKE_HAND_REQUEST = 0xB1;
    public static final int MSG_POSITION_DATA = 0x81;
    public static final int MSG_TRACKER_CONFIRMATION = 0x85;

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        ByteBuf buf = (ByteBuf) msg;
        buf.readUnsignedShortLE(); // length
        int type = buf.readUnsignedShortLE();
        if (type == MSG_SHAKE_HAND_REQUEST && channel != null) {
            ByteBuf response = Unpooled.buffer();
            response.writeShort(0x2929);
            response.writeShortLE(MSG_SHAKE_HAND_RESPONSE);
            response.writeShort(0X00);
            response.writeShort(0X05);
            response.writeShort(response.writerIndex() - 2);
            response.writeShort(MSG_SHAKE_HAND_REQUEST);
            response.writeShort(0x00);
            response.writeShort(Checksum.crc16(Checksum.CRC16_X25, response.nioBuffer(1,  response.writerIndex() - 2)));
            response.writeShort(0x0D);
            channel.writeAndFlush(new NetworkMessage(response, channel.remoteAddress()));
        } else if (type == MSG_CANCEL_ALARM) {
          System.out.print("Test");
        } else if (type == MSG_SETUP_ACC) {
            System.out.print("Test");
        } else if (type == MSG_POSITION_DATA) {
            System.out.print("Test");
        }
        return null;
    }

}
