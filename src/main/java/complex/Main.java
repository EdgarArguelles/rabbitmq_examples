package complex;

public class Main {

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        new Thread(server).start();

        Client client = new Client();
        int value = 4000;
        String response = client.call("" + value, "action");
        System.out.println("The server last " + response + " seconds");

        server.closeConection();
        System.exit(0);
    }
}