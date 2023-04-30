package gui;

import javax.swing.*;
import java.awt.*;

class UpperPanel extends JPanel {
    final JLabel timeLabel = new JLabel("0");
    final JButton bSave = new JButton("Сохранить");
    final JButton bLoad = new JButton("Загрузить");
    final JButton bConsole = new JButton("Консоль");
    UpperPanel(){
        super(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0; constraints.gridy = 0;
        constraints.insets = new Insets(2,5,2,5);
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.weightx = 1;
        constraints.weighty = 1;

        JPanel buttonsPanelLeft = new JPanel(new GridBagLayout());
        JPanel timePanel = new JPanel(new GridBagLayout());
        JLabel textLabel = new JLabel("Прошло времени:");
        textLabel.setFont(new Font("Comic Sans", Font.ITALIC, 18));
        timeLabel.setFont(new Font("Comic Sans", Font.ITALIC, 18));


        buttonsPanelLeft.add(bSave,constraints);
        timePanel.add(textLabel,constraints);

        constraints.gridx = 1; constraints.anchor = GridBagConstraints.WEST;
        buttonsPanelLeft.add(bLoad,constraints);
        timePanel.add(timeLabel,constraints);

        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 0;
        add(buttonsPanelLeft,constraints);

        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 1;
        add(timePanel,constraints);

        constraints.anchor = GridBagConstraints.EAST;
        constraints.gridx = 2;
        add(bConsole,constraints);
    }
    void canSaveLoad(boolean enabled){
        bSave.setEnabled(enabled);
        bLoad.setEnabled(enabled);
    }
}
