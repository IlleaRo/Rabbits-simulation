package animals;

import java.time.Duration;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Vector;

public enum RabbitArray{
    INSTANCE();
    private final Vector<Rabbit> rabbits = new Vector<>();
    private final TreeSet<Long> rabbitIds = new TreeSet<>();
    private final HashMap<Duration, Rabbit> rabbitBirthTime = new HashMap<>();
    private int regCount, albCount;
    public void initField() {
        this.regCount = 0;
        this.albCount = 0;
        if (rabbits.size() > 0) {
            this.rabbits.clear();
            this.rabbitBirthTime.clear();
            this.rabbitIds.clear();
        }
    }
    public Rabbit get(int index) {
        return this.rabbits.get(index);
    }
    public Rabbit get(Duration birthTime) {
        return rabbitBirthTime.get(birthTime);
    }
    public boolean hasId(long id) {
        return this.rabbitIds.contains(id);
    }
    public int indexOf(Rabbit rabbit) {
        return rabbits.indexOf(rabbit);
    }
    public void add(Rabbit rabbit) {
        rabbit.start();
        this.rabbits.add(rabbit);
        this.rabbitIds.add(rabbit.ID);
        this.rabbitBirthTime.put(rabbit.birthTime, rabbit);
        if (rabbit instanceof RabbitAlb) {
            this.albCount++;
        }
        else {
            this.regCount++;
        }
    }
    public int getRegCount() {
        return regCount;
    }
    public int getAlbCount() {
        return albCount;
    }

    public int size(){
        return rabbits.size();
    }
    public void remove(int index) {
        if (index >= rabbits.size()) throw new IndexOutOfBoundsException();
        rabbitIds.remove(rabbits.get(index).ID);
        rabbitBirthTime.remove(rabbits.get(index).birthTime);
        if (rabbits.get(index) instanceof RabbitReg) regCount--;
        else albCount--;
        rabbits.remove(index);
    }

    public HashMap<Duration,Rabbit> getRabbitBirthTime(){
        return rabbitBirthTime;
    }
}
