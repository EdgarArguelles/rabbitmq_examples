package rabbitmq.routing;

import rabbitmq.Consumer;

import java.io.IOException;

/**
 * This consumer will receive all messages.
 */
public class ReceiverAll extends ReceiverLog {

    public ReceiverAll() {
        super(new String[]{"info", "warning", "error"});
    }

    public static void main(String[] args) {
        try {
            System.out.println("**************ALL MESSAGE*****************");
            Consumer consumer = new ReceiverAll();
            consumer.receive();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}