package Client;


import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;


public class WriteMsg extends Thread{
    Socket socket;
    WriteMsg(Socket socket){
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
                if (str.equals("CLOSE")) break;
                socket.getOutputStream().write(str.getBytes());
                socket.getOutputStream().flush();
                if (str.equals("CONF")){
                    int a;
                    byte[] number = new byte[4];
                    socket.getInputStream().read(number);
                    a = ByteBuffer.wrap(number).getInt();
                    System.out.println(a);
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
