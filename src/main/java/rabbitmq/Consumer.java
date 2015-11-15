package rabbitmq;

/**
 * Abstract class to receive messages from RabbitMQ
 */
public abstract class Consumer extends QueueManager {

    /**
     * receive a message form server
     *
     * @throws Exception
     */
    public abstract void receive() throws Exception;
}