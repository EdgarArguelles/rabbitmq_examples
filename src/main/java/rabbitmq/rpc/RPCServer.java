package rabbitmq.rpc;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import rabbitmq.Consumer;
import rabbitmq.Producer;

public class RPCServer {

    private QueueingConsumer queueConsumer;
    private QueueingConsumer.Delivery delivery;

    private class Receiver extends Consumer {

        @Override
        public void receive() throws Exception {
            // connect to server, create and/or get channel
            final Channel channel = openChannel();

            // Server will listen rpc_queue for any client request
            channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);

            // usually server send all message to a Consumer, basicQos(1) tells RabbitMQ not to give more than one message
            // to a worker at a time. Or, in other words, don't dispatch a new message to a worker until it has processed
            // and acknowledged the previous one. Instead, it will dispatch it to the next worker that is not still busy.
            channel.basicQos(1);

            // server will stay waiting any client request using QueueingConsumer class to handle callbacks
            queueConsumer = new QueueingConsumer(channel);

            // the second parameter "false" called "autoAck", tells server should expect explicit acknowledgements (channel.basicAck)
            // that server will delete message from queue until an explicit acknowledgement arrives
            channel.basicConsume(RPC_QUEUE_NAME, false, queueConsumer);
            System.out.println(" [x] Awaiting RPC requests");
        }
    }

    private class Sender extends Producer {

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

    public void start() {
        Consumer consumer = new Receiver();
        Producer producer = new Sender();
        try {
            // server will listen to rpc_queue queue for any client request
            consumer.receive();

            // create an infinite loop to simulate a server always listening
            while (true) {
                // sleep process flow until request arrives
                delivery = queueConsumer.nextDelivery();

                // when a request arrive, process it an send response to replayTo queue
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
                producer.closeChannel();
            } catch (Exception ignore) {
            }
        }
    }

    private int doWork(int n) throws Exception {
        if (n < 1000) throw new Exception("value less than 1000");
        Thread.sleep(n);
        return n / 1000;
    }

    public static void main(String[] args) {
        RPCServer server = new RPCServer();
        server.start();
    }
}