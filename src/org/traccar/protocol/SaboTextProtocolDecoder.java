
package org.traccar.protocol;

import io.netty.buffer.ByteBuf;
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

public class SaboTextProtocolDecoder extends BaseProtocolDecoder {
    private ByteBuf photo;

    public SaboTextProtocolDecoder(Protocol protocol) {

        super(protocol);
    }

    private static final Pattern PATTERN = new PatternBuilder()
            .text("*")
            .number("(dd)/(dd)/(dddd),")         // date (ddmmyyyy)
            .number("(dd):(dd):(dd),")           // time (hhmmss)
            .number("(d+),")                     // imei
            .expression("(.+),")                 //VendorId
            .expression("(.+),")                 //Vehicle registration
            .number("(d+.d+),")                  // speed
            .number("(d+.d+),")                  // latitude
            .expression("([NS]),")               //direction
            .number("(d+.d+),")                  // longitude
            .expression("([EW]),")               //Direction
            .number("([01]),")                  //ignition
            .number("([01]),")                  //signal wire
            .number("([01]),")                  //connector
            .number("([01]),")                  //enclosure
            .expression("(.+),")                // speed source
            .number("(d+.d+),")                 // voltage
            .number("([01]),")                  //Callibration
            .number("(d+),")                  // SpeedLimit
            .number("(d+),")                    // GpsSpeed
            .number("([01])")                   // GpsFix
            .text(",*")
            .compile();
    private static final Pattern PATTERN_NEXT = new PatternBuilder()
            .number("(dd)/(dd)/(dddd),")         // date (ddmmyyyy)
            .number("(dd):(dd):(dd),")           // time (hhmmss)
            .number("(d+),")                     // imei
            .expression("(.+),")                 //VendorId
            .expression("(.+),")                 //Vehicle registration
            .number("(d+.d+),")                  // speed
            .number("([+-]d+.d+)")                  // latitude
            .expression("([EW]),")               //direction
            .number("([+-]d+.d+)")                  // longitude
            .expression("([NS]),")               //Direction
            .number("([01]),")                  //ignition
            .number("([01])")                  //Power status
            .any()
            .compile();


    @Override
    protected Object decode(Channel channel, SocketAddress remoteAddress, Object sentence) throws Exception {

        String data = (String) sentence;
        data = data.replace("??", "");
        Pattern pattern = PATTERN_NEXT;
        Parser parser = new Parser(pattern, data);
        if (!parser.matches()) {
            return null;
        }
        Position position = new Position(getProtocolName());
        position.setTime(parser.nextDateTime(Parser.DateTimeFormat.YMD_HMS));
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }

        position.setDeviceId(deviceSession.getDeviceId());
        position.set(Position.KEY_VENDORID, parser.next());
        position.set(Position.KEY_VEHICLE_REGISTRATION, parser.next());
        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextInt()));
        position.setLatitude(parser.nextDouble());
        position.setLongitude(parser.nextDouble());
        position.set(Position.KEY_IGNITION, parser.next());
        position.set(Position.KEY_STATUS, parser.next());
        return position;

    }

}



