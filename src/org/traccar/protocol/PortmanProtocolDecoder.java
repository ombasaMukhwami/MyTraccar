package org.traccar.protocol;


import io.netty.channel.Channel;
import org.traccar.BaseProtocolDecoder;
import org.traccar.DeviceSession;
import org.traccar.Protocol;
import org.traccar.helper.*;
import org.traccar.model.Position;



import java.net.SocketAddress;
import java.util.regex.Pattern;

public class PortmanProtocolDecoder extends BaseProtocolDecoder {

    public PortmanProtocolDecoder(Protocol protocol) {

        super(protocol);
    }

   // %%100000000007031,A,04,190417123428,S3155.3076E11552.9871,000,128,NA,4E000816,110,GNA,CFG:0.00|
    private static final Pattern PATTERN = new PatternBuilder()
            .text("%%")
            .expression("(.+),")          //imei           100000000007031
            .expression("(.+),")          // [GPS Valid]     A
            .number("(d+),")              // Sat       04
            .number("(dd)(dd)(dd)")       // date      190417
            .number("(dd)(dd)(dd),")      //time       123428
            .expression("([NS])")         //direction S
            .number("(dd)")               //lat        31
            .number("(d+.d+)")            //lat        55.3076
            .expression("([EW])")         //lon dir    E
            .number("(ddd)")              //lon        115
            .number("(d+.d+),")           //lon        52.9871
            .number("(d+),")              //speed      000
            .number("(d+),")              //course     128
            .expression("(.+),")          //           NA
            .expression("(.+),")          //status     4E000816
            .expression("(.+),")              //Event Id   110
            .expression("(.+),")          //           GNA
           // .text("CFG:")               //           CFG:
           // .number("(d+.d+)")            //           0.00
            .any()
            .compile();


    private Object decodeMessage(Parser parser, Position position, Channel channel, SocketAddress remoteAddress) {


        DeviceSession deviceSession = getDeviceSession(channel, remoteAddress, parser.next());
        if (deviceSession == null) {
            return null;
        }
        position.setDeviceId(deviceSession.getDeviceId());

        position.set(Position.KEY_VALID, parser.next());
        position.set(Position.KEY_SATELLITES, parser.nextInt(0));
        position.setTime(parser.nextDateTime(Parser.DateTimeFormat.YMD_HMS));
        String dirsn, direw;
        dirsn = parser.next();

        double latitute = parser.nextInt() + (parser.nextDouble() / 60.0);
        if (dirsn.equals("S")) {
            latitute = -1 * latitute;
        }
        position.setLatitude(latitute);
        direw = parser.next();
        double longitute = parser.nextInt() + (parser.nextDouble() / 60.0);
        if (direw.equals("W")) {
            longitute = -1 * longitute;
        }
        position.setLongitude(longitute);
        position.setSpeed(UnitsConverter.knotsFromKph(parser.nextInt(0)));
        position.setCourse(parser.nextInt(0));
        parser.next();
        int status = Integer.parseInt(parser.next(), 16);
        String deviceStatus = Helper.toBinary(status,  32);
        int ignition = Integer.parseInt(deviceStatus.substring(14, 15));
        position.set(Position.KEY_STATUS, status);
        position.set(Position.KEY_IGNITION, ignition > 0);
        position.set(Position.KEY_EVENT, parser.next());




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
