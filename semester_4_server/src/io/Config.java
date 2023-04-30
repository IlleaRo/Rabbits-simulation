package io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public enum Config {
    INSTANCE();
    public int[] delay;
    public int[] lifeTime;
    public int[] percentage;
    public boolean[] ai;
    public int[] priority;
    public boolean upperPanel;
    public boolean report;
    Config() {
        this.delay = new int[2];
        this.lifeTime = new int[2];
        this.percentage = new int[2];
        this.ai = new boolean[2];
        this.priority = new int[2];
        this.upperPanel = true;
        this.report = true;
    }
    public void set(int[] delay, int[] lifeTime, int[] percentage, boolean[] ai, int[] priority, boolean upperPanel, boolean report) {
        this.delay = delay;
        this.lifeTime = lifeTime;
        this.percentage = percentage;
        this.ai = ai;
        this.priority = priority;
        this.upperPanel = upperPanel;
        this.report = report;
    }
    public void setStr(String[] delay, String[] lifeTime, String[] percentage, String[] ai, String[] priority, String upperPanel, String report) {
        this.delay = Arrays.stream(delay).mapToInt(Integer::parseInt).toArray();
        this.lifeTime = Arrays.stream(lifeTime).mapToInt(Integer::parseInt).toArray();
        this.percentage = Arrays.stream(percentage).mapToInt(Integer::parseInt).toArray();
        this.ai = new boolean[2];
        this.ai[0] = Boolean.parseBoolean(ai[0]);
        this.ai[1] = Boolean.parseBoolean(ai[1]);
        this.priority = Arrays.stream(priority).mapToInt(Integer::parseInt).toArray();
        this.upperPanel = Boolean.parseBoolean(upperPanel);
        this.report = Boolean.parseBoolean(report);
    }
    private static class NotConfigException extends RuntimeException {
        public NotConfigException() {
            super("Not a config file");
        }
    }
    public void read(Path path) throws IOException {
        try (Stream<String> stream = Files.lines(path)) {
            String[] lineArr = stream.toArray(String[]::new);
            if (!Objects.equals(lineArr[0], "RabbitCfg")) {
                throw new NotConfigException();
            }
            this.setStr(
                    new String[]{lineArr[1], lineArr[2]},
                    new String[]{lineArr[3], lineArr[4]},
                    new String[]{lineArr[5], lineArr[6]},
                    new String[]{lineArr[7], lineArr[8]},
                    new String[]{lineArr[9], lineArr[10]},
                    lineArr[11], lineArr[12]);
        }
    }
    public void write(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        Files.writeString(path, "RabbitCfg\n");
        Files.writeString(path, this.delay[0] +"\n"+ this.delay[1]+"\n", StandardOpenOption.APPEND);
        Files.writeString(path, this.lifeTime[0] +"\n"+ this.lifeTime[1]+"\n", StandardOpenOption.APPEND);
        Files.writeString(path, this.percentage[0] +"\n"+ this.percentage[1]+"\n", StandardOpenOption.APPEND);
        Files.writeString(path, this.ai[0] +"\n"+ this.ai[1]+"\n", StandardOpenOption.APPEND);
        Files.writeString(path, this.priority[0] +"\n"+ this.priority[1]+"\n", StandardOpenOption.APPEND);
        Files.writeString(path, this.upperPanel+"\n", StandardOpenOption.APPEND);
        Files.writeString(path, this.report+"\n", StandardOpenOption.APPEND);
    }
    private ByteBuffer intToBytes(int number) {
        return ByteBuffer.allocate(4).putInt(number);
    }
    private byte boolsToByte (boolean[] bools) {
        byte result = 0;
        for (int i = 0; i < 4; i++) {
            result |= 1 << i;
        }
        return result;
    }
    private boolean[] byteToBools (byte input) {
        boolean[] result = new boolean[4];
        for (int i = 0; i < 4; i++) {
            result[i] = (((input >> i) & 1) == 1);
        }
        return result;
    }
    public ByteBuffer toBytes() {
        ByteBuffer result = ByteBuffer.allocate(33);
        for (int i = 0; i < 2; i++) {
            result.put(i*4, intToBytes(delay[i]).array());
            result.put((i+1)*4, intToBytes(lifeTime[i]).array());
            result.put((i+2)*4, intToBytes(percentage[i]).array());
            result.put((i+3)*4, intToBytes(priority[i]).array());
        }
        result.put(32, boolsToByte(new boolean[]{ai[0], ai[1], upperPanel, report}));
        return result;
    }
    public void fromBytes(ByteBuffer bytes) {
        for (int i = 0; i < 2; i++) {
            delay[i] = bytes.getInt(i*4);
            lifeTime[i] = bytes.getInt((i+1)*4);
            percentage[i] = bytes.getInt((i+2)*4);
            priority[i] = bytes.getInt((i+3)*4);
        }
        boolean[] bools = byteToBools(bytes.get(32));
        ai[0] = bools[0];
        ai[1] = bools[1];
        upperPanel = bools[2];
        report = bools[3];
    }
}
