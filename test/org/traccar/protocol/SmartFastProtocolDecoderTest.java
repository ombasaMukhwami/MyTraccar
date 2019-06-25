
package org.traccar.protocol;

import org.junit.Test;
import org.traccar.ProtocolTest;
public class SmartFastProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        SmartFastProtocolDecoder decoder = new SmartFastProtocolDecoder(null);

        verifyPosition(decoder, text(
                "19/03/27,12:20:45,869586033445501,Smartfast,ERROR,000,36.873917 E,1.232867 S,0,0"));
        verifyPosition(decoder, text(
                "19/05/30,07:15:10,860286047096320,,KBW 256P,000,36.880051 E,-1.218650 S,0,0"));
        verifyPosition(decoder, text(
                "19/06/25,12:11:05,869586033445501,Smartfast,KAZ 199Q,000,36.874584 E,1.233933 S,0,0\n"));
    }

}
