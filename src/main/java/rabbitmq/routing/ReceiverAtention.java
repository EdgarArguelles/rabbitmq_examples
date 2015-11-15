package rabbitmq.routing;

import rabbitmq.Consumer;

/**
 * This consumer will receive the warning and error messages.
 */
public class ReceiverAtention extends ReceiverLog {

    public ReceiverAtention() {
        super(new String[]{"warning", "error"});
    }

    public static void main(String[] args) {
        try {
            System.out.println("**************WARNING and ERROR MESSAGE*****************");
            Consumer consumer = new ReceiverAtention();
            consumer.receive();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}