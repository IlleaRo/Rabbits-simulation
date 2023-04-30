package io;

import animals.Rabbit;
import animals.RabbitAlb;
import animals.RabbitArray;
import animals.RabbitReg;
import gui.MainGUI;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Properties;

public class DB {
    private static final String url = "jdbc:postgresql://194.87.62.78:5432/rabbits";
    private static final String user = "jdbc";

    private DB() {}

    private static boolean checkTable(Connection dbConn) throws SQLException {
        Statement statement = dbConn.createStatement();
        ResultSet result = statement.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema='public';");
        boolean tableExists = false;
        while (result.next()) {
            if (result.getString(1).equals("rabbits")) {
                tableExists = true;
            }
        }
        if (tableExists) {
            result.close();
            statement.close();
            return true;
        }
        statement.execute("CREATE TABLE rabbits(id bigint, birthtime bigint, x int, y int, type char(3))");
        result.close();
        statement.close();
        return false;
    }
    private static Connection getConnection() throws SQLException {
        Properties dbProps = new Properties();
        dbProps.setProperty("user", user);
        dbProps.setProperty("password", System.getenv("POSTGRESQL_PASS"));
        dbProps.setProperty("ssl", "false");
        return DriverManager.getConnection(url, dbProps);
    }
    public static boolean writeDB(String type) {
        MainGUI.getInstance().pauseSim();
        try {
            Connection dbConn = getConnection();
            checkTable(dbConn);
            for (int i = 0; i < RabbitArray.INSTANCE.size(); i++) {
                Statement statement = dbConn.createStatement();
                Rabbit rabbit = RabbitArray.INSTANCE.get(i);
                String className = rabbit.getClass().getName().toLowerCase();
                String rabbitType = className.substring(className.length()-3);
                if (!type.equals(rabbitType) && !type.equals("all")) {
                    continue;
                }
                ResultSet existing = statement.executeQuery("SELECT 1 FROM rabbits WHERE id ="+rabbit.ID+";");
                if (existing.next()) {
                    continue;
                }
                String request = "INSERT INTO rabbits (id, birthtime, x, y, type) VALUES ("
                        +rabbit.ID+","
                        +Duration.between(MainGUI.getInstance().habitat.getStartTime(), LocalDateTime.now())
                            .minus(rabbit.birthTime).toMillis()+","
                        +rabbit.getMoveX()+","
                        +rabbit.getMoveY()+","
                        +"'"+rabbitType+"');";
                statement.execute(request);
                statement.close();
            }
            dbConn.close();
            MainGUI.getInstance().resumeSim();
            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
            MainGUI.getInstance().resumeSim();
            return false;
        }
    }
    public static boolean readDB(String type) {
        MainGUI.getInstance().pauseSim();
        try {
            Connection dbConn = getConnection();
            if (!checkTable(dbConn)){
                MainGUI.getInstance().resumeSim();
                return true;
            }
            Statement statement = dbConn.createStatement();
            ResultSet rabbits = statement.executeQuery("SELECT * FROM rabbits;");
            while (rabbits.next()) {
                String rabbitType = rabbits.getString("type");
                if (!type.equals(rabbitType) && !type.equals("all")) {
                    continue;
                }
                long id = rabbits.getLong("id");
                if (RabbitArray.INSTANCE.hasId(id)) {
                    continue;
                }
                long birthTime = Duration.between(MainGUI.getInstance().getStartTime(), LocalDateTime.now())
                        .minus(Duration.ofMillis(rabbits.getLong("birthtime"))).toMillis();
                int x = rabbits.getInt("x");
                int y = rabbits.getInt("y");
                Rabbit readRabbit;
                if (rabbitType.equals("alb")) {
                    readRabbit = new RabbitAlb(id, birthTime, x, y);
                }
                else {
                    readRabbit = new RabbitReg(id, birthTime, x, y);
                }
                RabbitArray.INSTANCE.add(readRabbit);
            }
            dbConn.close();
            MainGUI.getInstance().resumeSim();
            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        MainGUI.getInstance().resumeSim();
        return false;
    }
    public static boolean wipeDB() {
        MainGUI.getInstance().pauseSim();
        try {
            Connection dbConn = getConnection();
            dbConn.createStatement().execute("DROP TABLE rabbits;");
            dbConn.close();
            MainGUI.getInstance().resumeSim();
            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        MainGUI.getInstance().resumeSim();
        return false;
    }
}
