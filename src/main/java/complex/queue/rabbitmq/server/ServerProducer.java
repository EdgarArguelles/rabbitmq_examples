package complex.queue.rabbitmq.server;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import complex.queue.Message;
import complex.queue.Producer;
import complex.queue.rabbitmq.RabbitMQ;

/**
 * Send a generic response for any request to specific replayTo queue
 */
public class ServerProducer extends RabbitMQ implements Producer {

    @Override
    public void send(String message, String routingKey, Message originalMessage) throws Exception {
        // connect to server, create and/or get channel
        Channel channel = getChannel();

        // create a replay BasicProperties to send same correlationId than request message
        AMQP.BasicProperties replyProps = new AMQP.BasicProperties();
        replyProps.setCorrelationId(originalMessage.getCorrelationId());

        // publish server response to replayTo queue using the same correlationId than request message
        channel.basicPublish("", originalMessage.getReplyTo(), replyProps, message.getBytes("UTF-8"));
    }
}