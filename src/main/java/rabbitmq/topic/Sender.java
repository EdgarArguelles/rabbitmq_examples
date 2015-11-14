package rabbitmq.topic;

import com.rabbitmq.client.Channel;
import rabbitmq.Producer;

import java.io.IOException;

/**
 * The sender will connect to RabbitMQ, emits animals messages with routingKey to an exchange instead
 * of a specific queue, then exit.
 */
public class Sender extends Producer {

    public static void main(String[] args) {
        try {
            // In this example, we're going to send messages which all describe animals. The messages will be
            // sent with a routing key that consists of three words (two dots). The first word in the routing
            // key will describe speed, second a colour and third a species: "<speed>.<colour>.<species>".

            Producer producer = new Sender();
            producer.send("A Quick Orange Rabbit", "quick.orange.rabbit");
            producer.send("A Lazy Orange Elephant", "lazy.orange.elephant");
            producer.send("A Quick Orange Fox", "quick.orange.fox");
            producer.send("A Lazy Brown Fox", "lazy.brown.fox");
            producer.send("A Lazy Pink Rabbit", "lazy.pink.rabbit");
            producer.send("Lost message 1", "quick.brown.fox");
            producer.send("Lost message 2", "orange");
            producer.send("Lost message 3", "quick.orange.male.rabbit");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(String message, String routingKey) throws IOException {
        // connect to server, create and get channel
        Channel channel = openChannel();

        // create a exchange, an exchange is a very simple thing. On one side it receives messages
        // from producers and the other side it pushes them to queues, there are a few exchange types
        // available: direct, topic, headers and fanout, in this case we're going to use a topic exchange
        // called "topic_logs" a topic exchange allow us to router or filter some specific messages, the routing
        // algorithm behind a topic exchange is simple, a message sent with a particular routing key will be
        // delivered to all the queues that are bound with a matching binding key.
        channel.exchangeDeclare(EXCHANGE_TOPIC_LOGS, "topic");

        // In this case we don't need to create a queue (channel.queueDeclare), because the exchange will send
        // message to all queues that it knows

        // Now we're going to use the "topic_logs" exchange instead of default exchange "", and supply a routingKey
        // to allow routing the message
        channel.basicPublish(EXCHANGE_TOPIC_LOGS, routingKey, null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");

        // close connection
        closeChannel();
    }
}