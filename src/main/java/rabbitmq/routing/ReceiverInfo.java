package rabbitmq.routing;

import rabbitmq.Consumer;

/**
 * This consumer only will receive the info messages.
 */
public class ReceiverInfo extends ReceiverLog {

    public ReceiverInfo() {
        super(new String[]{"info"});
    }

    public static void main(String[] args) {
        try {
            System.out.println("**************INFO MESSAGE*****************");
            Consumer consumer = new ReceiverInfo();
            consumer.receive();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}