package org.traccar.protocol;

import org.junit.Test;
import org.traccar.ProtocolTest;

public class Huabao1ProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        Huabao1TextProtocolDecoder decoder = new Huabao1TextProtocolDecoder(null);

        verifyPosition(decoder, buffer(
                "05/08/19,09:06:14,860675049532870,GV9,ABC123,0,36.791118,E,1.277329,S,0,1"));


    }

}
