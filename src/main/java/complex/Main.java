package complex;

public class Main {

    public static void main(String[] args) throws Exception {
        Thread server1 = new Thread(new Server());
        server1.start();

        Client client = new Client();
        int value = 4000;
        String response = client.call("" + value, "action");
        System.out.println("The response was: " + response);

        System.exit(0);
    }
}