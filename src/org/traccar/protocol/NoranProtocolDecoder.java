/*
 * Copyright 2013 - 2018 Anton Tananaev (anton@traccar.org)
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
import org.traccar.DeviceSession;
import org.traccar.NetworkMessage;
import org.traccar.Protocol;
import org.traccar.helper.BitUtil;
import org.traccar.helper.DateBuilder;
import org.traccar.helper.UnitsConverter;
import org.traccar.model.Position;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class NoranProtocolDecoder extends BaseProtocolDecoder {

    public NoranProtocolDecoder(Protocol protocol) {
        super(protocol);
    }

    public static final int MSG_UPLOAD_POSITION = 0x0008;
    public static final int MSG_UPLOAD_POSITION_NEW = 0x0032;
    public static final int MSG_CONTROL = 0x0002;
    public static final int MSG_CONTROL_RESPONSE = 0x8009;
    public static final int MSG_ALARM = 0x0003;
    public static final int MSG_SHAKE_HAND = 0x0000;
    public static final int MSG_SHAKE_HAND_RESPONSE = 0x8000;
    public static final int MSG_IMAGE_SIZE = 0x0200;
    public static final int MSG_IMAGE_PACKET = 0x0201;

    //NORAN 34000800010B00000000000089430753134299B6A3BF4E523039423036323032000031392D30342D30382031333A32323A353400
    private Position decodeBasic(Position position, ByteBuf buf) {

        return position;
    }
private boolean getIgnition(int alarm, Position position) {
    if (alarm == 12) {
        return false;
    } else if (alarm == 11) {
        return true;
    } else {
        Position last = getLastLocation(position);
        if (last != null) {
            if (last.getAttributes().containsKey(Position.KEY_IGNITION)) {
                return  (boolean) last.getAttributes().get(Position.KEY_IGNITION);
            }
        }
        return true;
    }
}

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        ByteBuf buf = (ByteBuf) msg;
        short alarm;
        buf.readUnsignedShortLE(); // length
        int type = buf.readUnsignedShortLE();

        if (type == MSG_SHAKE_HAND && channel != null) {

            ByteBuf response = Unpooled.buffer(13);
            response.writeCharSequence("\r\n*KW", StandardCharsets.US_ASCII);
            response.writeByte(0);
            response.writeShortLE(response.capacity());
            response.writeShortLE(MSG_SHAKE_HAND_RESPONSE);
            response.writeByte(1); // status
            response.writeCharSequence("\r\n", StandardCharsets.US_ASCII);

            channel.writeAndFlush(new NetworkMessage(response, remoteAddress));

        } else if (type == MSG_UPLOAD_POSITION || type == MSG_UPLOAD_POSITION_NEW
                || type == MSG_CONTROL_RESPONSE || type == MSG_ALARM) {


            int test = buf.readableBytes();
            if (type == MSG_UPLOAD_POSITION && test == 48 || type == MSG_ALARM && test == 48 || type == MSG_CONTROL_RESPONSE && test == 57) {

            }

            Position position = new Position(getProtocolName());


            position.setValid(BitUtil.check(buf.readUnsignedByte(), 0));

            alarm = buf.readUnsignedByte();
            switch (alarm) {
                case 1:
                    position.set(Position.KEY_ALARM, Position.ALARM_SOS);
                    break;
                case 2:
                    position.set(Position.KEY_ALARM, Position.ALARM_OVERSPEED);
                    break;
                case 3:
                    position.set(Position.KEY_ALARM, Position.ALARM_GEOFENCE_EXIT);
                    break;
                case 9:
                    position.set(Position.KEY_ALARM, Position.ALARM_POWER_OFF);
                    break;
                default:
                    break;
            }

            position.setSpeed(UnitsConverter.knotsFromKph(buf.readUnsignedByte()));
            position.setCourse(buf.readUnsignedShortLE());
            position.setLongitude(buf.readFloatLE());
            position.setLatitude(buf.readFloatLE());

            ByteBuf rawId = buf.readSlice(12);
            String id = rawId.toString(StandardCharsets.US_ASCII).replaceAll("[^\\p{Print}]", "");
            DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, id);
            if (deviceSession == null) {
                return null;
            }
            position.setDeviceId(deviceSession.getDeviceId());
            DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            position.setTime(dateFormat.parse(buf.readSlice(17).toString(StandardCharsets.US_ASCII)));
            buf.readByte();
            position.set(Position.PREFIX_TEMP + 1, buf.readShortLE());
            position.set(Position.KEY_ODOMETER, buf.readFloatLE());
            position.set(Position.KEY_IGNITION, getIgnition(alarm, position));

            return position;
        }

        return null;
    }

}
