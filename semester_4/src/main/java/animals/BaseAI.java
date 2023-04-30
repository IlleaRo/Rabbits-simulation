package animals;


import java.io.Serializable;
import java.time.LocalDateTime;

abstract class BaseAI extends Thread implements Serializable {
    protected boolean needToM = false; // Необходимость передвигать кролика
    protected boolean isFirstM = true;
    protected double distance; // Дистанция в пикселях, которую необходимо преодолеть кролику
    protected double neededT; // Время, необходимое для преодоление дистанции
    protected int desX, desY; // Желаемые координаты кролика
    protected int curX, curY; // Текущие координаты кролика
    public LocalDateTime startMove = LocalDateTime.now();
    public final static int speed = 80; // Скорость передвижения кролика в пикс/с
    protected abstract void move();

    public int getMoveX(){
        return curX;
    }
    public int getMoveY(){
        return curY;
    }
}
