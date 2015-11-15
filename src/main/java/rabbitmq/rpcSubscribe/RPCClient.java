package rabbitmq.rpcSubscribe;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.QueueingConsumer;
import rabbitmq.Consumer;
import rabbitmq.Producer;

import java.util.UUID;

/**
 * Class that simulate a server client, this server create a producer which send request to server on a specific queue
 * and create a consumer which listen the request response
 */
public class RPCClient {

    private Consumer consumer;

    private class Receiver extends Consumer {

        private String replyQueueName;
        private QueueingConsumer queueConsumer;

        public String getReplyQueueName() {
            return replyQueueName;
        }

        public QueueingConsumer getQueueConsumer() {
            return queueConsumer;
        }

        @Override
        public void receive() throws Exception {
            // connect to server, create and/or get channel
            Channel channel = openChannel();

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

            // connect to exchange
            channel.exchangeDeclare(RPC_EXCHANGE, "direct");

            // create request BasicProperties
            BasicProperties props = MessageProperties.PERSISTENT_TEXT_PLAIN;
            props.setCorrelationId(correlationId);
            props.setReplyTo(((Receiver) consumer).getReplyQueueName());

            // publish client request to specific queue according with routingKey, in order to execute a specific
            // action or work on server side, sending an unique correlationId and a replayTo queue
            channel.basicPublish(RPC_EXCHANGE, routingKey, props, message.getBytes("UTF-8"));
        }
    }

    public RPCClient() throws Exception {
        // when this client is create, it creates an unique dynamic queue for this client, in order to receive
        // responds from server
        consumer = new Receiver();
        consumer.receive();
    }

    public String call(String message, String action) throws Exception {
        // create an unique correlationId for this request
        String correlationId = UUID.randomUUID().toString();

        // create an queue producer and send request message to "rpc_queue" queue
        Producer producer = new Sender(correlationId);
        producer.send(message, action);
        System.out.println(" [x] Awaiting " + action + " response");

        // this client has its own queue (replyQueueName) but all requests use the same queue whit different
        // correlationId, so this client could receive many different responses on the same queue, for this reason
        // we need to create an infinite loop until receive the correct respond for this request
        while (true) {
            // sleep process flow until a response arrives
            QueueingConsumer.Delivery delivery = ((Receiver) consumer).getQueueConsumer().nextDelivery();

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
            int value = 11000;
            String action = "action3";
            System.out.println(" [x] Requesting delay(" + value + ")");
            String response = client.call("" + value, action);
            System.out.println(" [.] Server last '" + response + "' seconds.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}