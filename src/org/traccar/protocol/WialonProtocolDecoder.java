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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WialonProtocolDecoder extends BaseProtocolDecoder {

    public WialonProtocolDecoder(Protocol protocol) {
        super(protocol);
    }

    private static final Pattern PATTERN = new PatternBuilder()
            .number("(dd)(dd)(dd);")             // date (ddmmyy)
            .number("(dd)(dd)(dd);")             // time (hhmmss)
            .number("(dd)(dd.d+);")              // latitude
            .expression("([NS]);")
            .number("(ddd)(dd.d+);")             // longitude
            .expression("([EW]);")
            .number("(d+.?d*)?;")                // speed
            .number("(d+.?d*)?;")                // course
            .number("(?:NA|(-?d+.?d*));")        // altitude
            .number("(?:NA|(d+))")               // satellites
            .groupBegin().text(";")
            .number("(?:NA|(d+.?d*));")          // hdop
            .number("(?:NA|(d+));")              // inputs
            .number("(?:NA|(d+));")              // outputs
            .expression("(?:NA|([^;]*));")       // adc
            .expression("(?:NA|([^;]*));")       // ibutton
            .expression("(?:NA|(.*))")           // params
            .groupEnd("?")
            .compile();

    private static final Pattern PATTERN_NTSA = new PatternBuilder()
            .text("#")
            .number("(dd)(dd)(dd),")             //date (ddmmyy)
            .number("(dd):(dd):(dd),")           //time (hhmmss)
            .number("(d+),")                     //imei
            .expression("(.+),")                 //VendorId
            .expression("(.+),")                 //Vehicle registration
            .number("(d+),")                     //speed
            .number("(dd)(d+.d+);")             //Longitute
            .expression("([EW]);")               //direction
            .number("(d+.d+);")                 //latitute
            .expression("([NS]),")               //Direction
            .number("([01]),")                   //ignition
            .number("([01])")                    //Power status
            .any()
            .compile();

    private static final Pattern PATTERN_COM = new PatternBuilder()
            .number("(dd)(dd)(dd),")             //date (ddmmyy)
            .number("(dd):(dd):(dd),")           //time (hhmmss)
            .number("(d+),")                     //imei
            .expression("(.+),")                 //VendorId
            .expression("(.+),")                 //Vehicle registration
            .number("(d+),")                     //speed
            .number("(dd)(d+.d+),")             //Longitute
            .expression("([EW]),")               //direction
            .number("(d{0,1})(dd.d+),")                 //latitute
            .expression("([NS]),")               //Direction
            .number("([01]),")                   //ignition
            .number("([01])")                    //Singal status
            .any()
            .compile();

    private static final Pattern PATTERN_X = new PatternBuilder()
            .number("(dd)(dd)(dd),")             //date (ddmmyy)
            .number("(d{1,3}):(d{1,2}):(d{1,2}),")           //time (hhmmss)
            .number("(d+),")                     //imei
            .expression("(.+),")                 //VendorId
            .expression("(.+),")                 //Vehicle registration
            .number("(d+),")                     //speed
            .number("(dd)(d+.d+);")             //Longitute
            .expression("([EW]);")               //direction
            .number("(d{0,1})(dd.d+);")           //latitute
            .expression("([NS]),")               //Direction
            .number("([01]),")                   //ignition
            .number("([01])")                    //signal status
            .any()
            .compile();

    private void sendResponse(Channel channel, SocketAddress remoteAddress, String type, Integer number) {
        if (channel != null) {
            StringBuilder response = new StringBuilder("#A");
            response.append(type.substring(1));
            if (number != null) {
                response.append(number);
            }
            response.append("\r\n");
            channel.writeAndFlush(new NetworkMessage(response.toString(), remoteAddress));
        }
    }

    private Position decodePosition(Channel channel, SocketAddress remoteAddress, String substring) {

        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress);
        if (deviceSession == null) {
            return null;
        }

        Parser parser = new Parser(PATTERN, substring);
        if (!parser.matches()) {
            return null;
        }

        Position position = new Position(getProtocolName());
        position.setDeviceId(deviceSession.getDeviceId());

        position.setTime(parser.nextDateTime(Parser.DateTimeFormat.DMY_HMS));

        position.setLatitude(parser.nextCoordinate());
        position.setLongitude(parser.nextCoordinate());
        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextDouble(0)));
        position.setCourse(parser.nextDouble(0));
        position.setAltitude(parser.nextDouble(0));

        if (parser.hasNext()) {
            int satellites = parser.nextInt(0);
            position.setValid(satellites >= 3);
            position.set(Position.KEY_SATELLITES, satellites);
        }

        position.set(Position.KEY_HDOP, parser.nextDouble());
        position.set(Position.KEY_INPUT, parser.next());
        position.set(Position.KEY_OUTPUT, parser.next());

        if (parser.hasNext()) {
            String[] values = parser.next().split(",");
            for (int i = 0; i < values.length; i++) {
                position.set(Position.PREFIX_ADC + (i + 1), values[i]);
            }
        }

        position.set(Position.KEY_DRIVER_UNIQUE_ID, parser.next());

        if (parser.hasNext()) {
            String[] values = parser.next().split(",");
            for (String param : values) {
                Matcher paramParser = Pattern.compile("(.*):[1-3]:(.*)").matcher(param);
                if (paramParser.matches()) {
                    try {
                        position.set(paramParser.group(1).toLowerCase(), Double.parseDouble(paramParser.group(2)));
                    } catch (NumberFormatException e) {
                        position.set(paramParser.group(1).toLowerCase(), paramParser.group(2));
                    }
                }
            }
        }

        return position;
    }

    private Object ntsaGovernor(Parser parser, Channel channel, SocketAddress remoteAddress) {

        Position position = new Position(getProtocolName());
        position.setTime(parser.nextDateTime(Parser.DateTimeFormat.MDY_HMS));
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }

        position.setDeviceId(deviceSession.getDeviceId());
        position.set(Position.KEY_VENDORID, parser.next());
        position.set(Position.KEY_VEHICLE_REGISTRATION, parser.next());
        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextInt()));
        position.setLongitude(parser.nextCoordinate());
        position.setLatitude(parser.nextCoordinate());
        position.set(Position.KEY_IGNITION, parser.nextInt() > 0);
        position.set(Position.KEY_STATUS, parser.next());
        position.set(Position.KEY_GOVERNOR, 1);
        return position;
    }
    private Object ntsaCom(Parser parser, Channel channel, SocketAddress remoteAddress) {

        Position position = new Position(getProtocolName());
        position.setTime(parser.nextDateTime(Parser.DateTimeFormat.MDY_HMS));
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }

        position.setDeviceId(deviceSession.getDeviceId());
        position.set(Position.KEY_VENDORID, parser.next());
        position.set(Position.KEY_VEHICLE_REGISTRATION, parser.next());
        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextInt()));
        position.setLongitude(parser.nextCoordinate());
        position.setLatitude(parser.nextCoordinate());
        position.set(Position.KEY_IGNITION, parser.nextInt() > 0);
        position.set(Position.KEY_STATUS, parser.next());
        position.set(Position.KEY_GOVERNOR, 1);
        return position;
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        String sentence = (String) msg;
        String type = sentence.substring(0, sentence.indexOf('#', 1) + 1);

        switch (type) {

            case "#L#":
                String[] values = sentence.substring(3).split(";");

                String imei = values[0].indexOf('.') >= 0 ? values[1] : values[0];
                DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, imei);
                if (deviceSession != null) {
                    sendResponse(channel, remoteAddress, type, 1);
                }
                break;

            case "#P#":
                sendResponse(channel, remoteAddress, type, null); // heartbeat
                break;

            case "#D#":
            case "#SD#":
                Position position = decodePosition(
                        channel, remoteAddress, sentence.substring(sentence.indexOf('#', 1) + 1));

                if (position != null) {
                    sendResponse(channel, remoteAddress, type, 1);
                    return position;
                }
                break;

            case "#B#":
                String[] messages = sentence.substring(sentence.indexOf('#', 1) + 1).split("\\|");
                List<Position> positions = new LinkedList<>();

                for (String message : messages) {
                    position = decodePosition(channel, remoteAddress, message);
                    if (position != null) {
                        position.set(Position.KEY_ARCHIVE, true);
                        positions.add(position);
                    }
                }

                sendResponse(channel, remoteAddress, type, messages.length);
                if (!positions.isEmpty()) {
                    return positions;
                }
                break;

            case "#M#":
                deviceSession = getDeviceSession(channel, remoteAddress);
                if (deviceSession != null) {
                    position = new Position(getProtocolName());
                    position.setDeviceId(deviceSession.getDeviceId());
                    getLastLocation(position, new Date());
                    position.setValid(false);
                    position.set(Position.KEY_RESULT, sentence.substring(sentence.indexOf('#', 1) + 1));
                    sendResponse(channel, remoteAddress, type, 1);
                    return position;
                }
                break;

            default:
                Pattern pattern = PATTERN_NTSA;
                Parser parser = new Parser(pattern, sentence);
                if (!parser.matches()) {
                    pattern = PATTERN_COM;
                    parser = new Parser(pattern, sentence);
                    if (!parser.matches()) {
                        pattern = PATTERN_X;
                        parser = new Parser(pattern, sentence);
                        if (!parser.matches()) {
                            return null;
                        }
                        return ntsaCom(parser, channel, remoteAddress);
                    }
                    return ntsaCom(parser, channel, remoteAddress);
                }

                return ntsaGovernor(parser, channel, remoteAddress);

        }

        return null;
    }

}
