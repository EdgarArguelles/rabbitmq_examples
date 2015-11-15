package rabbitmq.rpc;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import rabbitmq.Consumer;
import rabbitmq.Producer;

import java.util.UUID;

public class RPCClient {

    private String replyQueueName;
    private QueueingConsumer queueConsumer;

    private class Receiver extends Consumer {

        @Override
        public void receive() throws Exception {
            // connect to server, create and/or get channel
            final Channel channel = openChannel();

            // create a non-durable, exclusive, auto delete queue with a generated name for this client
            replyQueueName = channel.queueDeclare().getQueue();

            // client will stay waiting any server response using QueueingConsumer class to handle callbacks
            queueConsumer = new QueueingConsumer(channel);

            // the second parameter "true" called "autoAck", tells server should consider messages acknowledged once delivered
            // that means once the message is delivered, the server will delete message from queue
            channel.basicConsume(replyQueueName, true, queueConsumer);
        }
    }

    private class Sender extends Producer {

        private String correlationId;

        public Sender(String correlationId) {
            this.correlationId = correlationId;
        }

        @Override
        public void send(String message, String routingKey) throws Exception {
            // connect to server, create and/or get channel
            Channel channel = openChannel();

            // create request BasicProperties
            BasicProperties props = new BasicProperties();
            props.setCorrelationId(correlationId);
            props.setReplyTo(replyQueueName);

            // publish client request to "rpc_queue" queue sending an unique correlationId and a replayTo queue
            channel.basicPublish("", RPC_QUEUE_NAME, props, message.getBytes("UTF-8"));
        }
    }

    public RPCClient() throws Exception {
        // when this client is create, it creates an unique dynamic queue for this client, in order to receive
        // responds from server
        Consumer consumer = new Receiver();
        consumer.receive();
    }

    public String call(String message) throws Exception {
        // create an unique correlationId for this request
        String correlationId = UUID.randomUUID().toString();

        // create an queue producer and send request message to "rpc_queue" queue
        Producer producer = new Sender(correlationId);
        producer.send(message, null);
        System.out.println(" [x] Awaiting RPC response");

        // this client could receive all responses for different request, so we need to create an infinite loop
        // until receive respond for this request
        while (true) {
            // sleep process flow until a response arrives
            QueueingConsumer.Delivery delivery = queueConsumer.nextDelivery();

            // if current response doesn't correspond with correlationId request, ignore it and wait for another
            if (delivery.getProperties().getCorrelationId().equals(correlationId)) {
                String response = new String(delivery.getBody(), "UTF-8");
                producer.closeChannel();
                return response;
            }
        }
    }

    public static void main(String[] argv) {
        try {
            RPCClient client = new RPCClient();
            int value = 6000;
            System.out.println(" [x] Requesting delay(" + value + ")");
            String response = client.call("" + value);
            System.out.println(" [.] Server last '" + response + "' seconds.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}