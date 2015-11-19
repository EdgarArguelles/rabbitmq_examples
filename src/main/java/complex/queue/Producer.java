package complex.queue;

/**
 * Send messages to Queue server
 */
public interface Producer {

    /**
     * Send a message to server
     *
     * @param message         message to queue to server
     * @param routingKey      key to route message
     * @param originalMessage original message which contains replayTo, correlationId, etc
     * @throws Exception
     */
    void send(String message, String routingKey, Message originalMessage) throws Exception;

    /**
     * Close connection to server
     *
     * @throws Exception
     */
    void closeConection() throws Exception;
}