package rabbitmq.helloWorld;

import com.rabbitmq.client.Channel;
import rabbitmq.Producer;

import java.io.IOException;

/**
 * The sender will connect to RabbitMQ, send multiples messages, then exit.
 */
public class Sender extends Producer {

    public static void main(String[] args) {
        try {
            Producer producer = new Sender();
            producer.send("Hello World1!", null);
            producer.send("Hello World2!", null);
            producer.send("Hello World3!", null);
            producer.send("Hello World4!", null);
            producer.send("Hello World5!", null);
            producer.send("Hello World6!", null);
            producer.send("Hello World7!", null);
            producer.send("Hello World8!", null);
            producer.send("Hello World9!", null);
            producer.send("Hello World10!", null);
            producer.send("Hello World11!", null);
            producer.send("Hello World12!", null);
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
        channel.queueDeclare(QUEUE_HELLO_WORLD, false, false, false, null);
        channel.basicPublish("", QUEUE_HELLO_WORLD, null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");

        // close connection
        closeChannel();
    }
}