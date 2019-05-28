package org.traccar.protocol;


import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import org.traccar.BaseProtocol;
import org.traccar.CharacterDelimiterFrameDecoder;
import org.traccar.PipelineBuilder;
import org.traccar.TrackerServer;
import org.traccar.model.Command;

public class Wialon1Protocol extends BaseProtocol {
    public Wialon1Protocol() {
        setSupportedDataCommands(
                Command.TYPE_REBOOT_DEVICE,
                Command.TYPE_SEND_USSD,
                Command.TYPE_IDENTIFICATION,
                Command.TYPE_OUTPUT_CONTROL);
        addServer(new TrackerServer(false, getName()) {
            @Override
            protected void addProtocolHandlers(PipelineBuilder pipeline) {
                pipeline.addLast(new CharacterDelimiterFrameDecoder(1024,  false,  "\r\n", "#"));
                pipeline.addLast(new StringEncoder());
                pipeline.addLast(new StringDecoder());
                pipeline.addLast(new WialonProtocolEncoder());
                pipeline.addLast(new WialonProtocolDecoder(Wialon1Protocol.this));
            }
        });
    }
}
