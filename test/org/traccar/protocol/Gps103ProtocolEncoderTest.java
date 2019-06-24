package org.traccar.protocol;

import org.junit.Test;
import org.traccar.ProtocolTest;
import org.traccar.model.Command;

import static org.junit.Assert.assertEquals;

public class Gps103ProtocolEncoderTest extends ProtocolTest {

    @Test
    public void testEncodePositionPeriodic() throws Exception {

        Gps103ProtocolEncoder encoder = new Gps103ProtocolEncoder();
        
        Command command = new Command();
        command.setDeviceId(1);
        command.setType(Command.TYPE_POSITION_PERIODIC);
        command.set(Command.KEY_FREQUENCY, 300);
        
        assertEquals("**,imei:123456789012345,C,05m", encoder.encodeCommand(command));

    }

    @Test
    public void testEncodeCustom() throws Exception {

        Gps103ProtocolEncoder encoder = new Gps103ProtocolEncoder();

        Command command = new Command();
        command.setDeviceId(1);
        command.setType(Command.TYPE_CUSTOM);
        command.set(Command.KEY_DATA, "H,080");

        assertEquals("**,imei:123456789012345,H,080", encoder.encodeCommand(command));

    }
    @Test
    public void testEncodeCustomArm() throws Exception {

        Gps103ProtocolEncoder encoder = new Gps103ProtocolEncoder();

        Command command = new Command();
        command.setDeviceId(1);
        command.setType(Command.TYPE_CUSTOM);
        command.set(Command.KEY_DATA, "L");

        assertEquals("**,imei:123456789012345,112", encoder.encodeCommand(command));

    }


}
