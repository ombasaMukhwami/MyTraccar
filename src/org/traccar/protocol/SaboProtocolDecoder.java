
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

public class SaboProtocolDecoder extends BaseProtocolDecoder {
    private ByteBuf photo;

    public SaboProtocolDecoder(Protocol protocol) {

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
    protected Object decode(Channel channel, SocketAddress remoteAddress, Object sentence) throws Exception {

        String data = (String) sentence;
        Pattern pattern = PATTERN;
        Parser parser = new Parser(pattern, data);
        if (!parser.matches()) {
            return null;
        }

        return null ;

    }

}


