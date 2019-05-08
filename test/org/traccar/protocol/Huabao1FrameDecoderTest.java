package org.traccar.protocol;

import org.junit.Test;
import org.traccar.ProtocolTest;

import static org.junit.Assert.assertEquals;

public class Huabao1FrameDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        Huabao1FrameDecoder decoder = new Huabao1FrameDecoder();

        assertEquals(
                binary("7e0200002c001401709034080d000000000004000700139d440232082406880000000019050803133301040015d0e8030200002504000000008e7e"),
                decoder.decode(null, null, binary("7e0200002c001401709034080d000000000004000700139d440232082406880000000019050803133301040015d0e8030200002504000000008e7e")));

    }

}
