package rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Abstract class to allow to connect to RabbitMQ
 */
public abstract class QueueManager {
    private static final ConnectionFactory factory;
    private static Connection connection;
    private static Channel channel;

    // Queues name
    public static final String QUEUE_HELLO_WORLD = "helloWorld";
    public static final String TASK_QUEUE_NAME = "task_queue";
    public static final String EXCHANGE_LOGS = "logs";
    public static final String EXCHANGE_DIRECT_LOGS = "direct_logs";
    public static final String EXCHANGE_TOPIC_LOGS = "topic_logs";
    public static final String RPC_QUEUE_NAME = "rpc_queue";

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
    public Channel openChannel() throws Exception {
        if (channel == null || !channel.isOpen()) {
            connection = factory.newConnection();
            channel = connection.createChannel();
        }
        return channel;
    }

    /**
     * Close connection to server
     *
     * @throws Exception
     */
    public void closeChannel() throws Exception {
        channel.close();
        connection.close();
    }
}