package rabbitmq.routing;

import rabbitmq.Consumer;

import java.io.IOException;

/**
 * This consumer will receive the warning and error messages.
 */
public class ReceiverAtention extends ReceiverLog {

    public ReceiverAtention(String[] routingKeys) {
        super(routingKeys);
    }

    public static void main(String[] args) {
        try {
            System.out.println("**************WARNING and ERROR MESSAGE*****************");
            Consumer consumer = new ReceiverAtention(new String[]{"warning", "error"});
            consumer.receive();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}