package complex.queue.rabbitmq.client;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import complex.queue.Message;
import complex.queue.Producer;
import complex.queue.rabbitmq.RabbitMQ;

public class ClientProducer extends RabbitMQ implements Producer {

    @Override
    public void send(String message, String routingKey, Message originalMessage) throws Exception {
        // connect to server, create and/or get channel
        Channel channel = getChannel();

        // connect to exchange
        channel.exchangeDeclare(RPC_EXCHANGE_NAME, RPC_EXCHANGE_TYPE);

        // create request BasicProperties
        AMQP.BasicProperties props = MessageProperties.PERSISTENT_TEXT_PLAIN;
        props.setCorrelationId(originalMessage.getCorrelationId());
        props.setReplyTo(originalMessage.getReplyTo());

        // publish client request to specific queue according with routingKey, in order to execute a specific
        // action or work on server side, sending an unique correlationId and a replayTo queue
        channel.basicPublish(RPC_EXCHANGE_NAME, routingKey, props, message.getBytes("UTF-8"));
    }
}