package rabbitmq.topic;

import rabbitmq.Consumer;

/**
 * This consumer will receive all animals messages.
 */
public class ReceiverAll extends ReceiverAnimal {

    public ReceiverAll() {
        // * (star) can substitute for exactly one word.
        // # (hash) can substitute for zero or more words.
        super(new String[]{"#"});
    }

    public static void main(String[] args) {
        try {
            System.out.println("**************ALL MESSAGE*****************");
            Consumer consumer = new ReceiverAll();
            consumer.receive();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}