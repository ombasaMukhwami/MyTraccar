package org.traccar.protocol;

import org.traccar.StringProtocolEncoder;
import org.traccar.model.Command;

public class C2stekProtocolEncoder extends StringProtocolEncoder {

    @Override
    protected Object encodeCommand(Command command) {

        switch (command.getType()) {
            case Command.TYPE_CUSTOM:
                return formatCommand(command, "{%s}", Command.KEY_DATA);
            case Command.TYPE_ALARM_ARM:
                return formatCommand(command, "{%s}", Command.TYPE_ALARM_ARM);
            case Command.TYPE_ALARM_DISARM:
                return formatCommand(command, "{%s}", Command.TYPE_ALARM_DISARM);
            case Command.TYPE_REBOOT_DEVICE:
                return formatCommand(command, "{%s}", Command.TYPE_REBOOT_DEVICE);
            case Command.TYPE_SET_ODOMETER:
                return formatCommand(command, "{%s}", Command.TYPE_SET_ODOMETER);
            case Command.TYPE_ENGINE_STOP:
                return formatCommand(command, "{%s}", Command.TYPE_ENGINE_STOP);
            case Command.TYPE_ENGINE_RESUME:
                return formatCommand(command, "{%s}", Command.TYPE_ENGINE_RESUME);
            default:
                return null;
        }
    }
}
