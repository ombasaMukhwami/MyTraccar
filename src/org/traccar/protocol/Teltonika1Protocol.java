package org.traccar.protocol;
//Teltonika1Protocol
import org.traccar.BaseProtocol;
import org.traccar.PipelineBuilder;
import org.traccar.TrackerServer;
import org.traccar.model.Command;

public class Teltonika1Protocol extends BaseProtocol {

    public Teltonika1Protocol() {
        setSupportedDataCommands(
                Command.TYPE_CUSTOM);
        addServer(new TrackerServer(false, getName()) {
            @Override
            protected void addProtocolHandlers(PipelineBuilder pipeline) {
                pipeline.addLast(new TeltonikaFrameDecoder());
                pipeline.addLast(new TeltonikaProtocolEncoder());
                pipeline.addLast(new TeltonikaProtocolDecoder(Teltonika1Protocol.this, false));
            }
        });
        addServer(new TrackerServer(true, getName()) {
            @Override
            protected void addProtocolHandlers(PipelineBuilder pipeline) {
                pipeline.addLast(new TeltonikaProtocolEncoder());
                pipeline.addLast(new TeltonikaProtocolDecoder(Teltonika1Protocol.this, true));
            }
        });
    }

}
