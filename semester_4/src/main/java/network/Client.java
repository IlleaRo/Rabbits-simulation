package network;

import gui.RabConsole;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

//Singleton класс клиента
public class Client {
    private static Socket clientSocket;
    static ReadMsg readMsg;
    private static Client INSTANCE;

    private Client(InetAddress address, int port, String name) throws IOException{
        clientSocket = new Socket(address, port);
        readMsg = new ReadMsg(clientSocket);
        readMsg.bufferedOutputStream.write(name.getBytes());
        readMsg.bufferedOutputStream.flush();
        readMsg.start();
    }
    public static boolean sendMsgToServ(String msg){
        if (clientSocket == null || clientSocket.isClosed()){
            return false;
        }
        try {
            readMsg.bufferedOutputStream.write((msg).getBytes());
            readMsg.bufferedOutputStream.flush();
        }catch (IOException e){
            RabConsole.getInstance().showMessage(e.toString());
            return false;
        } catch (Exception e){
            return false;
        }
        return true;
    }
    public static Client getInstance() {
        if(INSTANCE == null) {
            throw new AssertionError("You have to call init first");
        }
        return INSTANCE;
    }
    public synchronized static Client init(InetAddress address,int port, String name) throws IOException {
        if (INSTANCE != null)
        {
            throw new AssertionError("You already initialized me");
        }
        INSTANCE = new Client(address, port, name);
        return INSTANCE;
    }
    // Отключение от сервера
    public static void closeConnect() throws IOException{
        clientSocket.close();
        INSTANCE = null;
    }
    public static Client reConnect() throws IOException{
        closeConnect();
        return getInstance();
    }
}
