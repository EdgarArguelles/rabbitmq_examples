package complex.queue.rabbitmq.client;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import complex.queue.Consumer;
import complex.queue.Message;
import complex.queue.rabbitmq.RabbitMQ;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ClientConsumer extends RabbitMQ implements Consumer {

    private String replyQueueName;
    private QueueingConsumer queueConsumer;

    @Override
    public void startListening() throws Exception {
        // connect to server, create and/or get channel
        Channel channel = getChannel();

        // create a non-durable, exclusive, auto delete queue with a generated name for this client
        replyQueueName = channel.queueDeclare().getQueue();

        // client will stay waiting any server response using QueueingConsumer class to handle callbacks
        queueConsumer = new QueueingConsumer(channel);

        // the second parameter "true" called "autoAck", tells server should consider messages acknowledged once delivered
        // that means once the message is delivered, the server will delete message from queue
        channel.basicConsume(replyQueueName, true, queueConsumer);
    }

    @Override
    public Message nextMessage() throws Exception {
        QueueingConsumer.Delivery delivery = queueConsumer.nextDelivery();

        // get request BasicProperties
        AMQP.BasicProperties props = delivery.getProperties();

        return new Message(props.getCorrelationId(), props.getReplyTo(), delivery.getEnvelope().getDeliveryTag(), delivery.getBody());
    }

    @Override
    public void notifyAcknowledgement(long deliveryTag) throws Exception {
        throw new NotImplementedException();
    }

    public String getReplyQueueName() throws Exception {
        if (replyQueueName == null) {
            startListening();
        }

        return replyQueueName;
    }
}