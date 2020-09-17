package Hometask6.Chat;


import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

//обработчик сообщений клиента
public class ClientHandler implements Runnable{
    private MyServer myServer;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String name;
    private String userLogin;

    public  String getName() {
        return name;
    }

    public ClientHandler(MyServer myServer, Socket socket) {
        this.myServer = myServer;
        this.socket = socket;
        name = "";
    }

    private static final Logger log = Logger.getLogger(ClientHandler.class);

    @Override
    public void run(){
        log.info("NEW CLIENT on " + Thread.currentThread());
        try {
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
//            new Thread(()-> {
            try {
                authenticate();
                readMessages();
            } catch (IOException | SQLException ex) {
                ex.printStackTrace();
            } finally {
                closeConnection();
            }
//            }).start();
        } catch (IOException ex) {
            throw new RuntimeException("Client creation error");
        }
    }

    private void closeConnection() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        myServer.unsubscribe(this);
        myServer.broadcast("User " + name + " left");
        log.info(name + " left");
        log.info("Socket with [" + name + "] is closed: " + socket.isClosed());
    }

    // Читаем сообщения от пользователя:
    private void readMessages() throws IOException, SQLException {
        while (true) {
            if (in.available()>0) {
                String message = in.readUTF();
                log.info("From " + name + ":" + message + "  " + Thread.currentThread());
                if (message.equals("/end")) {

                    out.writeUTF("/end");
                    return;
                }
                else if(message.startsWith("/login")){
                    String[] parts = message.split("\\s");
                    userLogin = parts[1];
                }

                //если пользователь хочет сменить nick:
                else if (message.startsWith("/changeNickTo")){
                    String[] parts = message.split("\\s");
                    String newNick = parts[1];
                    myServer.notifyFriends(name, newNick);
                    name = newNick;
                    out.writeUTF("/nickOk " + newNick);
                    myServer.broadcastClientsList();
                    AuthService.changeNickInDB(userLogin, newNick);
                }

                else if (message.startsWith("/w ")) {
                    String[] parts = message.split("\\s");
                    String recipientName = parts[1];
                    String [] mess = new String[parts.length-2];
                    System.arraycopy(parts, 2, mess , 0, parts.length - 2);
                    // Проверка на запрещенные слова и цензура:
                    String[] censoredMessage = Censorship.censor(mess);
                    message = Censorship.joinString(censoredMessage);
                    myServer.sendDirect(this, recipientName, message);
                }

                else {
                    String [] mess = message.split("\\s");
                    // Проверка на запрещенные слова и цензура:
                    String[] censoredMessage = Censorship.censor(mess);
                    message = Censorship.joinString(censoredMessage);
                    myServer.broadcast(name + ": " + message);
                }

            }
        }
    }

    private void authenticate() throws IOException {
        while(true) {
            if (in.available()>0){
                String str = in.readUTF();
                if (str.startsWith("/auth")) {
                    String[] parts = str.split("\\s");
                    //ищем nick в БД:
                    String nick = myServer.getAuthService().getNickByLoginAndPwd(parts[1], parts[2]);
                    if (nick != null) {
                        if (!myServer.isNickLogged(nick)) {
                            log.info(nick + " logged into chat");
                            name = nick;
                            sendMsg("/authOk " + nick);
                            myServer.broadcast(nick + " is in chat");
                            myServer.subscribe(this);
                            sendMsg("To change your nick write '/changeNickTo [nick]'");
                            return;
                        } else {
                            //добавила возможность делать re-enter
                            log.info("User " + nick + " tried to re-enter");
                            sendMsg("User already logged in");
                            name = nick;
                            sendMsg("/authOk " + nick);
                            myServer.broadcast(nick + " is in chat");
                            //удаляем старые данные из списка clients сервера:
                            myServer.unsubscribeWithoutBroadcast(name);
                            //заново логинимся (добавляем клиента в список clients):
                            myServer.subscribe(this);

                            return;
                        }
                    } else {
                        log.info("Wrong login/password");
                        sendMsg("Incorrect login attempted");
                    }
                }
            }
        }
    }

    public void sendMsg(String s) {
        try {
            out.writeUTF(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
