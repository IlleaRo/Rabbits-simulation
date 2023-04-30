import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws IOException {
        int port = 25565;
        Server server = Server.getInstance();
        System.out.println("The server is running at " + port);
        while (true) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();
            if (Objects.equals(input, "stop") && server.isRunning()) {
                server.stop();
            }
            else if (Objects.equals(input, "start") && !server.isRunning()) {
                server.run();
            }
            else if (Objects.equals(input, "exit")) {
                server.stop();
                System.exit(0);
            }
        }
    }
}