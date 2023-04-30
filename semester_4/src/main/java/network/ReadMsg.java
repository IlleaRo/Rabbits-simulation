package network;

import gui.MainGUI;
import gui.RabConsole;
import io.Config;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ReadMsg extends Thread{
    Socket socket;
    final BufferedOutputStream bufferedOutputStream;
    final BufferedInputStream bufferedInputStream;
    ReadMsg(Socket socket) throws IOException {
        this.socket = socket;
        bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
        bufferedInputStream = new BufferedInputStream(socket.getInputStream());
    }
    @Override
    public void run(){
        String str;
        try {
            while (true){
                byte[] temp = new byte[1024];
                bufferedInputStream.read(temp);
                str = new String(temp);
                str = str.replaceAll("(?s)\n", "");
                str = str.replaceAll("(?s)\0", "");
                if (str.startsWith("CFG")){
                    byte cfg[] = Arrays.copyOfRange(temp, 7, 40);
                    Config.INSTANCE.fromBytes(ByteBuffer.wrap(cfg));
                    RabConsole.getInstance().mainGUI.getConfig();
                    continue;
                }
                if (str.startsWith("PUT")){
                    MainGUI.getInstance().dumpConfig();
                    ByteBuffer message = ByteBuffer.allocate(3+4+33);
                    message.put("CFG".getBytes());
                    message.put(Arrays.copyOfRange(temp, 3, 7));
                    message.put(Config.INSTANCE.toBytes().array());
                    bufferedOutputStream.write(message.array());
                    bufferedOutputStream.flush();
                    System.out.println("Config sent");
                    continue;
                }
                if (str.equals("UPDATE")){
                    RabConsole.getInstance().connectStatusPane.removeAll();
                    bufferedInputStream.read(temp);
                    String names = new String(temp);
                    names = names.replaceAll("(?s)\n", "");
                    names = names.replaceAll("(?s)\0", "");
                    for (String nameOfUser: names.split(";")) {
                        RabConsole.getInstance().connectStatusPane.add(new JLabel(nameOfUser));
                    }
                    RabConsole.getInstance().connectStatusPane.setVisible(true);
                    RabConsole.getInstance().pack();
                    RabConsole.getInstance().repaint();
                    continue;
                }
                if (str.equals("STOP")){
                    Client.closeConnect();
                    RabConsole.getInstance().connectStatusPane.setVisible(false);
                    RabConsole.getInstance().pack();
                    break;
                }
                if (!str.equals(""))
                    RabConsole.getInstance().showMessage("Server: "+str+"\n");
                sleep(1000);
            }
        }catch (IOException | InterruptedException e){
            e.printStackTrace();
            try {
                Client.closeConnect();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}