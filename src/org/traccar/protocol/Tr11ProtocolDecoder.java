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
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.traccar.BaseProtocolDecoder;
import org.traccar.DeviceSession;
import org.traccar.NetworkMessage;
import org.traccar.Protocol;
import org.traccar.helper.BcdUtil;
import org.traccar.helper.DateBuilder;
import org.traccar.helper.Helper;
import org.traccar.helper.UnitsConverter;
import org.traccar.model.Position;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

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
    public static final int MSG_POSITION = 0x80;
    public static final int MSG_TRACKER_CONFIRMATION = 0x85;
    public static final int MSG_SETUP_ACC_ON_LCATION_UPDATE = 0x38;
    public static final int MSG_END = 0x0D;
    public static final int MSG_LENGTH_TO_SEND = 0x05;

    public byte verify(byte[] b) {
        byte a = 0;
        for (int i = 0; i < b.length; i++) {
            a = (byte) (a ^ b[i]);
        }
        return a;
    }
    private int getMessage(String checksum) {
        StringBuilder sb = new StringBuilder();
        sb.append("2929");
        sb.append("210005");
        sb.append(checksum);
        sb.append("B1");
        byte[] dataPacket = Helper.hexStringToByteArray(sb.toString());
        return verify(dataPacket);
    }
    private String getDeviceId(int f1, int f2, int f3, int f4) {

        if (f2 > 128) {
            f2 -= 128;
        }
        if (f3 > 128) {
            f3 -= 128;
        }
        return  String.format("%02d%02d%02d%02d", f1, f2, f3, f4);
    }
    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        //2929-80-0037-19D6C605-190329050716-037548350-23423740-00-00-347F81599157FFC5E00001E000000000000000500010000000006008801159915020D
        ByteBuf buf = (ByteBuf) msg;
        ByteBufUtil.hexDump(buf.readSlice(2));
        int type = buf.readUnsignedShortLE();
        ByteBufUtil.hexDump(buf.readSlice(1));
        Position position = new Position(getProtocolName());
        if (type == MSG_SHAKE_HAND_REQUEST && channel != null) {
            //29-29-B1-00-07-19-D6-C6-05-0C-B6-0D
            ByteBuf response = Unpooled.buffer();
            ByteBufUtil.hexDump(buf.readSlice(5));
            int cksum = buf.readUnsignedShortLE();
            int  checksum = getMessage(Helper.byteToHex(cksum));
            response.writeShort(0x2929);
            response.writeByte(33);
            response.writeShort(MSG_LENGTH_TO_SEND);
            response.writeByte(cksum);
            response.writeShortLE(MSG_SHAKE_HAND_REQUEST);
            response.writeByte(checksum);
            response.writeByte('\r');

            channel.writeAndFlush(new NetworkMessage(response, channel.remoteAddress()));
            return null;
        } else if (type == MSG_CANCEL_ALARM) {
          System.out.print("Test");
        } else if (type == MSG_SETUP_ACC) {
            System.out.print("Test");
        } else if (type == MSG_POSITION_DATA || type == MSG_POSITION) {


            String deviceId = getDeviceId(buf.readUnsignedByte(), buf.readUnsignedByte(), buf.readUnsignedByte(), buf.readUnsignedByte());

            //String deviceId =  ByteBufUtil.hexDump(buf.readSlice(4));
            DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, deviceId);
            if (deviceSession == null) {
                return null;
            }
          //2929-80-0037-19D6C605-190329050716-03754835-02342374-0000-0347-F81599157FFC5E00001E000000000000000500010000000006008801159915020D
            position.setDeviceId(deviceSession.getDeviceId());
            double latitute, longitute;
            String lat, lon, latH, lonh, latmin, lonmin;
            DateBuilder dateBuilder = new DateBuilder()
                    .setYear(BcdUtil.readInteger(buf, 2))
                    .setMonth(BcdUtil.readInteger(buf, 2))
                    .setDay(BcdUtil.readInteger(buf, 2))
                    .setHour(BcdUtil.readInteger(buf, 2))
                    .setMinute(BcdUtil.readInteger(buf, 2))
                    .setSecond(BcdUtil.readInteger(buf, 2));
            position.setTime(dateBuilder.getDate());
            lat = ByteBufUtil.hexDump(buf.readSlice(4));
            lon = ByteBufUtil.hexDump(buf.readSlice(4));
            latH = lat.substring(0, 3);
            latmin = lat.substring(3);
            lonh = lon.substring(0, 3);
            lonmin = lon.substring(3);
            latitute = Double.parseDouble(latH) + (Double.parseDouble(latmin) / (60 * 1000));
            longitute = Double.parseDouble(lonh) + (Double.parseDouble(lonmin) / (60 * 1000));
            position.setLatitude(latitute);
            position.setLongitude(longitute);
            position.setSpeed(UnitsConverter.knotsFromKph(BcdUtil.readInteger(buf, 4)));
            position.setCourse(BcdUtil.readInteger(buf, 4));
            int vap = Integer.parseInt(ByteBufUtil.hexDump(buf.readSlice(1)), 16);
            int odometer = buf.readUnsignedMedium();
            position.set(Position.KEY_ODOMETER, odometer / 1000.0);
            String validAtennaPower = Integer.toBinaryString(vap);
            //String validAtennaPower ="01234567";
            int gps = Integer.parseInt(validAtennaPower.substring(0, 1));
            int antenna = Integer.parseInt(validAtennaPower.substring(1, 3));
            int power = Integer.parseInt(validAtennaPower.substring(3, 5));

             String a = Helper.toBinary(Integer.parseInt(ByteBufUtil.hexDump(buf.readSlice(1)),  16));
            String b = Helper.toBinary(Integer.parseInt(ByteBufUtil.hexDump(buf.readSlice(1)),  16));
            String c = Helper.toBinary(Integer.parseInt(ByteBufUtil.hexDump(buf.readSlice(1)),  16));
            String d = Helper.toBinary(Integer.parseInt(ByteBufUtil.hexDump(buf.readSlice(1)),  16));
            int ignition = Integer.parseInt(a.substring(0, 1));
            if (ignition == 1) {
                position.set(Position.KEY_IGNITION, false);
            } else {
                position.set(Position.KEY_IGNITION, true);
            }
            position.set(Position.KEY_GPS, gps);
            position.set(Position.KEY_ANTENNA, antenna);


        }
        return position;
    }

}
