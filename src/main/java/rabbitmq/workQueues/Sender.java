package rabbitmq.workQueues;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import rabbitmq.Producer;

import java.io.IOException;

/**
 * The sender will connect to RabbitMQ, send multiples messages to be catch by different Consumers one by one when they
 * are not busy, then exit.
 */
public class Sender extends Producer {

    public static void main(String[] args) {
        try {
            Producer producer = new Sender();
            // each "." will delay a second, so the different consumers could share work while one is busy
            producer.send("13.............", null);
            producer.send("7.......", null);
            producer.send("6......", null);
            producer.send("3...", null);
            producer.send("2..", null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(String message, String routingKey) throws IOException {
        // connect to server, create and get channel
        Channel channel = openChannel();

        // Next we create a channel, which is where most of the API for getting things done resides.
        // To send, we must declare a queue for us to send to; then we can publish a message to the queue:
        // the second parameter (true) is named "durable", and tells server to don't lost the queue even if
        // server crash or restart
        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
        // MessageProperties.PERSISTENT_TEXT_PLAIN tells server how to persist the queue
        channel.basicPublish("", TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");

        // close connection
        closeChannel();
    }
}