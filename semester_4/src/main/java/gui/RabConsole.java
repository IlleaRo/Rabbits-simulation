package gui;

import animals.RabbitAlb;
import animals.RabbitArray;
import io.DB;
import network.Client;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class RabConsole extends JDialog {
    JTextPane commandStatusPane = new JTextPane();
    JTextField editPane = new JTextField();
    public JPanel connectStatusPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private final HashMap<String, FunctionPointerInt> commandsInt = new HashMap<>();
    private final HashMap<String, FunctionPointerBytesIntStr> commandsBytesIntStr = new HashMap<>();
    private final HashMap<String,FunctionPointerStr> commandsStr = new HashMap<>();
    private int cursor = 0;
    final private ArrayList<String> commandHistory = new ArrayList<>();
    public final MainGUI mainGUI;
    private final SimpleAttributeSet casualText, successResult, failureResult, serverWord;
    private static RabConsole INSTANCE;

    public static RabConsole getInstance() {
        if(INSTANCE == null) {
            throw new AssertionError("You have to call init first");
        }
        return INSTANCE;
    }
    public synchronized static RabConsole init(MainGUI frame) {
        if (INSTANCE != null)
        {
            throw new AssertionError("You already initialized me");
        }
        INSTANCE = new RabConsole(frame);
        return INSTANCE;
    }

    private RabConsole(MainGUI owner){
        super(owner,"Консоль",false);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        mainGUI = owner;
        // Сюда добавлять методы с названиями БЕЗ пробелов
        commandsInt.put("decreaseAlb",this::delAlbWithPercent);
        commandsBytesIntStr.put("connect",this::connectToServ);
        commandsStr.put("save",this::saveRabbits);
        commandsStr.put("load",this::loadRabbits);
        commandsStr.put("wipe",this::wipeRabbits);

        casualText = new SimpleAttributeSet();
        StyleConstants.setForeground(casualText, Color.BLACK);
        StyleConstants.setFontFamily(casualText,"Sans-serif");
        StyleConstants.setFontSize(casualText,18);
        StyleConstants.setBold(casualText, true);
        successResult = new SimpleAttributeSet(casualText);
        StyleConstants.setForeground(successResult, Color.GREEN);
        serverWord = new SimpleAttributeSet(casualText);
        StyleConstants.setForeground(serverWord,Color.BLUE);

        failureResult = new SimpleAttributeSet(casualText);
        StyleConstants.setForeground(failureResult, Color.RED);


        commandStatusPane.setEditable(true);
        commandStatusPane.setFocusable(false);
        editPane.setFont(new Font("Sans-serif",Font.PLAIN,18));

        Container container = this.getContentPane();
        container.setLayout(new BorderLayout());

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(700,400));
        setLocationRelativeTo(null);

        commandStatusPane.setPreferredSize(new Dimension(300,300));

        this.pack();
        JScrollPane scrollPane = new JScrollPane(commandStatusPane);
        container.add(scrollPane,BorderLayout.CENTER);
        container.add(editPane,BorderLayout.SOUTH);
        container.add(connectStatusPane,BorderLayout.NORTH);
        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER -> {
                        commandHistory.add(editPane.getText());
                        showCommandResult(editPane.getText(),performCommand(editPane.getText()));
                        editPane.setText("");
                        cursor = commandHistory.size();
                    }
                    case KeyEvent.VK_UP -> {
                        if (cursor==0) return;
                        editPane.setText(commandHistory.get(--cursor));
                    }
                    case KeyEvent.VK_DOWN -> {
                        if (cursor==commandHistory.size()) return;
                        editPane.setText(commandHistory.get(++cursor - 1));
                    }
                }

            }
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
            }
        };
        editPane.addKeyListener(keyAdapter);
        editPane.requestFocusInWindow();
    }


    private void showCommandResult(String msg, boolean isComplete)
    {
        if (Objects.equals(msg, "")) return;

        boolean msgToServ = msg.startsWith("S ");
        try {
            Client.getInstance();
        }catch (AssertionError e){
            msgToServ = false;
        }

        Document doc = commandStatusPane.getDocument();
        try {
            doc.insertString(doc.getLength(),msg+(msgToServ? " ---> ":""), casualText);

            if (!msgToServ) {
                if (isComplete)
                    doc.insertString(doc.getLength(), " - complete!\n", successResult);
                else
                    doc.insertString(doc.getLength(), " - error!\n", failureResult);
            }
        } catch(BadLocationException e) {
            e.printStackTrace();
        }
    }
    public void showMessage(String msg){
        if (Objects.equals(msg, "")) return;
        Document doc = commandStatusPane.getDocument();
        try {
            doc.insertString(doc.getLength(), msg, serverWord);
        } catch(BadLocationException e) {
            e.printStackTrace();
        }
    }
    private boolean performCommandBytesInt(String command){
        command = command.replace(":", " ");
        String[] bytesAndInt = command.split(" ");
        FunctionPointerBytesIntStr pointer = commandsBytesIntStr.get(bytesAndInt[0]);
        if (pointer==null) return false;
        if (bytesAndInt.length!=4) return false;
        byte[] bytes = new byte[4];
        String[] sBytes = bytesAndInt[1].split("\\.");
        if (sBytes.length!=4) return false;
        try {
            for (int i = 0; i < 4; i++) {
                bytes[i] = (byte) (Integer.parseInt(sBytes[i]));
            }
        }catch (Exception e){
            return false;
        }
        return pointer.methodSignature(bytes,Integer.parseInt(bytesAndInt[2]),bytesAndInt[3]);
    }
    private boolean performCommandInt(String command){
        char[] arrayOfCommand = command.toCharArray();
        // Начало и конец числа
        int indexOfStartN = 0, indexOfStopN = 0;
        for (int i = 0; i<arrayOfCommand.length;++i) {
            if (arrayOfCommand[i]>='0' && arrayOfCommand[i]<='9') {
                indexOfStartN = i;
                for (int j = i; j<arrayOfCommand.length;++j)
                    if (arrayOfCommand[j]>='0' && arrayOfCommand[j]<='9') indexOfStopN = j;
                break;
            }
        }
        int value = 0;
        if (indexOfStopN != 0)
            value = Integer.parseInt(command.substring(indexOfStartN,indexOfStopN+1));
        command = command.substring(0,indexOfStartN);
        command = command.replaceAll(" ","");
        FunctionPointerInt pointer = commandsInt.get(command);
        if (pointer!=null)
            return pointer.methodSignature(value);
        return false;
    }
    private boolean performCommandStr(String[] partsOfCommand){
        FunctionPointerStr pointer = commandsStr.get(partsOfCommand[0]);
        if (pointer!=null)
            return pointer.methodSignature(partsOfCommand[1]);
        return false;
    }
    private boolean performCommand(String command){
        if (command.startsWith("S "))
            return Client.sendMsgToServ(command.substring(2));
        if (command.contains(":"))
            return performCommandBytesInt(command);
        String[] partsOfCommand = command.split(" ");
        if (partsOfCommand.length==1) return false;
        try {
            Integer.parseInt(partsOfCommand[1]);
            return performCommandInt(command);
        }catch (NumberFormatException e){
            return performCommandStr(partsOfCommand);
        }
    }

    private interface FunctionPointerInt {
        boolean methodSignature(int number);
    }
    private interface FunctionPointerBytesIntStr {
        boolean methodSignature(byte[] bytes,int number, String str);
    }
    private interface FunctionPointerStr{
        boolean methodSignature(String str);
    }
    private boolean delAlbWithPercent(int percent) {
        if (RabbitArray.INSTANCE.getAlbCount() == 0) return true; // Бездействуем если кроликов нет
        int numberOfRabToDel = RabbitArray.INSTANCE.getAlbCount() * percent / 100;
        mainGUI.pauseSim();
        for (int i = 0;numberOfRabToDel>0; i++)
            if (RabbitArray.INSTANCE.get(i) instanceof RabbitAlb){
                RabbitArray.INSTANCE.remove(i);
                --numberOfRabToDel;
                --i;
            }
        mainGUI.resumeSim();
        return true;
    }
    private boolean connectToServ(byte[] address,int port, String name){
        try {
            Client.init(InetAddress.getByAddress(address), port, name);
        }catch (AssertionError | IOException e) {
            return false;
        }
        return true;
    }

    private boolean loadRabbits(String type){
        return DB.readDB(type.toLowerCase());
    }
    private boolean saveRabbits(String type){
        return DB.writeDB(type.toLowerCase());
    }
    private boolean wipeRabbits(String confirmation) {
        if (confirmation.equals("YES")) {
            return DB.wipeDB();
        }
        return false;
    }
}
