package animals;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.sqrt;

public class RabbitReg extends Rabbit{
    private static final Object lock = new Object();
    private static boolean work = true, onPause = false;
    public static long lifeTime;
    static final int delDirection = 1000;
    public RabbitReg(long id, long birthTime, int x, int y) {
        super(id, birthTime,x,y);
    }
    public RabbitReg(LocalDateTime startSim, int priority){
        super(startSim, priority);
    }
    @Override
    public long getLifeTime() {
        return RabbitReg.lifeTime;
    }

    @Override
    public void move() {
        /*
        0 - left
        1 - up
        2 - right
        3 - down
        */
        if (!isFirstM && needToM) {
            curX = (int) (curX+(desX-curX)*speed * Duration.between(startMove,LocalDateTime.now()).toMillis()/ distance /1000);
            curY = (int) (curY+(desY-curY)*speed * Duration.between(startMove,LocalDateTime.now()).toMillis()/ distance /1000);
        } else isFirstM = false;
        byte side = (byte) ThreadLocalRandom.current().nextInt(0, 4);
        switch (side) {
            case 0 -> {
                desX = 0;
                desY = ThreadLocalRandom.current().nextInt(0, parentPanel.getHeight() - 40);
            }
            case 1 -> {
                desX = ThreadLocalRandom.current().nextInt(0, parentPanel.getWidth() - 40);
                desY = 0;
            }
            case 2 -> {
                desX = parentPanel.getWidth() - 40;
                desY = ThreadLocalRandom.current().nextInt(0, parentPanel.getHeight() - 40);
            }
            case 3 -> {
                desX = ThreadLocalRandom.current().nextInt(0, parentPanel.getWidth() - 40);
                desY = parentPanel.getHeight() - 40;
            }
            default -> throw new NumberFormatException();
        }

        startMove = LocalDateTime.now();
        distance = sqrt((desX-curX)*(desX-curX)+(desY-curY)*(desY-curY));
        neededT =  distance / speed * 1000;
        needToM = true;
        //System.out.println("Я не сплю!"+distance);
    }

    @Override
    public int getMoveX() {
        if (!needToM) return curX;
        long curT = Duration.between(startMove,LocalDateTime.now()).toMillis();
        if (neededT<curT){
            curX = desX;
            return curX;
        }
        return (int) (curX+(desX-curX)*speed *curT / distance/1000);
    }

    @Override
    public int getMoveY() {
        if (!needToM) return curY;
        long curT = Duration.between(startMove,LocalDateTime.now()).toMillis();
        if (neededT<curT){
            needToM = false;
            curY = desY;
            return curY;
        }
        return (int) (curY+(desY-curY)*speed *curT / distance / 1000);
    }

    public static void pause(){
        onPause = true;
    }

    public static void renew(){
        onPause = false;
        synchronized(lock){
            lock.notifyAll();
        }
    }
    @Override
    public void run() {
        while (work){
            move();
            try {
                sleep(delDirection);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            synchronized (lock) {
                while (onPause){
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
