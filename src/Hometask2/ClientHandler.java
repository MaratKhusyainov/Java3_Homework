package Hometask2;

import Hometask2.Multiscene.ChatSceneApp;
import Hometask2.Multiscene.SceneFlow;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

//обработчик сообщений клиента
public class ClientHandler {
    private MyServer myServer;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String name;
    private String userLogin;

    public String getName() {
        return name;
    }

    public ClientHandler(MyServer myServer, Socket socket) {
        this.myServer = myServer;
        this.socket = socket;
        name = "";
        try {
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    authenticate();
                    readMessages();
                } catch (IOException | SQLException ex) {
                    ex.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
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
        System.out.println(name + " left");
        System.out.println("Socket with [" + name + "] is closed: " + socket.isClosed());
    }

    private void readMessages() throws IOException, SQLException {
        while (true) {
            if (in.available() > 0) {
                String message = in.readUTF();
                System.out.println("From " + name + ":" + message);
                if (message.equals("/end")) {

                    out.writeUTF("/end");
                    return;
                } else if (message.startsWith("/login")) {
                    String[] parts = message.split("\\s");
                    userLogin = parts[1];
                }

                //если пользователь хочет сменить nick:
                else if (message.startsWith("/changeNickTo")) {
                    String[] parts = message.split("\\s");
                    String newNick = parts[1];
                    myServer.notifyFriends(name, newNick);
                    name = newNick;
                    out.writeUTF("/nickOk " + newNick);
                    myServer.broadcastClientsList();
                    AuthService.changeNickInDB(userLogin, newNick);
                } else if (message.startsWith("/w ")) {
                    String[] parts = message.split("\\s");
                    String recipientName = parts[1];
                    String[] mess = new String[parts.length - 2];
                    System.arraycopy(parts, 2, mess, 0, parts.length - 2);
                    message = String.join(" ", mess);
                    myServer.sendDirect(this, recipientName, message);
                } else myServer.broadcast(name + ": " + message);

            }
        }
    }

    private void authenticate() throws IOException {
        while (true) {
            if (in.available() > 0) {
                String str = in.readUTF();
                if (str.startsWith("/auth")) {
                    String[] parts = str.split("\\s");
                    //ищем nick в БД:
                    String nick = myServer.getAuthService().getNickByLoginAndPwd(parts[1], parts[2]);
                    if (nick != null) {
                        if (!myServer.isNickLogged(nick)) {
                            System.out.println(nick + " logged into chat");
                            name = nick;
                            sendMsg("/authOk " + nick);
                            myServer.broadcast(nick + " is in chat");
                            myServer.subscribe(this);
                            sendMsg("To change your nick write '/changeNickTo [nick]'");
                            return;
                        } else {
                            //добавила возможность делать re-enter
                            System.out.println("User " + nick + " tried to re-enter");
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
                        System.out.println("Wrong login/password");
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
