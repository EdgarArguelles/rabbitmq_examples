package rabbitmq;

/**
 * Abstract class to send messages to RabbitMQ
 */
public abstract class Producer extends QueueManager {

    /**
     * Send a message to server
     *
     * @param message    message to queue to server
     * @param routingKey key to route message
     * @throws Exception
     */
    public abstract void send(String message, String routingKey) throws Exception;
}