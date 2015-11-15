package rabbitmq.publishSubscribe;

import com.rabbitmq.client.Channel;
import rabbitmq.Producer;

/**
 * The sender will connect to RabbitMQ, emits log messages to an exchange instead of a specific queue, then exit.
 */
public class Sender extends Producer {

    public static void main(String[] args) {
        try {
            Producer producer = new Sender();
            producer.send("message1", null);
            producer.send("message2", null);
            producer.send("message3", null);
            producer.send("message4", null);
            producer.send("message5", null);
            producer.send("message6", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(String message, String routingKey) throws Exception {
        // connect to server, create and/or get channel
        Channel channel = openChannel();

        // create a exchange, an exchange is a very simple thing. On one side it receives messages
        // from producers and the other side it pushes them to queues, there are a few exchange types
        // available: direct, topic, headers and fanout, in this case we're going to use a fanout exchange called logs
        // a fanout exchange broadcasts all the messages it receives to all the queues it knows
        channel.exchangeDeclare(EXCHANGE_LOGS, "fanout");

        // In this case we don't need to create a queue (channel.queueDeclare), because the exchange will send
        // message to all queues that it knows

        // Now we're going to use the "logs" exchange instead of default exchange "", We need to supply a routingKey
        // when sending, but its value is ignored for fanout exchanges and because we're using a fanout exchange we
        // don't need a routingKey so we're using ""
        channel.basicPublish(EXCHANGE_LOGS, "", null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");

        // close connection
        closeChannel();
    }
}