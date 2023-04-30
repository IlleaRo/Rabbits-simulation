import Client.ReadMsg;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Objects;

import static java.lang.Thread.sleep;

public class Server implements Runnable{
    private static Server INSTANCE;
    private static int idCounter;
    public final int port;
    private final ServerSocket txSocket;
    private boolean running;
    public static LinkedList<ClientHandler> serverList = new LinkedList<>();
    private static final long durationOfPause = 100;
    private Server() throws IOException {
        this.port = 25565;
        this.txSocket = new ServerSocket(port);
        new Thread(this).start();
    }
    public static Server getInstance() throws IOException {
        if (INSTANCE == null)
            synchronized (Server.class){
                if (INSTANCE == null)
                    INSTANCE = new Server();
            }
        return INSTANCE;
    }
    @Override
    public void run() {
        this.running = true;
        while (running) {
            try {
                serverList.add(new ClientHandler(txSocket.accept(), serverList));
                idCounter++;
                updateList();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                stop();
            }
        }
    }
    void updateList() throws IOException, InterruptedException {
        String[] names = new String[serverList.size()];
        int i = 0;
        for (ClientHandler cH:serverList) {
            System.out.print(cH.name+" ");
            names[i] = cH.name;
            cH.bufferedOutputStream.write(("UPDATE").getBytes());
            cH.bufferedOutputStream.flush();
            ++i;
        }
        sleep(durationOfPause);
        for (ClientHandler cH:serverList) {
            cH.bufferedOutputStream.write(String.join(";", names).getBytes());
            cH.bufferedOutputStream.flush();
        }
        System.out.println();
    }
    public void stop() {
        this.running = false;
    }
    public boolean isRunning() {
        return this.running;
    }
}
