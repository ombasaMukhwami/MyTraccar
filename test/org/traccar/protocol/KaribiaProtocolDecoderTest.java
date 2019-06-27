package org.traccar.protocol;

import org.junit.Test;
import org.traccar.ProtocolTest;
public class KaribiaProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        Huabao1TextProtocolDecoder decoder = new Huabao1TextProtocolDecoder(null);

        verifyPosition(decoder, buffer(
                "12/23/18,13:00:00,990000862471854,GV4,KAA001A,065,1.23456,N,36.12345,E,0,0,272,1,0#"));

        verifyPosition(decoder, buffer(
                "06/26/19,12:53:44,860298040114828,GV4,CHSZ-0001,0,1.284483,S,36.821250,E,0,1,0,0,0#"));
    }

}
