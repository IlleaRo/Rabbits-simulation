package gui;

import animals.Rabbit;
import animals.RabbitAlb;
import animals.RabbitArray;
import animals.RabbitReg;
import io.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

public class MainGUI extends JFrame {
    public Habitat habitat;
    private boolean displayInfo = true;
    private java.util.Timer habitatTimer;
    private final Timer simTimer;
    private LocalDateTime startTime;
    private LocalDateTime pauseStart;
    private boolean running = false;
    private final Cell cell;
    final Object[] answers = {"Да","Нет"};
    private final UpperPanel upperPanel;
    private final BottomPanel bottomPanel;
    final Container mainContainer;
    private MainGUI() {
        super("Cell simulator");
        RabConsole.init(this);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double height = screenSize.getHeight();
        double width = screenSize.getWidth();
        this.setBounds((int) width/2-400, (int) height/2-400 , 800, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainContainer = this.getContentPane();
        mainContainer.setLayout(new BorderLayout());

        bottomPanel = new BottomPanel();
        upperPanel = new UpperPanel();
        bottomPanel.bStop.setEnabled(false);
        bottomPanel.bShowStat.setEnabled(false);
        bottomPanel.showTime.setSelected(true);
        bottomPanel.showInfo.setSelected(true);

        cell = new Cell(this);
        mainContainer.add(cell, BorderLayout.CENTER);
        mainContainer.add(bottomPanel, BorderLayout.SOUTH);
        mainContainer.add(upperPanel, BorderLayout.NORTH);
        //pack();
        Rabbit.parentPanel = cell;
        KeyAdapter kA = new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_T -> {
                        if (!upperPanel.isVisible()){
                            bottomPanel.showTime.doClick();
                        }
                        else bottomPanel.hideTime.doClick();
                    }
                    case KeyEvent.VK_E -> cancelSim();
                    case KeyEvent.VK_B -> restartSim();
                    case KeyEvent.VK_Q -> showRabbits();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
            }
        };
        this.addKeyListener(kA);
        simTimer = new Timer(1000, event ->
                upperPanel.timeLabel.setText(Long.toString(Duration.between(startTime, LocalDateTime.now()).toSeconds())));

        bottomPanel.bStart.addActionListener(event -> startSim());
        bottomPanel.bStop.addActionListener(event -> cancelSim());
        bottomPanel.bShowStat.addActionListener(e -> showRabbits());

        bottomPanel.showTime.addActionListener(e -> {
            upperPanel.setVisible(true);
            this.requestFocusInWindow();
        });
        bottomPanel.hideTime.addActionListener(e -> {
            upperPanel.setVisible(false);
            this.requestFocusInWindow();
        });
        bottomPanel.showInfo.addActionListener(e -> {
            displayInfo=true;
            this.requestFocusInWindow();
        });
        bottomPanel.hideInfo.addActionListener(e -> {
            displayInfo=false;
            this.requestFocusInWindow();
        });

