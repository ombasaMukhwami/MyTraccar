package org.traccar.protocol;

import org.traccar.BaseProtocol;
import org.traccar.PipelineBuilder;
import org.traccar.TrackerServer;
import org.traccar.model.Command;

public class EkasProtocol extends BaseProtocol {

    public EkasProtocol() {
        setSupportedDataCommands(
                Command.TYPE_OUTPUT_CONTROL);
        addServer(new TrackerServer(false, getName()) {
            @Override
            protected void addProtocolHandlers(PipelineBuilder pipeline) {
                pipeline.addLast(new CellocatorFrameDecoder());
                pipeline.addLast(new CellocatorProtocolEncoder());
                pipeline.addLast(new CellocatorProtocolDecoder(EkasProtocol.this));
            }
        });
        addServer(new TrackerServer(true, getName()) {
            @Override
            protected void addProtocolHandlers(PipelineBuilder pipeline) {
                pipeline.addLast(new CellocatorProtocolEncoder());
                pipeline.addLast(new CellocatorProtocolDecoder(EkasProtocol.this));
            }
        });
    }

}
