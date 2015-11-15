package rabbitmq.rpcSubscribe;

import rabbitmq.QueueManager;

/**
 * Define server action 3
 */
public class Work3 extends Work {

    /**
     * Define server action 3
     */
    public Work3() {
        super("ACTION 3", QueueManager.RPC_QUEUE_ACTION3, new String[]{"action3"});
    }

    @Override
    protected int doWork(int number) throws Exception {
        System.out.println("doing work 3 ...");
        if (number < 1000) throw new Exception("value less than 1000");
        Thread.sleep(number);
        return number / 1000;
    }

    public static void main(String[] args) {
        new Work3().start();
    }
}