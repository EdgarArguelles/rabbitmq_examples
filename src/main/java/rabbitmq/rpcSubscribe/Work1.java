package rabbitmq.rpcSubscribe;

import rabbitmq.QueueManager;

/**
 * Define server action 1
 */
public class Work1 extends Work {

    /**
     * Define server action 1
     */
    public Work1() {
        super("ACTION 1", QueueManager.RPC_QUEUE_ACTION1, new String[]{"action1"});
    }

    @Override
    protected int doWork(int number) throws Exception {
        System.out.println("doing work 1 ...");
        if (number < 1000) throw new Exception("value less than 1000");
        Thread.sleep(number);
        return number / 1000;
    }

    public static void main(String[] args) {
        new Work1().start();
    }
}