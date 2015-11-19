package complex;

import complex.queue.Consumer;
import complex.queue.Message;
import complex.queue.Producer;
import complex.queue.rabbitmq.client.ClientConsumer;
import complex.queue.rabbitmq.client.ClientProducer;

import java.util.UUID;

public class Client {

    public String call(String msg, String action) throws Exception {
        Consumer consumer = new ClientConsumer();
        Producer producer = new ClientProducer();

        // create an unique correlationId for this request
        String correlationId = UUID.randomUUID().toString();
        producer.send(msg, action, new Message(correlationId, ((ClientConsumer) consumer).getReplyQueueName()));
        producer.closeConection();
        System.out.println("CLIENT waiting for server ...");

        // wait until server responds
        while (true) {
            // sleep process flow until a response arrives
            Message message = consumer.nextMessage();

            if (message.getCorrelationId().equals(correlationId)) {
                String response = message.getBodyAsString();
                consumer.closeConection();
                return response;
            }
        }
    }
}