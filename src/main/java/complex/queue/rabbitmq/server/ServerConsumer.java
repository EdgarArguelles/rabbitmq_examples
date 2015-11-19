package complex.queue.rabbitmq.server;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import complex.queue.Consumer;
import complex.queue.Message;
import complex.queue.rabbitmq.RabbitMQ;

/**
 * Listen to specific queue for any request
 */
public class ServerConsumer extends RabbitMQ implements Consumer {

    public static final String RPC_QUEUE_NAME = "rpc_action1";
    public static final String[] RPC_ROUTING_KEYS = new String[]{"action", "action1"};

    private QueueingConsumer queueConsumer;

    @Override
    public void startListening() throws Exception {
        // connect to server, create and/or get channel
        Channel channel = getChannel();

        // connect to exchange
        channel.exchangeDeclare(RPC_EXCHANGE_NAME, RPC_EXCHANGE_TYPE);

        // Server will listen to specific action queue for any client request
        // the second parameter (true) is named "durable", and tells server to don't lost the queue even if
        // server crash or restart
        channel.queueDeclare(RPC_QUEUE_NAME, true, false, false, null);

        // Now we need to tell the exchange to send messages to our queue. A binding is a relationship between
        // an exchange and a queue. This can be simply read as: the queue is interested in messages from this exchange.
        for (String key : RPC_ROUTING_KEYS) {
            channel.queueBind(RPC_QUEUE_NAME, RPC_EXCHANGE_NAME, key);
        }

        // usually server send all message to a Consumer, basicQos(1) tells RabbitMQ not to give more than one message
        // to a worker at a time. Or, in other words, don't dispatch a new message to a worker until it has processed
        // and acknowledged the previous one. Instead, it will dispatch it to the next worker that is not still busy.
        channel.basicQos(1);

        // server will stay waiting any client request using QueueingConsumer class to handle callbacks
        queueConsumer = new QueueingConsumer(channel);

        // the second parameter "false" called "autoAck", tells server should expect explicit acknowledgements (channel.basicAck)
        // that server will delete message from queue until an explicit acknowledgement arrives
        channel.basicConsume(RPC_QUEUE_NAME, false, queueConsumer);
    }

    @Override
    public Message nextMessage() throws Exception {
        // sleep process flow until request arrives
        QueueingConsumer.Delivery delivery = queueConsumer.nextDelivery();

        // get request BasicProperties
        AMQP.BasicProperties props = delivery.getProperties();

        return new Message(props.getCorrelationId(), props.getReplyTo(), delivery.getEnvelope().getDeliveryTag(), delivery.getBody());
    }

    @Override
    public void notifyAcknowledgement(long deliveryTag) throws Exception {
        // connect to server, create and/or get channel
        Channel channel = getChannel();
        channel.basicAck(deliveryTag, false);
    }
}