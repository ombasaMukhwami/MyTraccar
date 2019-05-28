package org.traccar.protocol;

import io.netty.channel.Channel;
import org.traccar.BaseProtocolDecoder;
import org.traccar.DeviceSession;
import org.traccar.Protocol;
import org.traccar.helper.Parser;
import org.traccar.helper.PatternBuilder;
import org.traccar.helper.UnitsConverter;
import org.traccar.model.Position;


import java.net.SocketAddress;
import java.util.regex.Pattern;

public class Wialon1ProtocolDecoder extends BaseProtocolDecoder {

    public Wialon1ProtocolDecoder(Protocol protocol) {

        super(protocol);
    }

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
            .number("(dd)(d+.d+),")              //Longitute
            .expression("([EW]),")               //direction
            .number("(d+.d+),")                  //latitute
            .expression("([NS]),")               //Direction
            .number("([01]),")                   //ignition
            .number("([01])")                    //Power status
            .any()
            .compile();




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
    protected Object decode(Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        String sentence = (String) msg;
        Pattern pattern = PATTERN_NTSA;
        Parser parser = new Parser(pattern, sentence);
        if (!parser.matches()) {
            pattern = PATTERN_COM;
            parser = new Parser(pattern, sentence);
            if (!parser.matches()) {
                return null;
            }
            return ntsaCom(parser, channel, remoteAddress);
        }

        return ntsaGovernor(parser, channel, remoteAddress);

    }

}
