package org.traccar.protocol;

import org.traccar.BaseProtocol;
import org.traccar.PipelineBuilder;
import org.traccar.TrackerServer;

public class BslProtocol  extends BaseProtocol {

    public BslProtocol() {
        addServer(new TrackerServer(false, getName()) {
            @Override
            protected void addProtocolHandlers(PipelineBuilder pipeline) {
                pipeline.addLast(new Tr11ProtocolDecoder(BslProtocol.this));
            }
        });
    }

}

