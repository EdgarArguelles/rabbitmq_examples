package complex.queue;

import java.io.UnsupportedEncodingException;

/**
 * Queue Message POJO
 */
public class Message {

    private String correlationId;
    private String replyTo;
    private long deliveryTag;
    private byte[] body;

    /**
     * Constructor class
     *
     * @param correlationId unique id that identify request
     * @param replyTo       queue name to answer a response
     */
    public Message(String correlationId, String replyTo) {
        this.correlationId = correlationId;
        this.replyTo = replyTo;
    }

    /**
     * Constructor class
     *
     * @param correlationId unique id that identify request
     * @param replyTo       queue name to answer a response
     * @param deliveryTag   tag that identify current message
     * @param body          message body
     */
    public Message(String correlationId, String replyTo, long deliveryTag, byte[] body) {
        this.correlationId = correlationId;
        this.replyTo = replyTo;
        this.deliveryTag = deliveryTag;
        this.body = body;
    }

    /**
     * get unique id that identify request
     *
     * @return unique id that identify request
     */
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * set unique id that identify request
     *
     * @param correlationId unique id that identify request
     */
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    /**
     * get queue name to answer a response
     *
     * @return queue name to answer a response
     */
    public String getReplyTo() {
        return replyTo;
    }

    /**
     * set queue name to answer a response
     *
     * @param replyTo queue name to answer a response
     */
    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    /**
     * get tag that identify current message
     *
     * @return tag that identify current message
     */
    public long getDeliveryTag() {
        return deliveryTag;
    }

    /**
     * set tag that identify current message
     *
     * @param deliveryTag tag that identify current message
     */
    public void setDeliveryTag(long deliveryTag) {
        this.deliveryTag = deliveryTag;
    }

    /**
     * get message body
     *
     * @return message body
     */
    public byte[] getBody() {
        return body;
    }

    /**
     * set message body
     *
     * @param body message body
     */
    public void setBody(byte[] body) {
        this.body = body;
    }

    /**
     * get message body as String UTF-8
     *
     * @return String UTF-8 representation of message body
     */
    public String getBodyAsString() throws UnsupportedEncodingException {
        return new String(body, "UTF-8");
    }
}