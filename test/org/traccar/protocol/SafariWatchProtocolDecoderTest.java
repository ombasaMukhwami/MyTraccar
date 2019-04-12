
package org.traccar.protocol;

import org.junit.Test;
import org.traccar.ProtocolTest;
public class SafariWatchProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        SafariWatchProtocolDecoder decoder = new SafariWatchProtocolDecoder(null);

        verifyPosition(decoder, text(
                "19/04/05,14:02:16,867959033026923,SAFARIWATCH,KHG 6573I,050.2,-0000.3880° S,000036.9685° E,1,0"));


    }

}
