package org.traccar;

import org.traccar.model.Event;
import org.traccar.model.Position;

public class NavigateEvent {
    public NavigateEvent() {
    }
    private Event event;
    private Position position;
    public NavigateEvent(Event event, Position position) {
        this.event = event;
        this.position = position;
    }
}
