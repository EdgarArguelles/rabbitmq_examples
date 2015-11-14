package rabbitmq.topic;

import rabbitmq.Consumer;

import java.io.IOException;

/**
 * This consumer will receive Rabbit or Lazy animals messages.
 */
public class ReceiverRabbitOrLazy extends ReceiverAnimal {

    public ReceiverRabbitOrLazy() {
        // * (star) can substitute for exactly one word.
        // # (hash) can substitute for zero or more words.
        super(new String[]{"*.*.rabbit", "lazy.#"});
    }

    public static void main(String[] args) {
        try {
            System.out.println("**************RABBIT or LAZY MESSAGE*****************");
            Consumer consumer = new ReceiverRabbitOrLazy();
            consumer.receive();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}