package rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;

/**
 * Abstract class to receive messages from RabbitMQ
 */
public abstract class Consumer implements QueueManager {
    private Connection connection;
    private Channel channel;

    static {
        factory.setHost(SERVER);
    }

    /**
     * receive a message form server
     *
     * @throws IOException
     */
    public abstract void receive() throws IOException;

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