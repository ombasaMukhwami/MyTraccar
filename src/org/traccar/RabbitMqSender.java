package org.traccar;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
public final class RabbitMqSender {
    private static final String HOST_NAME = "localhost";
    private static final String USER_NAME = "whitelabeltracking";
    private static final String PASSWORD =  "WltC0ntr0l";
    private static final String QUEUE_NAME = "UTS_MQ_NAVIGATE";
    private static final String EXCHANGE_NAME = "UTS_EXCHANGE_NAVIGATE";
    private static final String ROUTING_KEY = "traccar";
    private RabbitMqSender() {
        //not called
    }
    public static boolean sendMessage(String message) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(HOST_NAME);
            factory.setPassword(PASSWORD);
            factory.setUsername(USER_NAME);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "topic", true);
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
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
