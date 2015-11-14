package rabbitmq.routing;

import rabbitmq.Consumer;

import java.io.IOException;

/**
 * This consumer only will receive the info messages.
 */
public class ReceiverInfo extends ReceiverLog {

    public ReceiverInfo(String[] routingKeys) {
        super(routingKeys);
    }

    public static void main(String[] args) {
        try {
            System.out.println("**************INFO MESSAGE*****************");
            Consumer consumer = new ReceiverInfo(new String[]{"info"});
            consumer.receive();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}