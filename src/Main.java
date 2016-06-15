import java.net.ServerSocket;

public class Main {

    public static void main(String[] args) {
        //Construct a server and a couple clients to test. All on port 5190, but the port is variable.
        new Server(5190).start();
        new Client(5190).start();
        new Client(5190).start();
    }
}
