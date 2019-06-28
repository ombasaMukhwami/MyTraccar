package org.traccar.protocol;

import org.junit.Test;
import org.traccar.ProtocolTest;

public class Tr11ProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        Tr11ProtocolDecoder decoder = new Tr11ProtocolDecoder(null);

        verifyPosition(decoder, binary(
                "292980003719D6C605190328123031037548310234237600000149F81598A07FFC5D00001E0000000000000005000100000000060088011598A00D0D"));

        verifyPosition(decoder, binary(
                "29298e003623cbbe381906280915290373106002416451000003047a0a6d587fff0000aa550000000000ffffffff01010000000000000000009f0d"));

        verifyPosition(decoder, binary(
                "292980004023cbbe38190628091716037312010241620100150307fa0a6e737fff0000aa55060d000000ffffffff030100ca000a2954279015295453960529544e6e05a20d"));

        verifyPosition(decoder, binary(
                "29298e003623cbbe38190628071015037335260241994200000000fa0a4d4d7fff0000aa550809000000ffffffff010100ca000a295453960be50d"));

        verifyPosition(decoder, binary(
                "29298e003623cbbe38190628071045037335260241994500000000fa0a4d4d7fff0000aa550809000000ffffffff010100ca000a295453960bb20d"));

        verifyPosition(decoder, binary(
                "29298e003623cbbe38190628071145037335250241994400000000fa0a4d4d7fff0000aa550809000000ffffffff010100ca000a295453960bb10d"));

        verifyPosition(decoder, binary(
                "29298e003623cbbe38190628071215037335230241994300000000fa0a4d4d7fff0000aa550809000000ffffffff010100ca000a295453960be30d"));

        verifyPosition(decoder, binary(
                "29298e003623cbbe38190628071445037335260241994100000000fa0a4d4d7fff0000aa550809000000ffffffff010100ca000a295453960bb20d"));
        verifyPosition(decoder, binary(
                "29298e003623cbbe38190628071515037335270241994200000000fa0a4d4d7fff0000aa550809000000ffffffff010100ca000a295453960be10d"));

        verifyPosition(decoder, binary(
                "29298e003623cbbe38190628071615037335220241994300000000fa0a4d4d7fff0000aa550809000000ffffffff010100ca000a295453960be60d"));

        verifyPosition(decoder, binary(
                "29298e004023cbbe38190628091846037313260241599400140308fa0a6fff7fff0000aa55070d000000ffffffff030100ca000a2954279015295453960529544e6e05f90d"));

        verifyPosition(decoder, binary(
                "29298e003623cbbe38190628071715037335230241994100000000fa0a4d4d7fff0000aa550809000000ffffffff010100ca000a295453960be40d"));

        verifyPosition(decoder, binary(
                "29298e003623cbbe38190628071745037335220241994200000000fa0a4d4d7fff0000aa550809000000ffffffff010100ca000a295453960bb60d"));

        verifyPosition(decoder, binary(
                "29298e003623cbbe38190628071815037335230241994200000000fa0a4d4d7fff0000aa550809000000ffffffff010100ca000a295453960be80d"));

        verifyPosition(decoder, binary(
                "29298e003623cbbe38190628071915037335220241994200000000fa0a4d4d7fff0000aa550809000000ffffffff010100ca000a295453960be80d"));

        verifyPosition(decoder, binary(
                "29298e003623cbbe38190628071945037335240241994100000000fa0a4d4d7fff0000aa550809000000ffffffff010100ca000a295453960bbd0d"));

        verifyPosition(decoder, binary(
                "29298e003623cbbe38190628072015037335230241994100000000fa0a4d4d7fff0000aa550809000000ffffffff010100ca000a295453960bd30d"));

        verifyPosition(decoder, binary(
                "29298e004023cbbe38190628091946037314120241585700140304fa0a710b7fff0000aa55070d000000ffffffff030100ca000a2954279015295453960529544e6e05ef0d"));

        verifyPosition(decoder, binary(
                "292980004023cbbe38190628092046037314990241572800150313fa0a720f7fff0000aa55070d000000ffffffff030100ca000a2954279015295453960529544e6e05320d"));

        verifyPosition(decoder, binary(
                "29298e003b23cbbe38190628072503037335220241992900000000fa0a4d4d7fff0000aa55080a000000ffffffff020100ca000a2954771803295453960ab40d"));

    }

}
