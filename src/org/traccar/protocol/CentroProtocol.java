package org.traccar.protocol;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.traccar.BaseProtocol;
import org.traccar.PipelineBuilder;
import org.traccar.TrackerServer;


public class CentroProtocol extends BaseProtocol {

    public CentroProtocol() {
        addServer(new TrackerServer(false, getName()) {
            @Override
            protected void addProtocolHandlers(PipelineBuilder pipeline) {
                pipeline.addLast(new LengthFieldBasedFrameDecoder(1024, 2, 1, 1, 0));
                pipeline.addLast(new SaboTextProtocolDecoder(CentroProtocol.this));
            }
        });
        addServer(new TrackerServer(true, getName()) {
            @Override
            protected void addProtocolHandlers(PipelineBuilder pipeline) {
                pipeline.addLast(new LengthFieldBasedFrameDecoder(1024, 2, 1, 1, 0));
                pipeline.addLast(new SaboTextProtocolDecoder(CentroProtocol.this));
            }
        });
    }
}
