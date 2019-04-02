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

public class SmartFastProtocolDecoder extends BaseProtocolDecoder {

    public SmartFastProtocolDecoder(Protocol protocol) {

        super(protocol);
    }

    private static final Pattern PATTERN = new PatternBuilder()
            .number("(dddd)/(dd)/(dd),")         // date (yymmdd)
            .number("(dd):(dd):(dd),")           // time (hhmmss)
            .number("(d+),")                     // imei
            .expression("(.+),")                 //VendorId
            .expression("(.+),")                 //Vehicle registration
            .number("(d+),")                  // speed
            .number("(d+.d+) ")                  // Longitute
            .expression("([EW]),")               //direction
            .number("(d+.d+) ")                  // latitute
            .expression("([NS]),")               //Direction
            .number("([01]),")                  //ignition
            .number("([01])")                  //Power status
            .any()
            .compile();


    private Object decodeMessage(Parser parser, Position position, Channel channel, SocketAddress remoteAddress) {

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
        lon = parser.nextDouble();

        lonDir = parser.next();
        position.set(Position.KEY_LONGITUTE_DIRECTION, lonDir);
        lat = parser.nextDouble();
        latDir = parser.next();
        position.set(Position.KEY_LATITUTE_DIRECTION, latDir);
        position.set(Position.KEY_IGNITION, parser.nextInt() > 0);
        position.set(Position.KEY_STATUS, parser.next());

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
    protected Object decode(Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        String sentence = (String) msg;
        sentence = "20" + sentence;
        Pattern pattern = PATTERN;
        Parser parser = new Parser(pattern, sentence);
        if (!parser.matches()) {
            return null;
        }
        Position position = new Position(getProtocolName());
        return decodeMessage(parser, position, channel, remoteAddress);

    }

}
