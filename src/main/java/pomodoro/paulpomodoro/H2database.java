package pomodoro.paulpomodoro;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class H2database {

    private static Connection connection = null;
    private static Statement statement = null;

    private static final String url = "jdbc:h2:~/test";
    private static final String user = "sa";
    private static final String password = "";

    //Получить из таблицы весь список задач
    public static ObservableList<Tasks> selectAllTasks() {

        ObservableList<Tasks> tasks = FXCollections.observableArrayList();

        try {
            //Создаём соединение
            connection = DriverManager.getConnection(url, user,  password);
            System.out.println("Connection established...");
            System.out.println("Get all tasks from table 'TASKS'...");

            //Этот интерфейс используется для доступа к БД для общих целей.
            statement = connection.createStatement();

            String sql = "SELECT * FROM tasks";

            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String task = resultSet.getString("TASK");
                tasks.add(new Tasks(id, task));

            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                connection.close();
                statement.close();
                System.out.println("Connection close...");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return tasks;
    }


    public static void insertTask(String task) {
        try {
            //Создаём соединение
            connection = DriverManager.getConnection(url, user,  password);
            System.out.println("Connection established...");
            System.out.println("Insert task...");

            //Этот интерфейс используется для доступа к БД для общих целей.
            statement = connection.createStatement();

            String sql = "INSERT INTO tasks (task) VALUES('" + task + "');";

            statement.executeUpdate(sql);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                connection.close();
                statement.close();
                System.out.println("Connection close...");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



    public static void deleteTask(int id) {
        try {
            //Создаём соединение
            connection = DriverManager.getConnection(url, user,  password);
            System.out.println("Connection established...");
            System.out.println("Delete task...");

            //Этот интерфейс используется для доступа к БД для общих целей.
            statement = connection.createStatement();

            String sql = "DELETE FROM tasks WHERE id = '" + id + "';";

            statement.executeUpdate(sql);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                connection.close();
                statement.close();
                System.out.println("Connection close...");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



    //создаем таблицу TASKS если та не существует
    public static void createTableIfNotExist() {
        try {
            //Создаём соединение
            connection = DriverManager.getConnection(url, user,  password);
            System.out.println("Connection established...");
            System.out.println("Try to create table 'TASKS'...");

            //Этот интерфейс используется для доступа к БД для общих целей.
            statement = connection.createStatement();

            String sql = "create table if not exists tasks(id int auto_increment, task varchar(255));";

            statement.executeUpdate(sql);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            try {
                connection.close();
                statement.close();
                System.out.println("Connection close...");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }







}
