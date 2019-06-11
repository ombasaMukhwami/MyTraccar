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

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.traccar.BaseProtocolDecoder;
import org.traccar.DeviceSession;
import org.traccar.NetworkMessage;
import org.traccar.Protocol;
import org.traccar.helper.Parser;
import org.traccar.helper.PatternBuilder;
import org.traccar.helper.UnitsConverter;
import org.traccar.model.Position;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class C2stekProtocolDecoder  extends BaseProtocolDecoder {

    public C2stekProtocolDecoder(Protocol protocol) {
        super(protocol);
    }

    private static final Pattern PATTERN = new PatternBuilder()
            .text("PA$")
            .number("(d+)")                      // imei
            .text("$")
            .expression(".#")                    // data type
            .number("(dd)(dd)(dd)#")             // date (yymmdd)
            .number("(dd)(dd)(dd)#")             // time (hhmmss)
            .number("([01])#")                   // valid
            .number("([+-]?d+.d+)#")             // latitude
            .number("([+-]?d+.d+)#")             // longitude
            .number("(d+.d+)#")                  // speed
            .number("(d+.d+)#")                  // course
            .number("(-?d+.d+)#")                // altitude
            .number("(d+)#")                     // battery
            .number("d+#")                       // geo area alarm
            .number("(x+)#")                     // alarm
            .number("([01])")                    // armed
            .number("([01])")                    // door
            .number("([01])#")                   // ignition
            .any()
            .text("$AP")
            .compile();

    private static final Pattern PATTERN_NEXT = new PatternBuilder()
            .text("PA$")
            .number("(d+)")                      // imei
            .text("$")
            .expression(".#")                    // data type
            .number("(dd)(dd)(dd)#")             // date (yymmdd)
            .number("(dd)(dd)(dd)#")             // time (hhmmss)
            .number("([01])#")                   // valid
            .number("([+-]?d+.d+)#")             // latitude
            .number("([+-]?d+.d+)#")             // longitude
            .number("(d+.d+)#")                  // speed
            .number("(d+.d+)#")                  // course
            .number("(-?d+.d+)#")                // altitude
            .number("(d+)#")                     // battery
            .number("d+#")                       // geo area alarm
            .number("(x+)#")                     // alarm
            .number("([01])")                    // door
            .number("([01])#")                   // ignition
            .any()
            .text("$AP")
            .compile();

    private static final Pattern PATTERN_EXTRA = new PatternBuilder()
            .text("PA$")
            .number("(d+)")                      // imei
            .text("$")
            .expression(".#")                    // data type
            .number("(dd)(dd)(dd)#")             // date (yymmdd)
            .number("(dd)(dd)(dd)#")             // time (hhmmss)
            .number("([01])#")                   // valid
            .number("([+-]?d+.d+)#")             // latitude
            .number("([+-]?d+.d+)#")             // longitude
            .number("(d+.d+)#")                  // speed
            .number("(d+.d+)#")                  // course
            .number("(-?d+.d+)#")                // altitude
            .number("(d+)#")                     // battery
            .number("d+#")                       // geo area alarm
            .number("(x+)#")                     // alarm
            .number("([01])")                    // door
            .number("([01])")                   // ignition
            .text("$AP")
            .compile();

    private static final Pattern PATTERN_ODM = new PatternBuilder()
            .text("PA$")
            .number("(d+)")                      // imei
            .text("$")
            .expression(".#")                    // data type
            .number("(dd)(dd)(dd)#")             // date (yymmdd)
            .number("(dd)(dd)(dd)#")             // time (hhmmss)
            .number("([01])#")                   // valid
            .number("([+-]?d+.d+)#")             // latitude
            .number("([+-]?d+.d+)#")             // longitude
            .number("(d+.d+)#")                  // speed
            .number("(d+.d+)#")                  // course
            .number("(-?d+.d+)#")                // altitude
            .number("(d+)#")                     // battery
            .number("([01])")                    // alarm arm/disarm
            .number("([01])")                    // door
            .number("([01])#")                   // ignition
            .number("(d+)")
            .text("$AP")
            .compile();

    private String decodeAlarm(int alarm) {
        switch (alarm) {
            case 0x1:
                return Position.ALARM_MOVEMENT;
            case 0x2:
                return Position.ALARM_SHOCK;
            case 0x3:
                return Position.ALARM_POWER_CUT;
            case 0x4:
                return Position.ALARM_OVERSPEED;
            case 0x5:
                return Position.ALARM_SOS;
            case 0x6:
                return Position.ALARM_DOOR;
            case 0x7:
            case 0x8:
                return Position.ALARM_POWER_ON;
            case 0x9:
                return Position.ALARM_POWER_OFF;
            case 0x10:
                return Position.ALARM_GLASS_BROKEN;
            case 0xA:
                return Position.ALARM_LOW_BATTERY;
            case 0xB:
                return Position.ALARM_CRASH;
            case 0xC:
                return Position.ALARM_FIRE;
            case 0xD:
                return Position.ALARM_FUEL_LEAK;
            case 0xE:
                return Position.ALARM_INTRUSION;
            case 0xF:
                return Position.ALARM_BOOT_OPEN;
            default:
                return null;
        }
    }
    private Object decodeOdm(Channel channel, SocketAddress remoteAddress, String sentence) {

        Parser parser = new Parser(PATTERN_ODM, sentence);
        if (!parser.matches()) {
            return null;
        }
        String imei = parser.next();
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, imei);
        if (deviceSession == null) {
            return null;
        }

        Position position = new Position(getProtocolName());
        position.setDeviceId(deviceSession.getDeviceId());

        position.setTime(parser.nextDateTime());
        position.setValid(parser.nextInt() > 0);
        position.setLatitude(parser.nextDouble());
        position.setLongitude(parser.nextDouble());
        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextDouble()));
        position.setCourse(parser.nextDouble());
        position.setAltitude(parser.nextDouble());
        position.set(Position.KEY_BATTERY, parser.nextInt() * 0.001);
        position.set(Position.KEY_ARMED, parser.nextInt() > 0);
        position.set(Position.KEY_DOOR, parser.nextInt() > 0);
        position.set(Position.KEY_IGNITION, parser.nextInt() > 0);
        int alarm = parser.nextHexInt();
        position.set(Position.KEY_ALARM, decodeAlarm(alarm));
        sendReply(channel, remoteAddress, position, imei);
        return  position;
    }
    private Object decodeBasic(Channel channel, SocketAddress remoteAddress, String sentence) {

        Parser parser = new Parser(PATTERN, sentence);
        if (!parser.matches()) {
            return null;
        }
        String imei = parser.next();
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, imei);
        if (deviceSession == null) {
            return null;
        }

        Position position = new Position(getProtocolName());
        position.setDeviceId(deviceSession.getDeviceId());

        position.setTime(parser.nextDateTime());
        position.setValid(parser.nextInt() > 0);
        position.setLatitude(parser.nextDouble());
        position.setLongitude(parser.nextDouble());
        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextDouble()));
        position.setCourse(parser.nextDouble());
        position.setAltitude(parser.nextDouble());

        position.set(Position.KEY_BATTERY, parser.nextInt() * 0.001);
        int alarm = parser.nextHexInt();
        position.set(Position.KEY_ALARM, decodeAlarm(alarm));

        position.set(Position.KEY_ARMED, parser.nextInt() > 0);
        position.set(Position.KEY_DOOR, parser.nextInt() > 0);
        position.set(Position.KEY_IGNITION, ignitionStatus(alarm) > 0);
        sendReply(channel, remoteAddress, position, imei);
        return  position;
    }

    private Object decodeOther(Channel channel, SocketAddress remoteAddress, String sentence) {

        Parser parser = new Parser(PATTERN_NEXT, sentence);
        if (!parser.matches()) {
            return decodeExtra(channel, remoteAddress, sentence);
          }

        String imei = parser.next();
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, imei);
        if (deviceSession == null) {
            return null;
        }

        Position position = new Position(getProtocolName());
        position.setDeviceId(deviceSession.getDeviceId());

        position.setTime(parser.nextDateTime());
        position.setValid(parser.nextInt() > 0);
        position.setLatitude(parser.nextDouble());
        position.setLongitude(parser.nextDouble());
        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextDouble()));
        position.setCourse(parser.nextDouble());
        position.setAltitude(parser.nextDouble());

        position.set(Position.KEY_BATTERY, parser.nextInt() * 0.001);
        int alarm = parser.nextHexInt();
        position.set(Position.KEY_ALARM, decodeAlarm(alarm));

        position.set(Position.KEY_ARMED, 0);
        position.set(Position.KEY_DOOR, parser.nextInt() > 0);
        position.set(Position.KEY_IGNITION, ignitionStatus(alarm) > 0);
        sendReply(channel, remoteAddress, position, imei);

        return  position;
    }

    private Object decodeExtra(Channel channel, SocketAddress remoteAddress, String sentence) {

        Parser parser = new Parser(PATTERN_EXTRA, sentence);
        if (!parser.matches()) {
           return decodeOdm(channel, remoteAddress, sentence);
        }

        String imei = parser.next();
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, imei);
        if (deviceSession == null) {
            return null;
        }

        Position position = new Position(getProtocolName());
        position.setDeviceId(deviceSession.getDeviceId());

        position.setTime(parser.nextDateTime());
        position.setValid(parser.nextInt() > 0);
        position.setLatitude(parser.nextDouble());
        position.setLongitude(parser.nextDouble());
        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextDouble()));
        position.setCourse(parser.nextDouble());
        position.setAltitude(parser.nextDouble());

        position.set(Position.KEY_BATTERY, parser.nextInt() * 0.001);
        int alarm = parser.nextHexInt();
        position.set(Position.KEY_ALARM, decodeAlarm(alarm));

        position.set(Position.KEY_ARMED, 0);
        position.set(Position.KEY_DOOR, parser.nextInt() > 0);
        position.set(Position.KEY_IGNITION, ignitionStatus(alarm) > 0);
        sendReply(channel, remoteAddress, position, imei);

        return  position;
    }
