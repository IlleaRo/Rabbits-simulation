package Client;


import java.io.*;
import java.net.Socket;

public class Client {
    private static Socket clientSocket;
    private static ReadMsg readMsg;
    private static WriteMsg writeMsg;
    public static void main(String[] args) {
        try {
            clientSocket = new Socket("localhost", 25565);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        readMsg = new ReadMsg(clientSocket);
        writeMsg = new WriteMsg(clientSocket);
        readMsg.start();
        writeMsg.start();
    }
}
