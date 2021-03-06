package org.traccar.protocol;

import org.junit.Test;
import org.traccar.ProtocolTest;

public class PluginProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        PluginProtocolDecoder decoder = new PluginProtocolDecoder(null);

        verifyPosition(decoder, text(
                "$$STATUS801739,20190528143943,28.254086,-25.860665,0,0,0,-1,2,78,11395,0,0,0"));

        verifyPosition(decoder, text(
                "50000,20150623184513,113.828759,22.709578,70,190,0,-1,2,155135681,805327235,1.32,-32.1,0"));

    }

}
