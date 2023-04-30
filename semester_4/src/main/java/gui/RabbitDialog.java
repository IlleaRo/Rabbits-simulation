package gui;

import animals.Rabbit;
import animals.RabbitReg;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

class RabbitDialog extends JDialog {
    RabbitDialog(JFrame frame,HashMap<Duration, Rabbit> rabbits, Function<Void, Void> callback){
        //Настройки окна
        super(frame,"Кролики",true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(350,400));
        setLocationRelativeTo(null);


        //Заполнение содержимым модели таблицы
        DefaultTableModel tableModel = new DefaultTableModel();
        String[] columnNames = {"Время рождения", "Тип кролика"};
        tableModel.setColumnIdentifiers(columnNames);
        for (Map.Entry<Duration,Rabbit> entry:rabbits.entrySet())
            tableModel.addRow(new Object[]{entry.getKey().toSeconds(),entry.getValue() instanceof RabbitReg ? "Обыкновенный":"Альбинос"});

        //Создание таблицы по мадели
        JTable table = new JTable(tableModel);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.setAutoCreateRowSorter(true);

        //Отрисовка таблицы
        this.pack();
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent windowEvent) {
            }

            @Override
            public void windowClosing(WindowEvent windowEvent) {

            }

            @Override
            public void windowClosed(WindowEvent windowEvent) {
                callback.apply(null);
            }

            @Override
            public void windowIconified(WindowEvent windowEvent) {

            }

            @Override
            public void windowDeiconified(WindowEvent windowEvent) {

            }

            @Override
            public void windowActivated(WindowEvent windowEvent) {

            }

            @Override
            public void windowDeactivated(WindowEvent windowEvent) {

            }
        });
    }
}