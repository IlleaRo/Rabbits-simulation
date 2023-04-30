package gui;

import animals.Rabbit;
import animals.RabbitAlb;
import animals.RabbitArray;
import animals.RabbitReg;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

class Cell extends JPanel implements ActionListener {
    boolean albOnPause=false, regOnPause=false;
    private final JFrame frame;
    Timer mapUpdateTimer = new Timer(100,this);
    private Image map, imgRB, imgRW;
    Cell(JFrame frame){
        this.frame = frame;
        try {
            map = ImageIO.read(new File("img/field.jpg"));
            imgRB = ImageIO.read(new File("img/black.png"));
            imgRW = ImageIO.read(new File("img/white.png"));
        }
        catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
        setBackground(Color.gray);
    }

    @Override
    public void paint(Graphics g){
        g.drawImage(map,0,0,getWidth(),getHeight(),null);
        for (int i = 0; i<RabbitArray.INSTANCE.size();++i) {
            Rabbit tRab = RabbitArray.INSTANCE.get(i);
            g.drawImage(tRab instanceof RabbitReg? imgRB:imgRW,tRab.getMoveX(),tRab.getMoveY(),40,40,null);
        }
    }
    @Override
    public void actionPerformed(ActionEvent e){ repaint();}
    public void enableTimer(){
        mapUpdateTimer.start();
        RabbitReg.renew();
        RabbitAlb.renew();
    }
    public void disableTimer(){
        mapUpdateTimer.stop();
        RabbitReg.pause();
        RabbitAlb.pause();
    }

    public void startRabIntelligence(){
        if (!regOnPause) RabbitReg.renew();
        if (!albOnPause) RabbitAlb.renew();
    }
}
