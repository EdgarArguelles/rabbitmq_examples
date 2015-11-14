package rabbitmq.workQueues;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import rabbitmq.Consumer;

import java.io.IOException;

/**
 * Different instances of ReceiverInfo will share work while one is busy
 * we'll keep it running to listen for messages and print them out.
 */
public class Receiver extends Consumer {
    public static void main(String[] args) {
        try {
            Consumer consumer = new Receiver();
            consumer.receive();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receive() throws IOException {
        // connect to server, create and get channel
        final Channel channel = openChannel();

        // Note that we declare the queue here, as well. Because we might start the receiver before the sender,
        // we want to make sure the queue exists before we try to consume messages from it.
        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // usually server send all message to a Consumer, basicQos(1) tells RabbitMQ not to give more than one message
        // to a worker at a time. Or, in other words, don't dispatch a new message to a worker until it has processed
        // and acknowledged the previous one. Instead, it will dispatch it to the next worker that is not still busy.
        channel.basicQos(1);

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
                    doWork(message);
                } finally {
                    System.out.println(" [x] Done");
                    // tells server that this message is complete (explicit acknowledgement)
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };

        // the second parameter "false" called "autoAck", tells server should expect explicit acknowledgements (channel.basicAck)
        // that server will delete message from queue until an explicit acknowledgement arrives
        channel.basicConsume(TASK_QUEUE_NAME, false, consumer);
    }

    // create a delay for each "."
    private static void doWork(String task) {
        for (char ch : task.toCharArray()) {
            if (ch == '.') {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException _ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}