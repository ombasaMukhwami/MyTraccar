package org.traccar.protocol;

import org.traccar.StringProtocolEncoder;
import org.traccar.model.Command;

public class ItsProtocolEncoder extends StringProtocolEncoder {

    @Override
    protected Object encodeCommand(Command command) {

        switch (command.getType()) {
            case Command.TYPE_ENGINE_STOP:
                return "@SET#RLP,OP1,";
            case Command.TYPE_ENGINE_RESUME:
                return "@CLR#RLP,OP1,";
            default:
                return null;
        }
    }

}
