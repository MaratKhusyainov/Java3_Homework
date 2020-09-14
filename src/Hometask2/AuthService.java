//1. Добавить в сетевой чат авторизацию через базу данных MySQL (SQLite).

package Hometask2;

import java.sql.*;
import java.util.Properties;

public class AuthService {
    public static Connection conn;
    public static Statement statmt;
    public static ResultSet resSet;


    private class User {
        private String login;
        private String passwd;
        private String nick;


        public User(String login, String passwd, String nick) {
            this.login = login;
            this.passwd = passwd;
            this.nick = nick;
        }
    }

    public AuthService()throws SQLException, ClassNotFoundException {
        getConn();
        createTable();
//        writeDB();
    }

    // --------ПОДКЛЮЧЕНИЕ К БАЗЕ ДАННЫХ--------
    public void getConn () throws ClassNotFoundException, SQLException
    {
        conn = null;
        Properties prop = new Properties();
        prop.setProperty("useSSL", "false");
        prop.setProperty("serverTimezone", "Europe/Moscow");
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://root:root@localhost:3306/chat_db", prop);
        System.out.println("База Подключена!");
    }

    // --------Создание таблицы--------
    public void createTable () throws ClassNotFoundException, SQLException
    {
        statmt = conn.createStatement();
        statmt.execute("CREATE TABLE if not exists users \n" +
                "(id INTEGER PRIMARY KEY AUTO_INCREMENT,\n" +
                " login VARCHAR(50) unique,\n" +
                " pass VARCHAR(50), \n" +
                " nick VARCHAR(50));");

        System.out.println("Таблица создана или уже существует.");
    }

    // Заполнение таблицы (Первоначальное заполнение таблицы - используется 1 раз - для чата без возможности регистрации)
    public void writeDB () throws SQLException {
        statmt.execute("INSERT INTO users (login, pass, nick) VALUES ('login1', 'pass1', 'nick1'); ");
        statmt.execute("INSERT INTO users (login, pass, nick) VALUES ('login2', 'pass2', 'nick2'); ");
        statmt.execute("INSERT INTO users (login, pass, nick) VALUES ('login3', 'pass3', 'nick3'); ");

        System.out.println("Таблица заполнена");
    }

    public void start() {
        System.out.println("Authentication service started");
    }

    public String getNickByLoginAndPwd(String login, String passwd) {
        try {
            String sqlCommand = String.format("SELECT nick FROM users WHERE login = '%s' AND pass = '%s'", login, passwd);

            resSet = statmt.executeQuery(sqlCommand);
            if (resSet != null){
                resSet.next();  // передвигаем курсор на первую строку
                String nick = resSet.getString("nick");
                return nick;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    // Изменение ника пользователя в таблице
    public static void changeNickInDB(String login, String newNick) throws SQLException {
        String sqlCommand = String.format("UPDATE users SET nick = '%s' WHERE login = '%s'", newNick, login);
        statmt.execute(sqlCommand);
    }


    // --------Закрытие--------
    public void stop() throws ClassNotFoundException {
        try {
            resSet.close();
            statmt.close();
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println("Соединения закрыты");
        System.out.println("Authentication service stopped");
    }
}
