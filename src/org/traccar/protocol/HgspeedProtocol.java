package org.traccar.protocol;

import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.traccar.BaseProtocol;
import org.traccar.CharacterDelimiterFrameDecoder;
import org.traccar.PipelineBuilder;
import org.traccar.TrackerServer;
public class HgspeedProtocol extends BaseProtocol {

    public HgspeedProtocol() {
        addServer(new TrackerServer(false, getName()) {
            @Override
            protected void addProtocolHandlers(PipelineBuilder pipeline) {
                pipeline.addLast(new CharacterDelimiterFrameDecoder(1024,  false,  "\r\n"));
                pipeline.addLast(new StringEncoder());
                pipeline.addLast(new StringDecoder());
                pipeline.addLast(new SmartFastProtocolDecoder(HgspeedProtocol.this));
            }
        });
        addServer(new TrackerServer(true, getName()) {
            @Override
            protected void addProtocolHandlers(PipelineBuilder pipeline) {
                pipeline.addLast(new CharacterDelimiterFrameDecoder(1024, false,  "\r\n"));
                pipeline.addLast(new StringEncoder());
                pipeline.addLast(new StringDecoder());
                pipeline.addLast(new SmartFastProtocolDecoder(HgspeedProtocol.this));
            }
        });
    }

}
