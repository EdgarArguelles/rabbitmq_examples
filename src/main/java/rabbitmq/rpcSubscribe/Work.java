package rabbitmq.rpcSubscribe;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import rabbitmq.Consumer;
import rabbitmq.Producer;

/**
 * Abstract class that define a specific server action or work
 */
public abstract class Work {

    private String name;
    private String queueActionName;
    private String[] routingKeys;

    /**
     * Work constructor
     *
     * @param name            work name
     * @param queueActionName queue name for this work
     * @param routingKeys     router keys to bind with this queue
     */
    public Work(String name, String queueActionName, String[] routingKeys) {
        this.name = name;
        this.queueActionName = queueActionName;
        this.routingKeys = routingKeys;
    }

    private class Receiver extends Consumer {

        private QueueingConsumer queueConsumer;

        public QueueingConsumer getQueueConsumer() {
            return queueConsumer;
        }

        @Override
        public void receive() throws Exception {
            // connect to server, create and/or get channel
            Channel channel = openChannel();

            // connect to exchange
            channel.exchangeDeclare(RPC_EXCHANGE, "direct");

            // Server will listen to specific action queue for any client request
            // the second parameter (true) is named "durable", and tells server to don't lost the queue even if
            // server crash or restart
            channel.queueDeclare(queueActionName, true, false, false, null);

            // Now we need to tell the exchange to send messages to our queue. A binding is a relationship between
            // an exchange and a queue. This can be simply read as: the queue is interested in messages from this exchange.
            for (String key : routingKeys) {
                channel.queueBind(queueActionName, RPC_EXCHANGE, key);
            }

            // usually server send all message to a Consumer, basicQos(1) tells RabbitMQ not to give more than one message
            // to a worker at a time. Or, in other words, don't dispatch a new message to a worker until it has processed
            // and acknowledged the previous one. Instead, it will dispatch it to the next worker that is not still busy.
            channel.basicQos(1);

            // server will stay waiting any client request using QueueingConsumer class to handle callbacks
            queueConsumer = new QueueingConsumer(channel);

            // the second parameter "false" called "autoAck", tells server should expect explicit acknowledgements (channel.basicAck)
            // that server will delete message from queue until an explicit acknowledgement arrives
            channel.basicConsume(queueActionName, false, queueConsumer);
            System.out.println(" [x] Awaiting " + name + " requests");
        }
    }

    private class Sender extends Producer {

        private QueueingConsumer.Delivery delivery;

        public Sender(QueueingConsumer.Delivery delivery) {
            this.delivery = delivery;
        }

        @Override
        public void send(String message, String routingKey) throws Exception {
            // connect to server, create and/or get channel
            Channel channel = openChannel();

            // get request BasicProperties
            BasicProperties props = delivery.getProperties();

            // create a replay BasicProperties to send same correlationId than request message
            BasicProperties replyProps = new BasicProperties();
            replyProps.setCorrelationId(props.getCorrelationId());

            // publish server response to replayTo queue using the same correlationId than request message
            channel.basicPublish("", props.getReplyTo(), replyProps, message.getBytes("UTF-8"));
        }

        /**
         * Notify server that message is complete (explicit acknowledgement)
         *
         * @param deliveryTag message tag to notify complete
         * @throws Exception
         */
        public void basicAck(long deliveryTag) throws Exception {
            // connect to server, create and/or get channel
            Channel channel = openChannel();
            channel.basicAck(deliveryTag, false);
        }
    }

    /**
     * start work listener
     */
    public void start() {
        Consumer consumer = new Receiver();
        try {
            // server will listen to rpc_queue queue for any client request
            consumer.receive();

            // create an infinite loop to simulate a server always listening
            while (true) {
                // sleep process flow until request arrives
                QueueingConsumer.Delivery delivery = ((Receiver) consumer).getQueueConsumer().nextDelivery();

                // when a request arrive, process it an send response to replayTo queue
                Producer producer = new Sender(delivery);
                String response = null;
                try {
                    // process request
                    String message = new String(delivery.getBody(), "UTF-8");
                    int n = Integer.parseInt(message);

                    System.out.println(" [.] delay(" + message + ")");
                    response = "" + doWork(n);
                } catch (Exception e) {
                    System.out.println(" [.] " + e.toString());
                    response = "";
                } finally {
                    // send response to replayTo queue
                    producer.send(response, null);
                    // notify queue server that current message is completed
                    ((Sender) producer).basicAck(delivery.getEnvelope().getDeliveryTag());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                consumer.closeChannel();
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * specific action or work
     *
     * @param number number to be processed
     * @return value returned by work
     */
    protected abstract int doWork(int number) throws Exception;
}