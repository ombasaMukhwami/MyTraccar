package org.traccar.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.traccar.BaseProtocol;
import org.traccar.PipelineBuilder;
import org.traccar.TrackerServer;

import java.nio.ByteOrder;

public class Minifinder2Protocol extends BaseProtocol {

    public Minifinder2Protocol() {
        addServer(new TrackerServer(false, getName()) {
            @Override
            protected void addProtocolHandlers(PipelineBuilder pipeline) {
                pipeline.addLast(new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, 1024, 2, 2, 4, 0, true));
                pipeline.addLast(new Minifinder2ProtocolDecoder(Minifinder2Protocol.this));
            }
        });
    }

}
