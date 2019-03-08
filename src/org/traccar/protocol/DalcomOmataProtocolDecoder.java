
package org.traccar.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.traccar.BaseProtocolDecoder;
import org.traccar.DeviceSession;
import org.traccar.Protocol;
import org.traccar.helper.Parser;
import org.traccar.helper.PatternBuilder;
import org.traccar.model.Position;


import java.net.SocketAddress;
import java.util.regex.Pattern;

public class DalcomOmataProtocolDecoder extends BaseProtocolDecoder {
    private ByteBuf photo;

    public DalcomOmataProtocolDecoder(Protocol protocol) {

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


    @Override
    protected Object decode(Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        String sentence = (String) msg;
        //sentence=sentence.replace("/","").replace(":","");
        Pattern pattern = PATTERN;
        Parser parser = new Parser(pattern, sentence);
        if (!parser.matches()) {
            return null;
        }
        Position position = new Position(getProtocolName());
        position.setTime(parser.nextDateTime(Parser.DateTimeFormat.DMY_HMS));
        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }
        String latDirection, lonDirection;
        double a, b, c, d;
        position.setDeviceId(deviceSession.getDeviceId());
        position.set(Position.KEY_VENDORID, parser.next());
        position.set(Position.KEY_VEHICLE_REGISTRATION, parser.next());
        position.setSpeed(parser.nextDouble());
        String lat = parser.next();
        a = Double.parseDouble(lat.substring(0, 2));
        b = (Double.parseDouble(lat.substring(2)) / 60.0);
        double iLat  =  a +  b;
        latDirection  =  parser.next();
        position.set(Position.KEY_LATITUTE_DIRECTION, latDirection);
        String lon = parser.next();
        c = Double.parseDouble(lon.substring(0, 3));
        d = (Double.parseDouble(lon.substring(3)) / 60.0);
        double iLon  =  c + d;
        lonDirection = parser.next();
        position.set(Position.KEY_LONGITUTE_DIRECTION, lonDirection);
        position.set(Position.KEY_IGNITION, parser.nextInt() > 0);
        position.set(Position.KEY_SIGNAL_WIRE, parser.next());
        position.set(Position.KEY_CONNECTOR, parser.next());
        position.set(Position.KEY_ENCLOSURE, parser.next());
        position.set(Position.KEY_SPEED_SOURCE, parser.next());
        position.set(Position.KEY_POWER, parser.nextDouble());
        position.set(Position.KEY_CALLIBRATION, parser.next());
        position.set(Position.KEY_SPEED_LIMIT, parser.next());
        position.set(Position.KEY_GPS_SPEED, parser.next());
        position.set(Position.KEY_GPSFIX, parser.next());
        if (latDirection == "S") {
            iLat  = -1 * iLat;
        }
        if (lonDirection ==  "W") {
            iLon  = -1 * iLon;
        }

        position.setLatitude(iLat);
        position.setLongitude(iLon);

        return position;

    }

}


