package rabbitmq.helloWorld;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import rabbitmq.Consumer;

import java.io.IOException;

/**
 * The receiver is pushed messages from RabbitMQ, so unlike the sender which publishes a single message,
 * we'll keep it running to listen for messages and print them out.
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
        Channel channel = openChannel();

        // Note that we declare the queue here, as well. Because we might start the receiver before the sender,
        // we want to make sure the queue exists before we try to consume messages from it.
        channel.queueDeclare(QUEUE_HELLO_WORLD, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

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
        channel.basicConsume(QUEUE_HELLO_WORLD, true, consumer);
    }
}