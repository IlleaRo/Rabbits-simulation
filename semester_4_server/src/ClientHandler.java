import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;

public class ClientHandler extends Thread {
    public final String name;
    static final long durationOfPause = 100;
    private final Socket rxSocket;
    private final LinkedList<ClientHandler> serverList;
    private final BufferedInputStream bufferedInputStream;
    final BufferedOutputStream bufferedOutputStream;

    public ClientHandler(Socket rxSocket, LinkedList<ClientHandler> serverList) throws IOException {
        this.rxSocket = rxSocket;
        this.serverList = serverList;
        bufferedOutputStream = new BufferedOutputStream(rxSocket.getOutputStream());
        bufferedInputStream = new BufferedInputStream(rxSocket.getInputStream());
        byte[] temp = new byte[1024];
        bufferedInputStream.read(temp);
        String inputString = new String(temp);
        inputString = inputString.replaceAll("(?s)\n", "");
        inputString = inputString.replaceAll("(?s)\0", "");
        name = inputString+"\n\0";
        start();
    }

    @Override
    public void run() {
        String inputString;
        System.out.println("opened socket on port" + rxSocket.getLocalPort());
        try {
            while (true) {
                byte[] temp = new byte[1024];
                bufferedInputStream.read(temp);
                inputString = new String(temp);
                inputString = inputString.replaceAll("(?s)\n", "");
                inputString = inputString.replaceAll("(?s)\0", "");
                System.out.println(inputString);
                if (Objects.equals(inputString, "close")) {
                   bufferedOutputStream.write(("The connection was interrupted!").getBytes());
                   bufferedOutputStream.flush();
                   sleep(durationOfPause);
                   bufferedOutputStream.write(("STOP".getBytes()));
                   bufferedOutputStream.flush();
                   sleep(durationOfPause);
                   rxSocket.close();
                   serverList.remove(this);
                   Server.getInstance().updateList();
                   break;
                }
                if (Objects.equals(inputString, "port")) {
                   bufferedOutputStream.write((String.valueOf(rxSocket.getLocalPort())).getBytes());
                   bufferedOutputStream.flush();
                    continue;
                }
                if (Objects.equals(inputString, "getID")) {
                   bufferedOutputStream.write((String.valueOf(serverList.indexOf(this))).getBytes());
                   bufferedOutputStream.flush();
                    continue;
                }
                if(inputString.startsWith("CFG")) {
                    byte[] id = Arrays.copyOfRange(temp, 3, 7);
                    int idInt = ByteBuffer.wrap(id).getInt();
                    serverList.get(idInt).bufferedOutputStream.write(Arrays.copyOfRange(temp, 0, 40));
                    serverList.get(idInt).bufferedOutputStream.flush();
                    continue;
                }
                String[] strings = inputString.split(" ");
                if (Objects.equals(strings[0], "getConfig") && strings.length == 2) {
                    strings[1] = strings[1].replace("\n", "");
                    int id = Integer.parseInt(strings[1]);
                    if (id < serverList.size() && id >= 0) {
                        ByteBuffer message = ByteBuffer.allocate(3+4);
                        message.put("PUT".getBytes());
                        message.putInt(serverList.indexOf(this));
                        serverList.get(id).bufferedOutputStream.write(message.array());
                        serverList.get(id).bufferedOutputStream.flush();
                    }
                   bufferedOutputStream.write(("OK!").getBytes());
                   bufferedOutputStream.flush();
                    continue;
                }
               bufferedOutputStream.write(("Unknown command!!").getBytes());
               bufferedOutputStream.flush();
            }
        }
        catch (SocketException ignored) {
            Server.serverList.remove(this);
            try {
                Server.getInstance().updateList();
            } catch (IOException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            Server.serverList.remove(this);
            try {
                Server.getInstance().updateList();
            } catch (IOException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
