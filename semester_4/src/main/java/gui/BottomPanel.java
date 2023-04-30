package gui;

import io.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Vector;

class BottomPanel extends JPanel {
    private final int stepL=5, maxL = 100, minL = 5, currentL = 10;
    final JSpinner spinLTReg = new JSpinner(new SpinnerNumberModel(currentL,minL,maxL,stepL));
    final JSpinner spinLTAlb = new JSpinner(new SpinnerNumberModel(currentL,minL,maxL,stepL));
    final JTextArea inputDReg = new JTextArea("2");
    final JTextArea inputDAlb = new JTextArea("4");
    final JButton bStart,bStop,bShowStat, bToggleAlb, bToggleReg;
    final Vector<Integer> percents = new Vector<>(Arrays.asList(10,20,30,40,50,60,70,80,90,100));
    final JComboBox<Integer> inputRReg = new JComboBox<>(percents);
    final JComboBox<Integer> inputRAlb = new JComboBox<>(percents);
    final Vector<Integer> priority = new Vector<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    final JComboBox<Integer> priorityReg = new JComboBox<>(priority);
    final JComboBox<Integer> priorityAlb = new JComboBox<>(priority);
    final JRadioButton showTime, hideTime, showInfo, hideInfo;
    BottomPanel(){
        super(new GridBagLayout());

        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints constraints = new GridBagConstraints();

        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        bStart = new JButton("Старт");
        bStop = new JButton("Стоп");
        bShowStat = new JButton("Текущие объекты");
        bToggleAlb = new JButton("Вкл");
        bToggleReg = new JButton("Вкл");
        bToggleReg.setBackground(Color.green);
        bToggleAlb.setBackground(Color.green);

        ButtonGroup timeButtonGroup = new ButtonGroup();
        showTime = new JRadioButton("Показывать время симуляции");
        hideTime = new JRadioButton("Скрывать время симуляции");
        timeButtonGroup.add(showTime);
        timeButtonGroup.add(hideTime);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.insets = new Insets(1,5,1,5);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.CENTER;

        //Панель с кнопками
        buttonsPanel.add(bStart,constraints);
        constraints.gridx = 1;
        buttonsPanel.add(bShowStat,constraints);
        constraints.gridx = 2;
        buttonsPanel.add(bStop,constraints);
        bStart.setBackground(Color.green);
        bStop.setBackground(Color.red);

        ButtonGroup infoButtonGroup = new ButtonGroup();
        showInfo = new JRadioButton("Показывать отчет симуляции");
        hideInfo = new JRadioButton("Скрывать отчет симуляции");
        infoButtonGroup.add(showInfo);
        infoButtonGroup.add(hideInfo);

        //Общие настройки таблицы
        constraints.weightx = 0;
        constraints.weighty = 0;

        //Первая строка - заголовки
        constraints.gridy = 0;
        constraints.gridx = 1;
        add(new JLabel("Обычный кролик"),constraints);
        constraints.gridx = 2;
        add(new JLabel("Белый кролик"),constraints);

        //Вторая строка - задержка появления кролика
        constraints.gridx = 0;
        constraints.gridy = 1;
        add(new JLabel("Задержка появления кролика (сек)"), constraints);
        constraints.gridx = 1;
        add(inputDReg, constraints);
        constraints.gridx = 2;
        add(inputDAlb,constraints);

        //Третья строка - время жизни кролика
        constraints.gridx = 0;
        constraints.gridy = 2;
        add(new JLabel("Время жизни кролика"),constraints);
        constraints.gridx = 1;
        add(spinLTReg,constraints);
        constraints.gridx = 2;
        add(spinLTAlb,constraints);

        //Четвертая строка - вероятность появления кролика
        constraints.gridx = 0;
        constraints.gridy = 3;
        add(new JLabel("Вероятность появления кролика (%)"), constraints);
        constraints.gridx = 1;
        add(inputRReg,constraints);

        //Пятая строка - соотношения кроликов к общему количеству
        constraints.gridx = 0;
        constraints.gridy = 4;
        add(new JLabel("Допустимое отношение кроликов к общему количеству (%)"),
                constraints);
        constraints.gridx = 2;
        add(inputRAlb,constraints);

        /*//Шестая строка - разделитель
        constraints.gridx = 1;
        constraints.gridy = 5;
        add(new JTextArea("Чепуха"));
        constraints.gridx = 1;
        add(new JLabel("gridx =1"));
*/
        //Седьмая строка - интеллект
        constraints.gridx = 0;
        constraints.gridy = 6;
        add(new JLabel("Интеллект"), constraints);
        constraints.gridx = 1;
        add(bToggleReg,constraints);
        constraints.gridx = 2;
        add(bToggleAlb,constraints);

        //Восьмая строка - Приоритет
        constraints.gridx = 0;
        constraints.gridy = 7;
        add(new JLabel("Приоритет"), constraints);
        constraints.gridx = 1;
        add(priorityReg,constraints);
        constraints.gridx = 2;
        add(priorityAlb,constraints);

        //Девятая строка - Время симуляции
        constraints.gridx = 0;
        constraints.gridy = 8;
        add(new JLabel("Верхняя панель"),constraints);
        constraints.gridx = 1;
        add(showTime,constraints);
        constraints.gridx = 2;
        add(hideTime,constraints);

        //Десятая строка - отчет симуляции
        constraints.gridx = 0;
        constraints.gridy = 9;
        add(new JLabel("Отчет симуляции"),constraints);
        constraints.gridx = 1;
        add(showInfo,constraints);
        constraints.gridx = 2;
        add(hideInfo,constraints);

        //Одиннадцатая строка - кнопки: старт и стоп
        constraints.gridx = 0;
        constraints.gridy = 10;
        constraints.gridwidth = 3;
        add(buttonsPanel,constraints);



        inputDAlb.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                inputDAlb.setBackground(Color.WHITE);
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });
        inputDReg.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                inputDReg.setBackground(Color.WHITE);
            }

            @Override
            public void focusLost(FocusEvent e){
            }
        });
    }

    void editeModeOn(boolean state){
        if (!state){
            inputDAlb.setEnabled(false);
            inputDReg.setEnabled(false);
            inputRAlb.setEnabled(false);
            spinLTAlb.setEnabled(false);
            spinLTReg.setEnabled(false);
            inputRReg.setEnabled(false);
            bStart.setEnabled(false);
            priorityReg.setEnabled(false);
            priorityAlb.setEnabled(false);
            bStop.setEnabled(true);
            bShowStat.setEnabled(true);
        }else {
            inputDAlb.setEnabled(true);
            inputDReg.setEnabled(true);
            inputRAlb.setEnabled(true);
            inputRReg.setEnabled(true);
            spinLTAlb.setEnabled(true);
            spinLTReg.setEnabled(true);
            bStart.setEnabled(true);
            priorityReg.setEnabled(true);
            priorityAlb.setEnabled(true);
            bStop.setEnabled(false);
            bShowStat.setEnabled(false);
        }
    }
}