
package org.traccar.protocol;
import org.traccar.BaseProtocolDecoder;
import org.traccar.DeviceSession;
import org.traccar.Protocol;
import org.traccar.helper.Parser;
import org.traccar.helper.PatternBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.traccar.helper.UnitsConverter;
import org.traccar.model.Position;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;

import java.util.regex.Pattern;
public class Huabao1TextProtocolDecoder extends BaseProtocolDecoder {

    public Huabao1TextProtocolDecoder(Protocol protocol) {
        super(protocol);
    }
    private static final Pattern PATTERN = new PatternBuilder()
            .number("(dd)/(dd)/(dd),")     // date (ddmmyyyy)
            .number("(dd):(dd):(dd),")     // time (hhmmss)
            .number("(d+),")               // imei
            .expression("(.+),")           //VendorId
            .expression("(.+),")           //Vehicle registration
            .number("(d+),")               // speed
            .number("(d+.d+),")            // longitute
            .expression("([EW]),")         //direction
            .number("(d+.d+),")            // latitute
            .expression("([NS]),")         //Direction
            .number("([01]),")             //ignition
            .number("([01])")              //Power status
            .any()
            .compile();

    private static final Pattern PATTERN_NEXT = new PatternBuilder()
            .number("(dd)(dd)(dd),")     // date (ddmmyyyy)
            .number("(dd):(dd):(dd),")     // time (hhmmss)
            .number("(d+),")               // imei
            .expression("(.+),")           //VendorId
            .expression("(.+),")           //Vehicle registration
            .number("(d+),")               // speed
            .number("(d+.d+),")            // longitute
            .expression("([EW]);")         //directionh
            .number("(d+.d+),")            // latitute
            .expression("([NS]);")         //Direction
            .number("([01]),")             //ignition
            .number("([01])")              //Power status
            .any()
            .compile();
    private static final Pattern PATTERN_YEAR = new PatternBuilder()
            .number("(dddd)-(dd)-(dd),")   // date (ddmmyyyy)
            .number("(dd):(dd):(dd),")     // time (hhmmss)
            .number("(d+),")               // imei
            .expression("(.+),")           //VendorId
            .expression("(.+),")           //Vehicle registration
            .number("(d+),")               // speed
            .number("(d+),")               // course
            .number("(d+),")               // 2
            .number("(d+),")               // 0
            .number("(d+.d+),")            // longitute
            .expression("([EW])")          //direction
            .expression("(.+),")         //(+-)
            .number("(d+.d+),")            // latitute
            .expression("([NS])")          //Direction
            .expression("(.+),")           //(+-)
            .number("([01]),")             //ignition
            .number("([01]),")             //Power status
            .number("([01])")              //Antenna status
            .any()
            .compile();
    private static final Pattern PATTERN_EVENT = new PatternBuilder()
            .number("(dddd)-(dd)-(dd),")   // date (ddmmyyyy)
            .number("(dd):(dd):(dd),")     // time (hhmmss)
            .number("(d+),")               // imei
            .expression("(.+),")           //VendorId
            .expression("(.+),")           //Vehicle registration
            .number("(d+),")               // speed
            .number("(d+),")               // course
            .number("(d+),")               // 2
            .number("(d+),")               // 0
            .number("(d+.d+),")            // longitute
            .expression("([EW])")          //direction
            .expression("(.+),")         //(+-)
            .number("(d+.d+),")            // latitute
            .expression("([NS])")          //Direction
            .expression("(.+),")           //(+-)
            .number("([01]),")             //ignition
            .number("([01]),")             //Power status
            .number("([01]),")              //Antenna status
            .expression("(.+)")
            .any()
            .compile();

    private static final Pattern PATTERN_KARIBIA = new PatternBuilder()
            .number("(dd)/(dd)/(dd),")         // date (ddmmyyyy)
            .number("(dd):(dd):(dd),")           // time (hhmmss)
            .number("(d+),")                     // imei
            .expression("(.+),")                 //VendorId
            .expression("(.+),")                 //Vehicle registration
            .number("(d+),")                     // speed
            .number("(d+.d+),")                    // Longitute
            .expression("([NS]),")               //direction
            .number("(d+.d+),")                  // Latitute
            .expression("([EW]),")               //Direction
            .number("([01]),")                  //Power Connection(1 diconnected, 0 connected)
            .number("([01]),")                  //Speed Signal (0 disconnected
            .number("(d+),")                   //Course
            .number("([01]),")                 //Ignition status 1-on, 0 off
            .number("([01])")                  //Antennar disconnection 1 disconnected
            .any()
            .compile();

