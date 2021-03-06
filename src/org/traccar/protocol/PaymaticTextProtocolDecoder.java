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
public class PaymaticTextProtocolDecoder extends BaseProtocolDecoder {

    public PaymaticTextProtocolDecoder(Protocol protocol) {
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
            .number("(dd)/(dd)/(dd),")     // date (ddmmyyyy)
            .number("(dd):(dd):(dd),")     // time (hhmmss)
            .number("(d+),")               // imei
            .expression("(.+),")           //VendorId
            .expression("(.+),")           //Vehicle registration
            .number("(d+),")               // speed
            .number("(d+.d+),")            // longitute
            .expression("([NS]),")         //direction
            .number("(d+.d+),")            // latitute
            .expression("([EW]),")         //Direction
            .number("([01]),")             //ignition
            .number("([01])")              //Power status
            .any()
            .compile();

    public Object decodeMessage(Parser parser, Position position, Channel channel, SocketAddress remoteAddress) throws Exception {
        position.setTime(parser.nextDateTime(Parser.DateTimeFormat.DMY_HMS));
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
        if (latDir.equals("S")) {
            lat = -1 * lat;
        }

        if (lonDir.equals("W")) {
            lon = -1 * lon;
        }
        position.setLongitude(lon);
        position.setLatitude(lat);
        return position;
    }

    public Object decodeNext(Parser parser, Position position, Channel channel, SocketAddress remoteAddress) throws Exception {
        position.setTime(parser.nextDateTime(Parser.DateTimeFormat.DMY_HMS));
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
        if (lonDir.equals("S")) {
            lat = -1 * lat;
        }

        if (latDir.equals("W")) {
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
                return null;
            }
            return decodeNext(parser, position, channel, remoteAddress);
        }
        return decodeMessage(parser, position, channel, remoteAddress);
    }
}
