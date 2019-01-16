package org.traccar.notification;

import java.util.Set;

import com.google.gson.Gson;
import org.traccar.NavigateEvent;
import org.traccar.RabbitMqSender;
import org.traccar.model.Event;
import org.traccar.model.Position;

import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Entity;

public class JsonTypeEventForwarder extends EventForwarder {

    @Override
    protected void executeRequest(Event event, Position position, Set<Long> users, AsyncInvoker invoker) {
        Gson gson = new Gson();
        NavigateEvent navigateEvent = new NavigateEvent(event, position);
         if (!RabbitMqSender.sendMessage(gson.toJson(navigateEvent))) {
             invoker.post(Entity.json(preparePayload(event, position, users)));
         }
    }

}
