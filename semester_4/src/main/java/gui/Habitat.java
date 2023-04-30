package gui;

import animals.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.*;

public class Habitat extends TimerTask { //Класс gui.Habitat является наследником класса TimerTask
    //private Image regImage, albImage;
    private boolean running;
    private final int deltaReg;
    private final int deltaAlb; // Ширина, высота, задержки появлений кроликов
    private final double rateReg, percentAlb; /* Вероятность появления обыкновенного кролика и
    максимальное допустимое отношение кроликов альбиносов к общему количеству */
    private final RabbitArray rabbits = RabbitArray.INSTANCE; // Массив кроликов (поле)
    private LocalDateTime startTime; // Время - начало симуляции
    Duration regPrev, albPrev; // Времена появлений последних представителей каждого вида кроликов
    int priorityReg, priorityAlb; // Приоритеты кроликов
    Habitat(int deltaReg, double rateReg, int deltaAlb, double percentAlb,long lTimeReg,long lTimeAlb, int priorityReg, int priorityAlb) {

        RabbitAlb.lifeTime = lTimeAlb;
        RabbitReg.lifeTime = lTimeReg;
        this.rabbits.initField(/*sizeX, sizeY*/);
        this.deltaAlb = deltaAlb;
        this.deltaReg = deltaReg;
        this.rateReg = rateReg;
        this.percentAlb = percentAlb;
        this.startTime = LocalDateTime.now();
        this.regPrev = Duration.ZERO;
        this.albPrev = Duration.ZERO;
        this.priorityReg = priorityReg;
        this.priorityAlb = priorityAlb;
        running = true;
    } //Конструктор

    int getRegCount() {
        return rabbits.getRegCount();
    }
    int getAlbCount() {
        return rabbits.getAlbCount();
    }
    void update(Duration duration) { /*Метод вызывающийся по таймеру.
    Выполняет проверку необходимости появления нового кролика*/
        for(int i=0;i<rabbits.size();++i){
            if (rabbits.get(i) == null) continue;
            if (duration.minus(rabbits.get(i).birthTime).getSeconds() >= rabbits.get(i).getLifeTime()) {
                rabbits.remove(i);
            }
        }

        if (duration.minus(this.albPrev).getSeconds() >= this.deltaAlb) {
            for (int i = 0; i < duration.minus(this.albPrev).getSeconds() / this.deltaAlb; i++) {
                if (((double) rabbits.getAlbCount() / (rabbits.getRegCount() + rabbits.getAlbCount())) < percentAlb) {
                    rabbits.add(new RabbitAlb(startTime, priorityAlb));
                }
            }
            this.albPrev = duration;
        }
        if (duration.minus(this.regPrev).getSeconds() >= this.deltaReg) {
            Random rand = new Random();
            for (int i = 0; i < duration.minus(this.regPrev).getSeconds() / this.deltaReg; i++) {
                if (rand.nextDouble() <= this.rateReg) {
                    rabbits.add(new RabbitReg(startTime, priorityReg));
                }
            }
            this.regPrev = duration;
        }
    }
    @Override
    public void run() { // Переопределенный абстрактный метод run класса TimerTask
        if (running) update(Duration.between(this.startTime, LocalDateTime.now()));
    }
    public void setRunning(boolean status){
        running = status;
    }
    public void startTimePlus(TemporalAmount amount){
        startTime = startTime.plus(amount);
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
}
