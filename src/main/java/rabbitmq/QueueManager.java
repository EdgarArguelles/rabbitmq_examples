package rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;

/**
 * Allow to connect to RabbitMQ
 */
public interface QueueManager {
    String SERVER = "localhost";
    ConnectionFactory factory = new ConnectionFactory();

    // Queues name
    String QUEUE_HELLO_WORLD = "helloWorld";
    String TASK_QUEUE_NAME = "task_queue";
    String EXCHANGE_LOGS = "logs";
    String EXCHANGE_DIRECT_LOGS = "direct_logs";

    /**
     * Create and get a communication channel to server
     *
     * @return communication channel to server
     * @throws IOException
     */
    Channel openChannel() throws IOException;

    /**
     * Close connection to server
     *
     * @throws IOException
     */
    void closeChannel() throws IOException;
}