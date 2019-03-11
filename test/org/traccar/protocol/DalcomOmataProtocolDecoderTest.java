package org.traccar.protocol;

import org.junit.Test;
import org.traccar.ProtocolTest;
public class DalcomOmataProtocolDecoderTest extends ProtocolTest{

    @Test
    public void testDecode() throws Exception {

        DalcomOmataProtocolDecoder decoder = new DalcomOmataProtocolDecoder(null);

        verifyPosition(decoder, text(
                "*05/03/2019,12:09:05,865733028216463,GV3,KBY525Q,26.36,0000.11684,N,03743.52571,E,0,0,0,0,G,24.4,1,80,80,0,*"));

        verifyPosition(decoder, text(
                "*06/03/2019,13:40:20,865733028211852,GV3,M800066,0.000,0,0,0,0,1,0,0,0,0,0.00,1,80,80,1,*"));

    }

}
