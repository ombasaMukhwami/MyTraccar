package org.traccar.protocol;

import io.netty.handler.codec.string.StringEncoder;
import org.traccar.BaseProtocol;
import org.traccar.PipelineBuilder;
import org.traccar.TrackerServer;

public class BslProtocol  extends BaseProtocol {

    public BslProtocol() {
        addServer(new TrackerServer(false, getName()) {
            @Override
            protected void addProtocolHandlers(PipelineBuilder pipeline) {
                pipeline.addLast(new SaboFrameDecoder());
                pipeline.addLast(new StringEncoder());
                pipeline.addLast(new SaboProtocolDecoder(BslProtocol.this));
            }
        });
        addServer(new TrackerServer(true, getName()) {
            @Override
            protected void addProtocolHandlers(PipelineBuilder pipeline) {
                pipeline.addLast(new SaboFrameDecoder());
                pipeline.addLast(new StringEncoder());
                pipeline.addLast(new SaboProtocolDecoder(BslProtocol.this));
            }
        });
    }

}

