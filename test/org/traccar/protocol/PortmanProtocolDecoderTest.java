package org.traccar.protocol;

import org.junit.Test;
import org.traccar.ProtocolTest;

public class PortmanProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        PortmanProtocolDecoder decoder = new PortmanProtocolDecoder(null);


        verifyPosition(decoder, text(
                "%%100000000008570,A,06,190423085400,S3209.1963E11555.3359,068,079,NA,0E02C830,110,GNA,CFG:0.91|"));

  }

}