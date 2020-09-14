package Hometask2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MyServer {
    private final int PORT = 8189;
    private List<ClientHandler> clients;
    private AuthService authService;

    public AuthService getAuthService() {
        return authService;
    }

    //создаем сокет сервера, authService (подключается к БД), сервер ждет подключений, при подключении
    // создается обработчик клиентских сообщений ClientHandler:
    public MyServer() {
        try (ServerSocket server = new ServerSocket(PORT)) {
            authService = new AuthService();
            authService.start();
            clients = new ArrayList<>();
            while (true) {
                System.out.println("Server awaits clients");
                Socket socket = server.accept();
                System.out.println("Client connected");
                new ClientHandler(this, socket);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Server error");
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
            if (!client.getName().equals(userName)) {
                client.sendMsg("User " + userName + " changed nick to " + newUserName);
            } else {
                client.sendMsg("You changed nick to " + newUserName);
            }
        }
    }
}
