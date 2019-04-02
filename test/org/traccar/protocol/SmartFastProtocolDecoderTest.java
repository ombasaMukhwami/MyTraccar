
package org.traccar.protocol;

import org.junit.Test;
import org.traccar.ProtocolTest;
public class SmartFastProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        SmartFastProtocolDecoder decoder = new SmartFastProtocolDecoder(null);

        verifyPosition(decoder, text(
                "19/03/27,12:20:45,869586033445501,Smartfast,ERROR,000,36.873917 E,1.232867 S,0,0"));


    }

}
