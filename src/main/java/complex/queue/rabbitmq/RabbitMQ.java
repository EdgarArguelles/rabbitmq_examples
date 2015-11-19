package complex.queue.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Abstract class to allow to connect to RabbitMQ
 */
public abstract class RabbitMQ {

    private static final ConnectionFactory factory;
    private Connection connection;
    private Channel channel;

    public static final String RPC_EXCHANGE_NAME = "rpc_direct";
    public static final String RPC_EXCHANGE_TYPE = "direct";

    static {
        factory = new ConnectionFactory();
        factory.setHost("localhost");
    }

    /**
     * Create and get a communication channel to server
     *
     * @return communication channel to server
     * @throws Exception
     */
    public Channel getChannel() throws Exception {
        if (connection == null || !connection.isOpen()) {
            connection = factory.newConnection();
        }
        if (channel == null || !channel.isOpen()) {
            channel = connection.createChannel();
        }
        return channel;
    }

    /**
     * Close connection to server
     *
     * @throws Exception
     */
    public void closeConection() throws Exception {
        if (channel != null && channel.isOpen()) {
            channel.close();
        }
        if (connection != null && connection.isOpen()) {
            connection.close();
        }
    }
}