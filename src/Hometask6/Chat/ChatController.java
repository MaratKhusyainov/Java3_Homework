package Hometask6.Chat;

import Hometask6.Chat.Multiscene.ChatSceneApp;
import Hometask6.Chat.Multiscene.SceneFlow;
import Hometask6.Chat.Multiscene.Stageable;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

//контроллер сцены CHAT
public class ChatController implements Stageable {
    private Stage stage;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    public static ObservableList<String> nickListItems;
    Date time;
    private Thread readerThread;
    private String chatText = "<body>";
    private static String nick;
    private static String login;
    private final String filePath = "history.txt";

    public static void setLogin(String log) {
        login = log;
    }

    public static void setNick(String nick_) {
        System.out.println(nick_ + "<-- nick");
        nick = nick_;
    }


    @FXML
    WebView messageArea;

    @FXML
    TextField newMessage;

    @FXML
    ListView nickList;

    public void initialize() throws IOException {
        //принимаем сообщения от сервера:
        readerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!readerThread.interrupted()) {
                        if (in.available() > 0) {
                            String strFromServer = in.readUTF();
                            System.out.println("From server: " + strFromServer);
                            if (strFromServer.equalsIgnoreCase("/end")) {

                                terminateClient();
                                break;
                            } else if (strFromServer.startsWith("/nickOk")) {
                                String[] parts = strFromServer.split("\\s");
                                nick = parts[1];
                            } else if (strFromServer.startsWith("/clients ")) {
                                Platform.runLater(() -> {
                                    nickListItems.clear();
                                    nickListItems.add("All");
                                    String[] parts = strFromServer.split("\\s");
                                    for (int i = 1; i < parts.length; i++) {
//                                        if (!parts[i].equals(ChatSceneApp.getScenes().get(SceneFlow.CHAT).getNick()))
                                        if (!parts[i].equals(nick))

                                            nickListItems.add(parts[i]);
                                    }
                                });
                            } else {
                                // В этом блоке все остальные сообщения, кт. будут записываться в историю и отображаться в окне чата
                                String historyMess;
                                Path path = Paths.get(filePath);
                                // Создаем файл для записи истории сообщений, если он еще не был создан:
                                if (!Files.exists(path)) Files.createFile(path);

                                if (strFromServer.startsWith("from")) {
                                    chatText += "<p style='background-color:powderblue; white-space: normal; ' >" + ChatController.getSendingTime() +
                                            strFromServer + "</p>";
                                    historyMess = ChatController.getSendingTime() + strFromServer;
                                } else if (strFromServer.startsWith("to")) {
                                    chatText += "<p align='right' style='background-color:powderblue; white-space: normal;'>" + ChatController.getSendingTime() +
                                            strFromServer + "</p>";
                                    historyMess = ChatController.getSendingTime() + strFromServer;
                                } else if (strFromServer.startsWith(nick)) {
                                    chatText += "<p align='right' style='white-space: normal;'>" + ChatController.getSendingTime() + strFromServer + "</p>";
                                    historyMess = ChatController.getSendingTime() + strFromServer;
                                } else {
                                    chatText += "<p style='white-space: normal;'>" + ChatController.getSendingTime() + strFromServer + "</p>";
                                    historyMess = ChatController.getSendingTime() + strFromServer;
                                }
                                Platform.runLater(() -> {
                                    messageArea.getEngine().loadContent(chatText);
                                });

                                String text = historyMess + "\n";
                                //запись сообщений в файл history.txt:
                                try {
                                    Files.write(Paths.get(filePath), text.getBytes(), StandardOpenOption.APPEND);
                                } catch (IOException e) {
                                    System.out.println(e);
                                }

                                //Предыдущий вариант с полем чата TextArea (до добавления стилей):
//                            else Platform.runLater(()->{ messageArea.appendText(new SimpleDateFormat("hh:mm:ss a ").format(new Date()) + strFromServer + System.lineSeparator());});

                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        readerThread.start();

        nickListItems = FXCollections.observableArrayList();
        nickListItems.add("All");
        socket = ChatSceneApp.getScenes().get(SceneFlow.CHAT).getSocket();
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        String myNick = ChatSceneApp.getScenes().get(SceneFlow.CHAT).getNick();
        while (true) {
            if (in.available() > 0) {
                String strFromServer = in.readUTF();
                if (strFromServer.startsWith("/clients")) {
                    String[] parts = strFromServer.split("\\s");
                    for (int i = 1; i < parts.length; i++) {
                        if (!parts[i].equals(myNick)) nickListItems.add(parts[i]);
                    }
                    System.out.println("Authorized on server");
                    nickList.setItems(nickListItems);
                    nickList.getSelectionModel().select(0);
                    break;
                }
            }

        }
        // Отобразить на экране последние 100 сообщений:
        showHistory();

    }

    //выгрузка из history.txt последих 100 строк в чат:
    private void showHistory() throws IOException {
        int indxOfMessageStart = 11;
        String[] timeAndMess;

        List<String> lines = Files.readAllLines(Paths.get(filePath), UTF_8);

        if (lines.size() >= 100) {
            for (int i = lines.size() - 100; i < lines.size(); i++) {
                char[] charMessFromHistory = lines.get(i).toCharArray();
                int indexOfMessageEnd = lines.get(i).length();
                timeAndMess = separateTimefromMess(charMessFromHistory, indxOfMessageStart, indexOfMessageEnd);
                showHistoryinChat(timeAndMess);
            }
        } else {
            for (String line : lines) {
                char[] charMessFromHistory = line.toCharArray();
                int indexOfMessageEnd = line.length();
                timeAndMess = separateTimefromMess(charMessFromHistory, indxOfMessageStart, indexOfMessageEnd);
                showHistoryinChat(timeAndMess);
            }
        }
    }

    //отображает сообщения из истории, стилизуя в зависимости от типа:
    private void showHistoryinChat(String[] timeAndMess) {
        if (timeAndMess[1].startsWith("from")) {
            chatText += "<p style='background-color:powderblue; white-space: normal; ' >" + timeAndMess[0] +
                    timeAndMess[1] + "</p>";
        } else if (timeAndMess[1].startsWith("to")) {
            chatText += "<p align='right' style='background-color:powderblue; white-space: normal;'>" + timeAndMess[0] +
                    timeAndMess[1] + "</p>";
        } else if (timeAndMess[1].startsWith(nick)) {
            chatText += "<p align='right' style='white-space: normal;'>" + timeAndMess[0] + timeAndMess[1] + "</p>";
        } else {
            chatText += "<p style='white-space: normal;'>" + timeAndMess[0] + timeAndMess[1] + "</p>";
        }
        messageArea.getEngine().loadContent(chatText);
    }

    // Преобразует массив символов, содержащий сообщение в массив из 2х строк {время сообщения, сообщение}:
    private String[] separateTimefromMess(char[] charMessFromHistory, int indexOFMessStart, int indexOfMessEnd) {
        char[] charMessage = Arrays.copyOfRange(charMessFromHistory, indexOFMessStart, indexOfMessEnd);
        char[] charTime = Arrays.copyOfRange(charMessFromHistory, 0, indexOFMessStart);
        String historyMessTime = String.valueOf(charTime);
        String histMess = String.valueOf(charMessage);
        return new String[]{historyMessTime, histMess};
    }

    private static String getSendingTime() {
        return new SimpleDateFormat("[hh:mm a] ").format(new Date());
    }


    private void terminateClient() {
        readerThread.interrupt();
        try {
            socket.close();
            System.out.println("Socket closed: " + socket.isClosed());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Platform.exit();
    }

    // отправляем сообщения на сервер:
    public void sendMessageTypeAction(ActionEvent actionEvent) {
        int selectedIndex = (Integer) nickList.getSelectionModel().getSelectedIndices().get(0);
        String messageText = newMessage.getText().trim();
        if (!messageText.isEmpty()) {
            System.out.println("message sent: " + messageText);
            if (selectedIndex != 0) {
                messageText = "/w " + nickList.getSelectionModel().getSelectedItems().get(0) + " " + messageText;
                System.out.println("message sent: " + messageText);
            }
            try {
                out.writeUTF(messageText);
            } catch (IOException e) {
                e.printStackTrace();
            }
            newMessage.clear();
        }
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest(event -> {
            try {
                //отправляем на сервер "/end", чтобы получить симметричный ответ, после чего срабатывает terminateClient():
                out.writeUTF("/end");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}