package rabbitmq.routing;

import com.rabbitmq.client.Channel;
import rabbitmq.Producer;

/**
 * The sender will connect to RabbitMQ, emits log messages with routingKey to an exchange instead of a specific queue,
 * then exit.
 */
public class Sender extends Producer {

    public static void main(String[] args) {
        try {
            Producer producer = new Sender();
            producer.send("INFO: message1", "info");
            producer.send("INFO: message2", "info");
            producer.send("INFO: message3", "info");
            producer.send("WARNING: message1", "warning");
            producer.send("WARNING: message2", "warning");
            producer.send("ERROR: message1", "error");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(String message, String routingKey) throws Exception {
        // connect to server, create and get channel
        Channel channel = openChannel();

        // create a exchange, an exchange is a very simple thing. On one side it receives messages
        // from producers and the other side it pushes them to queues, there are a few exchange types
        // available: direct, topic, headers and fanout, in this case we're going to use a direct exchange
        // called "direct_logs" a direct exchange allow us to router or filter some specific messages, the routing
        // algorithm behind a direct exchange is simple, a message goes to the queues whose binding routing key exactly
        // matches the publish routing key of the message.
        channel.exchangeDeclare(EXCHANGE_DIRECT_LOGS, "direct");

        // In this case we don't need to create a queue (channel.queueDeclare), because the exchange will send
        // message to all queues that it knows

        // Now we're going to use the "direct_logs" exchange instead of default exchange "", and supply a routingKey
        // to allow routing the message
        channel.basicPublish(EXCHANGE_DIRECT_LOGS, routingKey, null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");

        // close connection
        closeChannel();
    }
}