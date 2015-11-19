package complex;

import complex.queue.Consumer;
import complex.queue.Message;
import complex.queue.Producer;
import complex.queue.rabbitmq.server.ServerConsumer;
import complex.queue.rabbitmq.server.ServerProducer;

public class Server implements Runnable {

    private Consumer consumer;

    @Override
    public void run() {
        try {
            consumer = new ServerConsumer();
            consumer.startListening();
            System.out.println("SERVER Listening ... ");

            // create a infinite loop that simulate a server listening
            while (true) {
                Message message = consumer.nextMessage();
                String response = null;
                try {
                    // process request
                    String msg = message.getBodyAsString();
                    int n = Integer.parseInt(msg);
                    System.out.println("SERVER [.] delay(" + msg + ")");
                    response = "" + doWork(n);
                } catch (Exception e) {
                    System.out.println("SERVER [.] " + e.toString());
                    response = "less than 1000";
                } finally {
                    Producer producer = new ServerProducer();
                    // send response to replayTo queue
                    producer.send(response, null, message);
                    producer.closeConection();
                    // notify queue server that current message is completed
                    consumer.notifyAcknowledgement(message.getDeliveryTag());
                }
            }
        } catch (Exception e) {
        }
    }

    public static int doWork(int number) throws Exception {
        if (number < 1000) throw new Exception("value less than 1000");
        Thread.sleep(number);
        return number / 1000;
    }

    public void closeConection() throws Exception {
        consumer.closeConection();
    }
}