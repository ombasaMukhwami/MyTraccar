/*
 * Copyright 2012 - 2018 Anton Tananaev (anton@traccar.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.protocol;

import org.traccar.BaseProtocolDecoder;
import org.traccar.Context;
import org.traccar.DeviceSession;
import org.traccar.NetworkMessage;
import org.traccar.Protocol;
import org.traccar.helper.BitUtil;
import org.traccar.helper.Parser;
import org.traccar.helper.PatternBuilder;
import org.traccar.helper.UnitsConverter;
import org.traccar.model.CellTower;
import org.traccar.model.Network;
import org.traccar.model.Position;
import org.traccar.model.WifiAccessPoint;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueclinkTextProtocolDecoder extends BaseProtocolDecoder {

    private boolean ignoreFixTime;
    private Gl200TextProtocolDecoder decoder;

    public QueclinkTextProtocolDecoder(Protocol protocol) {
        super(protocol);

        ignoreFixTime = Context.getConfig().getBoolean(getProtocolName() + ".ignoreFixTime");
        decoder = new Gl200TextProtocolDecoder(protocol);
    }


    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        String sentence = ((ByteBuf) msg).toString(StandardCharsets.US_ASCII);

        int typeIndex = sentence.indexOf(":GT");
        if (typeIndex < 0) {
            return null;
        }

        Object result;
        String type = sentence.substring(typeIndex + 3, typeIndex + 6);
        if (sentence.startsWith("+ACK")) {
            result = decoder.decodeAck(channel, remoteAddress, sentence, type);
        } else {
            switch (type) {
                case "INF":
                    result = decoder.decodeInf(channel, remoteAddress, sentence);
                    break;
                case "OBD":
                    result = decoder.decodeObd(channel, remoteAddress, sentence);
                    break;
                case "CAN":
                    result = decoder.decodeCan(channel, remoteAddress, sentence);
                    break;
                case "FRI":
                case "GEO":
                case "STR":
                    result = decoder.decodeFri(channel, remoteAddress, sentence);
                    break;
                case "ERI":
                    result = decoder.decodeEri(channel, remoteAddress, sentence);
                    break;
                case "IGN":
                case "IGF":
                    result = decoder.decodeIgn(channel, remoteAddress, sentence);
                    break;
                case "LSW":
                case "TSW":
                    result = decoder.decodeLsw(channel, remoteAddress, sentence);
                    break;
                case "IDA":
                    result = decoder.decodeIda(channel, remoteAddress, sentence);
                    break;
                case "WIF":
                    result = decoder.decodeWif(channel, remoteAddress, sentence);
                    break;
                case "GSM":
                    result = decoder.decodeGsm(channel, remoteAddress, sentence);
                    break;
                case "PDP":
                    result = decoder.decodePdp(channel, remoteAddress, sentence);
                    break;
                case "VER":
                    result = decoder.decodeVer(channel, remoteAddress, sentence);
                    break;
                default:
                    result = decoder.decodeOther(channel, remoteAddress, sentence, type);
                    break;
            }

            if (result == null) {
                result = decoder.decodeBasic(channel, remoteAddress, sentence, type);
            }

            if (result != null) {
                if (result instanceof Position) {
                    ((Position) result).set(Position.KEY_TYPE, type);
                } else {
                    for (Position p : (List<Position>) result) {
                        p.set(Position.KEY_TYPE, type);
                    }
                }
            }
        }

        return result;
    }

}
