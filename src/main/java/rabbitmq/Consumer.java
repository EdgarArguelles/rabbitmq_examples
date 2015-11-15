package rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

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
     * @throws Exception
     */
    public abstract void receive() throws Exception;

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