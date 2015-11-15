package rabbitmq.rpcSubscribe;

import rabbitmq.QueueManager;

/**
 * Define server action 2
 */
public class Work2 extends Work {

    /**
     * Define server action 2
     */
    public Work2() {
        super("ACTION 2", QueueManager.RPC_QUEUE_ACTION2, new String[]{"action2"});
    }

    @Override
    protected int doWork(int number) throws Exception {
        System.out.println("doing work 2 ...");
        if (number < 1000) throw new Exception("value less than 1000");
        Thread.sleep(number);
        return number / 1000;
    }

    public static void main(String[] args) {
        new Work2().start();
    }
}