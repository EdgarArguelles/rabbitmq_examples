package rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;

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
     * @throws IOException
     */
    public abstract void send(String message, String routingKey) throws IOException;

    @Override
    public Channel openChannel() throws IOException {
        connection = factory.newConnection();
        channel = connection.createChannel();
        return channel;
    }

    @Override
    public void closeChannel() throws IOException {
        channel.close();
        connection.close();
    }
}