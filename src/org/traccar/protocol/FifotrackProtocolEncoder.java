package org.traccar.protocol;

import org.traccar.StringProtocolEncoder;
import org.traccar.helper.Checksum;
import org.traccar.model.Command;

public class FifotrackProtocolEncoder extends StringProtocolEncoder {

    private Object formatCommand(Command command, String content) {
        String uniqueId = getUniqueId(command.getDeviceId());
        int length = 1 + uniqueId.length() + 3 + content.length();
        String result = String.format("##%02d,%s,1,%s*", length, uniqueId, content);
        result += Checksum.sum(result) + "\r\n";
        return result;
    }

    @Override
    protected Object encodeCommand(Command command) {

        switch (command.getType()) {
            case Command.TYPE_CUSTOM:
                return formatCommand(command, command.getString(Command.KEY_DATA));
            case Command.TYPE_REQUEST_PHOTO:
                return formatCommand(command, "D05,3");
            default:
                return null;
        }
    }

}
