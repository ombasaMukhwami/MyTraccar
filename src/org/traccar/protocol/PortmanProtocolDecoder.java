package org.traccar.protocol;


import io.netty.channel.Channel;
import org.traccar.BaseProtocolDecoder;
import org.traccar.DeviceSession;
import org.traccar.Protocol;
import org.traccar.helper.Parser;
import org.traccar.helper.PatternBuilder;
import org.traccar.model.Position;


import java.net.SocketAddress;
import java.util.regex.Pattern;

public class PortmanProtocolDecoder extends BaseProtocolDecoder {

    public PortmanProtocolDecoder(Protocol protocol) {

        super(protocol);
    }

    private static final Pattern PATTERN = new PatternBuilder()
            .number("(dddd)/(dd)/(dd),")         // date (ddmmyyyy)
            .number("(dd):(dd):(dd),")           // time (hhmmss)
            .number("(d+),")                     // imei
            .expression("(.+),")                 //VendorId
            .expression("(.+),")                 //Vehicle registration
            .number("(d+.d+),")                  // speed
            .number("([+-]d+.d+)")                  // latitude
            .expression("(.+) ")
            .expression("([NS]),")               //direction
            .number("(d+.d+)")                  // longitude
            .expression("(.+) ")
            .expression("([EW]),")               //Direction
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

        position.setDeviceId(deviceSession.getDeviceId());

        return position;
    }

    @Override
    protected Object decode(Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        String sentence = (String) msg;
        Pattern pattern = PATTERN;
        Parser parser = new Parser(pattern, sentence);
        Position position = new Position(getProtocolName());
        if (!parser.matches()) {
            return null;
        }
        return decodeMessage(parser, position, channel, remoteAddress);

    }

}
