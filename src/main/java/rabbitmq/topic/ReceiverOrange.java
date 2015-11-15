package rabbitmq.topic;

import rabbitmq.Consumer;

/**
 * This consumer will receive orange animals messages.
 */
public class ReceiverOrange extends ReceiverAnimal {

    public ReceiverOrange() {
        // * (star) can substitute for exactly one word.
        // # (hash) can substitute for zero or more words.
        super(new String[]{"*.orange.*"});
    }

    public static void main(String[] args) {
        try {
            System.out.println("**************ORANGE MESSAGE*****************");
            Consumer consumer = new ReceiverOrange();
            consumer.receive();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}