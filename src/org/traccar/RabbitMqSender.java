package org.traccar;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

public class RabbitMqSender {
    private  String HOST_NAME;
    private  String USER_NAME;
    private  String PASSWORD;
    private  String QUEUE_NAME;
    private  String EXCHANGE_NAME;
    private  String ROUTING_KEY;
    private  boolean DURABLE = false;
    public RabbitMqSender() {
        HOST_NAME = Context.getConfig().getString("rabbitMq.hostname");
        USER_NAME = Context.getConfig().getString("rabbitMq.username");
        PASSWORD = Context.getConfig().getString("rabbitMq.password");
        QUEUE_NAME = Context.getConfig().getString("rabbitMq.queue");
        EXCHANGE_NAME = Context.getConfig().getString("rabbitMq.exchange");
        ROUTING_KEY = Context.getConfig().getString("rabbitMq.routingKey");
        DURABLE = Context.getConfig().getBoolean("rabbitMq.durable");
    }
    public  boolean sendMessage(String message) {
        try {

            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(HOST_NAME);
            factory.setPassword(PASSWORD);
            factory.setUsername(USER_NAME);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "topic", DURABLE);
            channel.queueDeclare(QUEUE_NAME, DURABLE, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);
            channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, message.getBytes());
            channel.close();
            connection.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
