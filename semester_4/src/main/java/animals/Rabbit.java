package animals;

import javax.swing.*;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Rabbit extends BaseAI implements Serializable {

    public static JPanel parentPanel;
    public Long ID;
    public Duration birthTime;
    public Rabbit(LocalDateTime startSim, int priority) {
        curX = ThreadLocalRandom.current().nextInt(0, parentPanel.getWidth() - 40);
        curY = ThreadLocalRandom.current().nextInt(0, parentPanel.getHeight() - 40);
        birthTime = Duration.between(startSim, LocalDateTime.now());
        ID = System.currentTimeMillis();
        this.setPriority(priority);
    }
    public Rabbit(long id, long birthTime, int x, int y) {
        this.ID = id;
        this.birthTime = Duration.ofMillis(birthTime);
        this.curX = x;
        this.curY = y;
    }
    public abstract long getLifeTime();
    public void reID(){
        ID = System.currentTimeMillis();
    }

}
