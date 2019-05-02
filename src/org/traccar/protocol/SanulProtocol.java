package org.traccar.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.traccar.BaseProtocol;
import org.traccar.PipelineBuilder;
import org.traccar.TrackerServer;

import java.nio.ByteOrder;

public class SanulProtocol extends BaseProtocol {

    public SanulProtocol() {
        addServer(new TrackerServer(false, getName()) {
            @Override
            protected void addProtocolHandlers(PipelineBuilder pipeline) {
                pipeline.addLast(new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, 1024, 3, 2, 0, 0, true));
                pipeline.addLast(new SanulProtocolDecoder(SanulProtocol.this));
            }
        });
    }

}
