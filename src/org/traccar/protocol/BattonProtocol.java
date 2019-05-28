package org.traccar.protocol;

import org.traccar.BaseProtocol;
import org.traccar.PipelineBuilder;
import org.traccar.TrackerServer;
import org.traccar.model.Command;

import io.netty.handler.codec.string.StringEncoder;

public class BattonProtocol extends BaseProtocol {

    public BattonProtocol() {
        setSupportedDataCommands(
                Command.TYPE_POSITION_SINGLE,
                Command.TYPE_ENGINE_STOP,
                Command.TYPE_ENGINE_RESUME,
                Command.TYPE_IDENTIFICATION,
                Command.TYPE_REBOOT_DEVICE);
        addServer(new TrackerServer(false, getName()) {
            @Override
            protected void addProtocolHandlers(PipelineBuilder pipeline) {
                pipeline.addLast(new Huabao1FrameDecoder());
                pipeline.addLast(new StringEncoder());
                pipeline.addLast(new HuabaoProtocolEncoder());
                pipeline.addLast(new Huabao1ProtocolDecoder(BattonProtocol.this));
            }
        });
        addServer(new TrackerServer(true, getName()) {
            @Override
            protected void addProtocolHandlers(PipelineBuilder pipeline) {
                pipeline.addLast(new Huabao1FrameDecoder());
                pipeline.addLast(new StringEncoder());
                pipeline.addLast(new HuabaoProtocolEncoder());
                pipeline.addLast(new Huabao1ProtocolDecoder(BattonProtocol.this));
            }
        });
    }

}

