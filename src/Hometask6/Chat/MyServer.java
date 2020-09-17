package Hometask6.Chat;



import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyServer {
    private final int PORT = 8189;
    private List<ClientHandler> clients;
    private AuthService authService;
    // Создаем пул потоков = 2
    static ExecutorService executeIt = Executors.newFixedThreadPool(2);

    public AuthService getAuthService() {
        return authService;
    }

    private static final Logger log = Logger.getLogger(MyServer.class);

    // Создаем сокет сервера, authService (подключается к БД), сервер ждет подключений, при подключении
    // создается обработчик клиентских сообщений ClientHandler:
    public MyServer() {
        try (ServerSocket server = new ServerSocket(PORT)) {
            authService = new AuthService();
            authService.start();
            clients = new ArrayList<>();
            while (true) {
                log.info("Server awaits clients");
                Socket socket = server.accept();
                log.info("Client connected");
                // Сервер сможет одновременн обрабатывать тоько 2-х клиентов, т.к. пул = 2:
                executeIt.execute(new ClientHandler(this, socket));
//                new ClientHandler(this, socket);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            log.error("Server error");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (authService != null) {
                try {
                    authService.stop();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            // Закрываем ExecutorService:
            executeIt.shutdown();
        }
    }


    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastClientsList();
    }

    //удаляем данные повторно входящего клиента из списка clients (чтобы потом сделать subscribe с новыми данными):
    public synchronized void unsubscribeWithoutBroadcast(String name) {
        clients.removeIf(clientHandler -> clientHandler.getName().equals(name));
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        broadcastClientsList();
    }

    public synchronized void broadcast(String s) {
        for (ClientHandler client : clients) {
            client.sendMsg(s);
        }
    }

    public synchronized void broadcastClientsList() {
        StringBuilder sb = new StringBuilder("/clients ");
        for (ClientHandler o : clients) {
            sb.append(o.getName() + " ");
        }
        broadcast(sb.toString());
    }

    public synchronized boolean isNickLogged(String nick) {
        for (ClientHandler client : clients) {
            if (client.getName().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void sendDirect(ClientHandler sender, String recipientName, String s) {
        for (ClientHandler client : clients) {
            if (client.getName().equals(recipientName)) {
                client.sendMsg("from " + sender.getName() + ": " + s);
                sender.sendMsg("to " + recipientName + ": " + s);
                return;
            }
        }
        sender.sendMsg(recipientName + " is not available now.");
    }

    public void notifyFriends(String userName, String newUserName) {
        for (ClientHandler client : clients) {
            if (!client.getName().equals(userName)){
                client.sendMsg("User " + userName + " changed nick to " + newUserName);
            } else {
                client.sendMsg("You changed nick to "+ newUserName);
            }
        }
    }
}