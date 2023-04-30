package animals;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.abs;

public class RabbitAlb extends Rabbit{
    private static boolean onPause = false;
    public static long lifeTime;
    private boolean isMoveToLeft;
    public RabbitAlb(LocalDateTime startSim, int priority){
        super(startSim, priority);
        isMoveToLeft = ThreadLocalRandom.current().nextBoolean();
        move();
    }
    public RabbitAlb(long id, long birthTime, int x, int y) {
        super(id, birthTime,x,y);
    }
    @Override
    public long getLifeTime() {
        return RabbitAlb.lifeTime;
    }

    @Override
    public void move() {

        if (!isFirstM) {
            curX = (int) (curX+(desX-curX)*speed * Duration.between(startMove,LocalDateTime.now()).toMillis()/ distance /1000);
        } else isFirstM = false;

        if (isMoveToLeft){
            desX = parentPanel.getWidth()-40;
            isMoveToLeft = false;
        }
        else{
            desX = 0;
            isMoveToLeft = true;
        }
        startMove = LocalDateTime.now();
        distance = abs(desX-curX);
        neededT =  distance / speed * 1000;
        needToM = true;
    }
    public static void pause(){
        onPause = true;
    }
    public static void renew(){
        onPause = false;
    }
    @Override
    public int getMoveX() {
        if (!needToM) {
            if (!onPause) move();
            return curX;
        }
        long curT = Duration.between(startMove,LocalDateTime.now()).toMillis();
        if (neededT<curT){
            needToM = false;
            curX = desX;
            return curX;
        }
        return (int) (curX+(desX-curX)*speed *curT / distance/1000);
    }
}