    public Object decodeKaribia(Parser parser, Position position, Channel channel, SocketAddress remoteAddress) throws Exception {
        position.setTime(parser.nextDateTime(Parser.DateTimeFormat.MDY_HMS));
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }
        double lat, lon;
        String latDir, lonDir;
        position.setDeviceId(deviceSession.getDeviceId());
        position.set(Position.KEY_VENDORID, parser.next());
        position.set(Position.KEY_VEHICLE_REGISTRATION, parser.next());
        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextInt()));
        lat = parser.nextDouble();
        latDir = parser.next();
        lon = parser.nextDouble();
        lonDir = parser.next();
        position.set(Position.KEY_POWER_SOURCE, parser.nextInt());
        position.set(Position.KEY_SPEED_SOURCE, parser.nextInt());
        position.setCourse(parser.nextInt());
        position.set(Position.KEY_STATUS, parser.next());
        position.set(Position.KEY_IGNITION, parser.nextInt() > 0);
       // String test = parser.next();
       // position.set(Position.KEY_ANTENNA, test);
        position.set(Position.KEY_GOVERNOR, 1);
        if (latDir.equalsIgnoreCase("S")) {
            lat = -1 * lat;
        }

        if (lonDir.equalsIgnoreCase("W")) {
            lon = -1 * lon;
        }
        position.setLongitude(lon);
        position.setLatitude(lat);
        position.set(Position.KEY_LONGITUTE_DIRECTION, lonDir);
        position.set(Position.KEY_LATITUTE_DIRECTION, latDir);

        return position;
    }

    private String decodeAlarm(String alarm) {

        switch (alarm) {
            case "A1":
                return Position.ALARM_POWER_ON;
            case "A2":
                return Position.ALARM_POWER_OFF;
            case "A3":
                return Position.ALARM_OVERSPEED;
            case "A4":
            case "A5":
            case "A6":
            case "A7":
                return Position.ALARM_POWER_CUT;
            case "A8":
                return Position.ALARM_POWER_RESTORED;
            case "A9":
            case "A10":
            case "A11":
                return Position.ALARM_GPS_ANTENNA_CUT;
            case "A12":
                return Position.ALARM_GPS_RECONNECTED;
                default:
                    return  null;
        }
    }


    public Object decodeEvent(Parser parser, Position position, Channel channel, SocketAddress remoteAddress) throws Exception {
        position.setTime(parser.nextDateTime(Parser.DateTimeFormat.YMD_HMS));
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }
        double lat, lon;
        String latDir, lonDir;
        position.setDeviceId(deviceSession.getDeviceId());
        position.set(Position.KEY_VENDORID, parser.next());
        position.set(Position.KEY_VEHICLE_REGISTRATION, parser.next());
        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextInt()));
        position.set(Position.KEY_ODOMETER, parser.nextInt());
        position.set(Position.KEY_GPSFIX, parser.nextInt());
        position.set(Position.KEY_SATELLITES, parser.nextInt());
        lon = parser.nextDouble();
        lonDir = parser.next();
        parser.next();
        position.set(Position.KEY_LONGITUTE_DIRECTION, lonDir);
        lat = parser.nextDouble();
        latDir = parser.next();
        parser.next();
        position.set(Position.KEY_LATITUTE_DIRECTION, latDir);
        position.set(Position.KEY_IGNITION, parser.nextInt() > 0);
        position.set(Position.KEY_STATUS, parser.next());
        parser.next();
        position.set(Position.KEY_ALARM, decodeAlarm(parser.next()));
        if (latDir.equalsIgnoreCase("S")) {
            lat = -1 * lat;
        }

        if (lonDir.equalsIgnoreCase("W")) {
            lon = -1 * lon;
        }
        position.setLongitude(lon);
        position.setLatitude(lat);
        return position;
    }
    public Object decodeYear(Parser parser, Position position, Channel channel, SocketAddress remoteAddress) throws Exception {
        position.setTime(parser.nextDateTime(Parser.DateTimeFormat.YMD_HMS));
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }
        double lat, lon;
        String latDir, lonDir;
        position.setDeviceId(deviceSession.getDeviceId());
        position.set(Position.KEY_VENDORID, parser.next());
        position.set(Position.KEY_VEHICLE_REGISTRATION, parser.next());
        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextInt()));
        position.set(Position.KEY_ODOMETER, parser.nextInt());
        position.set(Position.KEY_GPSFIX, parser.nextInt());
        position.set(Position.KEY_SATELLITES, parser.nextInt());
        lon = parser.nextDouble();
        lonDir = parser.next();
        parser.next();
        position.set(Position.KEY_LONGITUTE_DIRECTION, lonDir);
        lat = parser.nextDouble();
        latDir = parser.next();
        parser.next();
        position.set(Position.KEY_LATITUTE_DIRECTION, latDir);
        position.set(Position.KEY_IGNITION, parser.nextInt() > 0);
        position.set(Position.KEY_STATUS, parser.next());
        position.set(Position.KEY_GOVERNOR, 1);
        if (latDir.equalsIgnoreCase("S")) {
            lat = -1 * lat;
        }

        if (lonDir.equalsIgnoreCase("W")) {
            lon = -1 * lon;
        }
        position.setLongitude(lon);
        position.setLatitude(lat);
        return position;
    }

    public Object decodeNext(Parser parser, Position position, Channel channel, SocketAddress remoteAddress) throws Exception {
        position.setTime(parser.nextDateTime(Parser.DateTimeFormat.MDY_HMS));
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }
        double lat, lon;
        String latDir, lonDir;
        position.setDeviceId(deviceSession.getDeviceId());
        position.set(Position.KEY_VENDORID, parser.next());
        position.set(Position.KEY_VEHICLE_REGISTRATION, parser.next());
        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextInt()));
        lon = parser.nextDouble();
        lonDir = parser.next();
        position.set(Position.KEY_LONGITUTE_DIRECTION, lonDir);
        lat = parser.nextDouble() / 60.0;
        latDir = parser.next();
        position.set(Position.KEY_LATITUTE_DIRECTION, latDir);
        position.set(Position.KEY_IGNITION, parser.nextInt() > 0);
        position.set(Position.KEY_STATUS, parser.next());
        position.set(Position.KEY_GOVERNOR, 1);
        if (latDir.equalsIgnoreCase("S")) {
            lat = -1 * lat;
        }

        if (lonDir.equalsIgnoreCase("W")) {
            lon = -1 * lon;
        }
        position.setLongitude(lon);
        position.setLatitude(lat);
        return position;
    }

    public Object decodeMessage(Parser parser, Position position, Channel channel, SocketAddress remoteAddress) throws Exception {
        position.setTime(parser.nextDateTime(Parser.DateTimeFormat.MDY_HMS));
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }
        double lat, lon;
        String latDir, lonDir;
        position.setDeviceId(deviceSession.getDeviceId());
        position.set(Position.KEY_VENDORID, parser.next());
        position.set(Position.KEY_VEHICLE_REGISTRATION, parser.next());
        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextInt()));
        lon = parser.nextDouble();
        lonDir = parser.next();
        position.set(Position.KEY_LONGITUTE_DIRECTION, lonDir);
        lat = parser.nextDouble();
        latDir = parser.next();
        position.set(Position.KEY_LATITUTE_DIRECTION, latDir);
        position.set(Position.KEY_IGNITION, parser.nextInt() > 0);
        position.set(Position.KEY_STATUS, parser.next());
        position.set(Position.KEY_GOVERNOR, 1);
        if (latDir.equalsIgnoreCase("S")) {
            lat = -1 * lat;
        }

        if (lonDir.equalsIgnoreCase("W")) {
            lon = -1 * lon;
        }
        position.setLongitude(lon);
        position.setLatitude(lat);
        return position;
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {
        String sentence = ((ByteBuf) msg).toString(StandardCharsets.US_ASCII);
        Pattern pattern = PATTERN;
        Parser parser = new Parser(pattern, sentence);
        Position position = new Position(getProtocolName());
        if (!parser.matches()) {
            pattern = PATTERN_NEXT;
            parser = new Parser(pattern, sentence);
            if (!parser.matches()) {
                pattern = PATTERN_YEAR;
                parser = new Parser(pattern, sentence);
                if (!parser.matches()) {
                    pattern = PATTERN_EVENT;
                    parser = new Parser(pattern, sentence);
                    if (!parser.matches()) {
                        pattern = PATTERN_KARIBIA;
                        parser = new Parser(pattern, sentence);
                        if (!parser.matches()) {
                            return null;
                        }
                        return decodeKaribia(parser, position, channel, remoteAddress);
                    }
                    return decodeEvent(parser, position, channel, remoteAddress);
                }
                return decodeYear(parser, position, channel, remoteAddress);
            }
            return decodeNext(parser, position, channel, remoteAddress);
        }
        return decodeMessage(parser, position, channel, remoteAddress);
    }
}
