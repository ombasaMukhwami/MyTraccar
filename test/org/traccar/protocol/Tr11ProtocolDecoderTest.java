package org.traccar.protocol;

import org.junit.Test;
import org.traccar.ProtocolTest;

public class Tr11ProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        Tr11ProtocolDecoder decoder = new Tr11ProtocolDecoder(null);

        verifyPosition(decoder, binary(
                "292980003719D6C605190328123031037548310234237600000149F81598A07FFC5D00001E0000000000000005000100000000060088011598A00D0D"));

    }

}