        bottomPanel.bToggleReg.addActionListener(e ->{
            if (cell.regOnPause){
                bottomPanel.bToggleReg.setText("Вкл.");
                bottomPanel.bToggleReg.setBackground(Color.green);
                RabbitReg.renew();
                cell.regOnPause=false;
            }else {
                bottomPanel.bToggleReg.setText("Выкл.");
                bottomPanel.bToggleReg.setBackground(Color.red);
                RabbitReg.pause();
                cell.regOnPause=true;
            }
            this.requestFocusInWindow();
        });
        bottomPanel.bToggleAlb.addActionListener(e ->{
            if (cell.albOnPause){
                bottomPanel.bToggleAlb.setText("Вкл.");
                bottomPanel.bToggleAlb.setBackground(Color.green);
                RabbitAlb.renew();
                cell.albOnPause=false;
            }else {
                bottomPanel.bToggleAlb.setText("Выкл.");
                bottomPanel.bToggleAlb.setBackground(Color.red);
                RabbitAlb.pause();
                cell.albOnPause=true;
            }
            this.requestFocusInWindow();
        });
        upperPanel.bConsole.addActionListener(e -> showConsole());
        upperPanel.bSave.addActionListener(e -> {
            try {
                save();
            }catch (Exception exception){
                exception.printStackTrace();
            }

        });
        upperPanel.bLoad.addActionListener(e -> {
            try {
                load();
            }catch (Exception exception){
                exception.printStackTrace();
            }
        });
        try{
            Config.INSTANCE.read(Path.of("./config.cfg"));
            getConfig();
        }
        catch (IOException ignored) {}
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                dumpConfig();
                Config.INSTANCE.write(Path.of("./config.cfg"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
        upperPanel.canSaveLoad(false);
    }
    private static class SingletonHolder{
        public static final MainGUI HOLDER_INSTANCE = new MainGUI();
    }
    public static MainGUI getInstance(){
        return SingletonHolder.HOLDER_INSTANCE;
    }
    public void pauseSim() {
        if (habitat==null) return;
        cell.disableTimer();
        simTimer.stop();
        habitat.setRunning(false);
        pauseStart  = LocalDateTime.now();
        cell.disableTimer();
    }
    public void resumeSim() {
        if (habitat==null) return;
        Duration pauseTime = Duration.between(pauseStart, LocalDateTime.now());

        //Время, отоброжаемое в верхней панеле
        startTime = startTime.plus(pauseTime);
        for (int i = 0; i < RabbitArray.INSTANCE.size(); i++) {
            RabbitArray.INSTANCE.get(i).startMove = RabbitArray.INSTANCE.get(i).startMove.plus(pauseTime);
        }
        //Время начала симуляции увеличивается
        habitat.startTimePlus(pauseTime);

        //Запускаем симуляцию
        habitat.setRunning(true);
        simTimer.start();

        cell.enableTimer();
    }
    private void cancelSim(){
        pauseSim();
        if (displayInfo){
            if(JOptionPane.showOptionDialog(mainContainer,
                    "Прошло времени: "+upperPanel.timeLabel.getText()+" сек\nОбыкновенных кроликов: "+habitat.getRegCount()+"\nКроликов альбиносов: "+habitat.getAlbCount()+"\nЗакончить симуляцию?",
                    "Результаты симуляции", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,answers,answers[0])==0){
                habitatTimer.cancel();
                running = false;
                cell.removeAll();
                cell.revalidate();

                upperPanel.timeLabel.setText("0");

                bottomPanel.editeModeOn(true);
                bottomPanel.bStart.setEnabled(true);
                upperPanel.canSaveLoad(false);
            }
            else{
                resumeSim();
            }
            this.requestFocusInWindow();
            return;
        }
        habitatTimer.cancel();
        running = false;
        cell.removeAll();
        cell.revalidate();
        bottomPanel.editeModeOn(true);
        bottomPanel.bStart.setEnabled(true);
        bottomPanel.bStop.setEnabled(false);
        bottomPanel.bShowStat.setEnabled(false);
        upperPanel.canSaveLoad(false);
        this.requestFocusInWindow();
    }
    private void startSim(){
        cell.mapUpdateTimer.start();
        int dReg = 0, dAlb = 0;
        boolean badData = false;
        try {
            if (bottomPanel.inputDReg.getText() == null) throw new NumberFormatException();
            dReg = Integer.parseInt(bottomPanel.inputDReg.getText());
            if (dReg<1) throw new NumberFormatException("Введенное значение меньше единицы");
        }
        catch (NumberFormatException e){
            bottomPanel.inputDReg.setBackground(Color.red);
            bottomPanel.inputDReg.setText("2");
            badData = true;
        }

        try {
            if (bottomPanel.inputDAlb.getText() == null) throw new NumberFormatException();
            dAlb = Integer.parseInt(bottomPanel.inputDAlb.getText());
            if (dAlb<1) throw new NumberFormatException("Введенное значение меньше единицы");
        }
        catch (NumberFormatException e){
            bottomPanel.inputDAlb.setBackground(Color.red);
            bottomPanel.inputDAlb.setText("4");
            badData = true;
        }
        if (badData) {
            JOptionPane.showMessageDialog(this, "Неправильный формат задержки появления");
            return;
        }
        if (bottomPanel.inputRReg.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "NullPointerException в поле inputRReg");
            return;
        }
        if (bottomPanel.inputRAlb.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "NullPointerException в поле inputRAlb");
            return;
        }
        this.requestFocusInWindow();
        startTime = LocalDateTime.now();
        simTimer.restart();
        cell.removeAll();
        habitat = new Habitat(dReg, (double) ((Integer) bottomPanel.inputRReg.getSelectedItem()) / 100,
                dAlb, (double) ((Integer) bottomPanel.inputRAlb.getSelectedItem()) / 100,
                (Integer)bottomPanel.spinLTReg.getValue(), (Integer) bottomPanel.spinLTAlb.getValue(),
                (Integer) bottomPanel.priorityReg.getSelectedItem(), (Integer) bottomPanel.priorityAlb.getSelectedItem());
        habitatTimer = new java.util.Timer();
        habitatTimer.scheduleAtFixedRate(habitat, 0, 100);
        running = true;
        bottomPanel.editeModeOn(false);
        bottomPanel.bStart.setEnabled(false);
        bottomPanel.bStop.setEnabled(true);
        bottomPanel.bShowStat.setEnabled(true);
        upperPanel.canSaveLoad(true);
        cell.startRabIntelligence();
    }
    private void restartSim(){
        if (bottomPanel.bStart.isEnabled()){
            bottomPanel.bStart.setEnabled(false);
            bottomPanel.bStop.setEnabled(true);
            bottomPanel.bShowStat.setEnabled(true);
            upperPanel.canSaveLoad(true);
        }
        else {
            cell.mapUpdateTimer.start();
            cell.startRabIntelligence();
        }
        if (bottomPanel.inputRReg.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "NullPointerException в поле inputRReg");
            return;
        }
        if (bottomPanel.inputRAlb.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "NullPointerException в поле inputRAlb");
            return;
        }
        if (running) {
            habitatTimer.cancel();
            running = false;
        }
        startTime = LocalDateTime.now();
        simTimer.restart();
        cell.removeAll();
        habitat = new Habitat(Integer.parseInt(bottomPanel.inputDReg.getText()), (double) ((Integer) bottomPanel.inputRReg.getSelectedItem()) / 100,
                Integer.parseInt(bottomPanel.inputDAlb.getText()), (double) ((Integer) bottomPanel.inputRAlb.getSelectedItem()) / 100,
                (Integer) bottomPanel.spinLTReg.getValue(), (Integer) bottomPanel.spinLTAlb.getValue(),
                (Integer) bottomPanel.priorityReg.getSelectedItem(), (Integer) bottomPanel.priorityAlb.getSelectedItem());
        habitatTimer = new java.util.Timer();
        habitatTimer.scheduleAtFixedRate(habitat, 0, 100);
        running = true;
        this.requestFocusInWindow();
    }

    private void showRabbits(){
        pauseSim();
        this.requestFocusInWindow();
        RabbitDialog rabbitDialog = new RabbitDialog(this, RabbitArray.INSTANCE.getRabbitBirthTime(), callback -> {
            resumeSim();
            return null;
        });
        rabbitDialog.setVisible(true);
    }
    private void showConsole(){
        this.requestFocusInWindow();
        RabConsole.getInstance().setVisible(true);
    }
    public void dumpConfig() {
        Config.INSTANCE.set(
                new int[]{Integer.parseInt(bottomPanel.inputDReg.getText()), Integer.parseInt(bottomPanel.inputDAlb.getText())},
                new int[]{(Integer) bottomPanel.spinLTReg.getValue(), (Integer) bottomPanel.spinLTAlb.getValue()},
                new int[]{(Integer) bottomPanel.inputRReg.getSelectedItem(), (Integer) bottomPanel.inputRAlb.getSelectedItem()},
                new boolean[]{!cell.regOnPause, !cell.albOnPause},
                new int[]{(Integer) bottomPanel.priorityReg.getSelectedItem(), (Integer) bottomPanel.priorityAlb.getSelectedItem()},
                bottomPanel.showTime.isSelected(), bottomPanel.showInfo.isSelected()
        );
    }
    public void getConfig() {
        bottomPanel.inputDReg.setText(Integer.toString(Config.INSTANCE.delay[0]));
        bottomPanel.inputDAlb.setText(Integer.toString(Config.INSTANCE.delay[1]));
        bottomPanel.spinLTReg.setValue(Config.INSTANCE.lifeTime[0]);
        bottomPanel.spinLTAlb.setValue(Config.INSTANCE.lifeTime[1]);
        bottomPanel.inputRReg.setSelectedItem(Config.INSTANCE.percentage[0]);
        bottomPanel.inputRAlb.setSelectedItem(Config.INSTANCE.percentage[1]);
        cell.regOnPause = Config.INSTANCE.ai[0];
        bottomPanel.bToggleReg.doClick();
        cell.albOnPause = Config.INSTANCE.ai[1];
        bottomPanel.bToggleAlb.doClick();
        bottomPanel.priorityReg.setSelectedItem(Config.INSTANCE.priority[0]);
        bottomPanel.priorityAlb.setSelectedItem(Config.INSTANCE.priority[1]);
        if (Config.INSTANCE.upperPanel) {
            bottomPanel.showTime.doClick();
        }
        else {
            bottomPanel.hideTime.doClick();
        }
        if (Config.INSTANCE.report) {
            bottomPanel.showInfo.doClick();
        }
        else {
            bottomPanel.hideInfo.doClick();
        }
        bottomPanel.repaint();
    }
    private void save() throws Exception{
        pauseSim();
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(chooser.getSelectedFile()));
            Rabbit[] rabbits = new Rabbit[RabbitArray.INSTANCE.size()];
            for (int i = 0; i < rabbits.length; i++)
                rabbits[i] = RabbitArray.INSTANCE.get(i);
            oos.writeObject(LocalDateTime.now()); // Время сохранения кроликов
            oos.writeObject(Duration.between(habitat.getStartTime(),LocalDateTime.now())); // Продолжительность симуляции
            oos.writeObject(rabbits); // Живые кролики
            oos.flush(); // Выгружаем из буфера
            oos.close();
        }
        resumeSim();
        this.requestFocusInWindow();
    }
    private void load() throws Exception{
        pauseSim();
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(chooser.getSelectedFile()));
            LocalDateTime saveTime = (LocalDateTime) ois.readObject();
            Duration elapsedTime = (Duration) ois.readObject();
            Rabbit[] rabbits = (Rabbit[]) ois.readObject();
            RabbitArray.INSTANCE.initField(); // Закоммитить если нужно просто добавить сохраненные объекты к текущим
            for (Rabbit r:rabbits) {
                r.birthTime = Duration.between(startTime,LocalDateTime.now())/*Время, которое прошло от текущей симуляции*/
                        .minus(elapsedTime.minus(r.birthTime)); /*Время, которое кролик уже успел пожить*/
                //r.reID(); // Раскоммитить если нужно просто добавить сохраненные объекты к текущим
                r.startMove = r.startMove.plus(Duration.between(saveTime,LocalDateTime.now()));
                RabbitArray.INSTANCE.add(r);
            }
            //startTime = startTime.plus(Duration.between(saveTime,LocalDateTime.now())); // Раскоммитить для восстановления времени, сохраненного в момент сохранения (уходит в МИНУС при нескольких нажатиях)
            ois.close();
        }
        resumeSim();
        this.requestFocusInWindow();
    }
    public LocalDateTime getStartTime(){
        return startTime;
    }
}