private int ignitionStatus(int alarm) {
    switch (alarm) {
        case 0x8:
            return 1;
        case 0x9:
            return 0;
        default:
            return 0;
    }

}

    private void sendReply(Channel channel, SocketAddress remoteAddress, Position position, String imei) {
        if (position.getAttributes().containsKey(Position.KEY_ALARM)) {

            String alarm = (String) position.getAttributes().get(Position.KEY_ALARM);
            switch (alarm) {
                case Position.ALARM_MOVEMENT:
                case Position.ALARM_SHOCK:
                case Position.ALARM_POWER_CUT:
                case Position.ALARM_OVERSPEED:
                case Position.ALARM_SOS:
                case Position.ALARM_DOOR:
                case Position.ALARM_POWER_ON:
                case Position.ALARM_POWER_OFF:
                case Position.ALARM_GLASS_BROKEN:
                case Position.ALARM_LOW_BATTERY:
                case Position.ALARM_CRASH:
                case Position.ALARM_FIRE:
                case Position.ALARM_FUEL_LEAK:
                case Position.ALARM_INTRUSION:
                case Position.ALARM_BOOT_OPEN:
                    //String uniqueId = getUniqueId(position.getDeviceId());
                    String sentence = String.format("PA$%s$16$AP", imei);
                    if (channel != null) {
                        channel.writeAndFlush(new NetworkMessage(
                                Unpooled.copiedBuffer(sentence, StandardCharsets.US_ASCII), remoteAddress));
                    }
                    break;
                    default:
                        break;
            }
        }

    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        String sentence = (String) msg;
        if ((sentence.contains("$22$") || sentence.contains("$20$")) && channel != null) {
            channel.writeAndFlush(new NetworkMessage(sentence, remoteAddress));
        }

        Parser parser = new Parser(PATTERN, (String) msg);
        if (!parser.matches()) {
            return decodeOther(channel, remoteAddress, sentence);
        } else {
            return decodeBasic(channel, remoteAddress, sentence);
        }

    }

}
