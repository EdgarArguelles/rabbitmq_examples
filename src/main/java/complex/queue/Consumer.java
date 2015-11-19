package complex.queue;

/**
 * Receive messages from Queue server
 */
public interface Consumer {

    /**
     * start listening queue server
     *
     * @throws Exception
     */
    void startListening() throws Exception;

    /**
     * get next avaliable queue message
     *
     * @return Message object representing a queue message
     * @throws Exception
     * @see Message
     */
    Message nextMessage() throws Exception;

    /**
     * notify server that this message is complete (Acknowledgement)
     *
     * @param deliveryTag tag that identify message
     * @throws Exception
     */
    void notifyAcknowledgement(long deliveryTag) throws Exception;

    /**
     * Close connection to server
     *
     * @throws Exception
     */
    void closeConection() throws Exception;
}