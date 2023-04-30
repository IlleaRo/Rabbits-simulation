package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ReadMsg extends Thread{
    Socket socket;
    ReadMsg(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run(){
        String str;
        try {
            while (true){
                byte[] temp = new byte[1024];
                socket.getInputStream().read(temp);
                str = new String(temp);
                str = str.replaceAll("(?s)\n", "");
                str = str.replaceAll("(?s)\0", "");
                if (str.equals("UPDATE")){
                    break;
                }
                if (str.equals("STOP")){
                    break;
                }
                System.out.println(str);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
