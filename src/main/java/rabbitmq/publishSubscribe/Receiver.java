package rabbitmq.publishSubscribe;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import rabbitmq.Consumer;

import java.io.IOException;

/**
 * All consumers will receive logs emitted by Producer, in this case the messages won't be splitted among Consumers.
 */
public class Receiver extends Consumer {

    public static void main(String[] args) {
        try {
            Consumer consumer = new Receiver();
            consumer.receive();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receive() throws Exception {
        // connect to server, create and/or get channel
        final Channel channel = openChannel();

        // connect to exchange
        channel.exchangeDeclare(EXCHANGE_LOGS, "fanout");
        // because we want to hear about all log messages, not just a subset of them. We're also interested only
        // in currently flowing messages not in the old ones, for this we need to connect Rabbit with a fresh empty
        // queue, for this each consumer will create its own temporal queue with a random name and when we disconnect
        // the consumer the queue should be automatically deleted.
        // so we're going to use channel.queueDeclare().getQueue() to create a non-durable, exclusive, auto delete
        // queue with a generated name
        String queueName = channel.queueDeclare().getQueue();

        // Now we need to tell the exchange to send messages to our queue. That relationship between exchange and
        // a queue is called a binding.
        channel.queueBind(queueName, EXCHANGE_LOGS, "");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // because each consumer has its own queue we don't need to configure basicQos(1) to share the message one
        // by one among consumers

        // DefaultConsumer is a class implementing the Consumer interface we'll use to buffer
        // the messages pushed to us by the server.
        com.rabbitmq.client.Consumer consumer = new DefaultConsumer(channel) {
            // We're about to tell the server to deliver us the messages from the queue. Since it will push us messages
            // asynchronously, we provide a callback in the form of an object that will buffer the messages until we're
            // ready to use them. That is what a DefaultConsumer subclass does.
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException _ignored) {
                    Thread.currentThread().interrupt();
                } finally {
                    System.out.println(" [x] Done");
                }
            }
        };

        // the second parameter "true" called "autoAck", tells server should consider messages acknowledged once delivered
        // that means once the message is delivered, the server will delete message from queue
        channel.basicConsume(queueName, true, consumer);
    }
}