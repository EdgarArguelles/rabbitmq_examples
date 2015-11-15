package rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * Abstract class to send messages to RabbitMQ
 */
public abstract class Producer implements QueueManager {
    private Connection connection;
    private Channel channel;

    static {
        factory.setHost(SERVER);
    }

    /**
     * Send a message to server
     *
     * @param message    message to queue to server
     * @param routingKey key to route message
     * @throws Exception
     */
    public abstract void send(String message, String routingKey) throws Exception;

    @Override
    public Channel openChannel() throws Exception {
        connection = factory.newConnection();
        channel = connection.createChannel();
        return channel;
    }

    @Override
    public void closeChannel() throws Exception {
        channel.close();
        connection.close();
    }
}