package Hometask2;


import Hometask2.Multiscene.ChatSceneApp;
import Hometask2.Multiscene.SceneFlow;
import Hometask2.Multiscene.Stageable;
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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;


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

    public static void setLogin(String log){ login = log; }

    public static void setNick(String nick_){
        System.out.println(nick_  + "<-- nick");
        nick = nick_;}



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
                        if (in.available()>0) {
                            String strFromServer = in.readUTF();
                            System.out.println("From server: " + strFromServer);
                            if (strFromServer.equalsIgnoreCase("/end")) {

                                terminateClient();
                                break;
                            }
                            else if (strFromServer.startsWith("/nickOk")){
                                String[] parts = strFromServer.split("\\s");
                                nick = parts[1];
                            }
                            else if (strFromServer.startsWith("/clients ")) {
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

                                if (strFromServer.startsWith("from")) {
                                    chatText += "<p style='background-color:powderblue; white-space: normal; ' >" + ChatController.getSendingTime() +
                                            strFromServer + "</p>";
                                }
                                else if (strFromServer.startsWith("to")) {
                                    chatText += "<p align='right' style='background-color:powderblue; white-space: normal;'>" + ChatController.getSendingTime() +
                                            strFromServer + "</p>";
                                }
                                else if (strFromServer.startsWith(nick)) {
                                    chatText += "<p align='right' style='white-space: normal;'>" + ChatController.getSendingTime() + strFromServer + "</p>";
                                }
                                else {
                                    chatText += "<p style='white-space: normal;'>" + ChatController.getSendingTime() + strFromServer + "</p>";
                                }
                                Platform.runLater(() -> {
                                    messageArea.getEngine().loadContent(chatText);
                                });

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
            if(in.available()>0) {
                String strFromServer = in.readUTF();
                if (strFromServer.startsWith("/clients")) {
                    String[] parts = strFromServer.split("\\s");
                    for(int i=1; i<parts.length; i++) {
                        if (!parts[i].equals(myNick)) nickListItems.add(parts[i]);
                    }
                    System.out.println("Authorized on server");
                    nickList.setItems(nickListItems);
                    nickList.getSelectionModel().select(0);
                    break;
                }
            }

        }

    }

    private static String getSendingTime(){
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
        if(!messageText.isEmpty()) {
            System.out.println("message sent: " + messageText);
            if(selectedIndex!=0) {
                messageText = "/w " + nickList.getSelectionModel().getSelectedItems().get(0) + " " +messageText;
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
        stage.setOnCloseRequest(event->{
            try {
                //отправляем на сервер "/end", чтобы получить симметричный ответ, после чего срабатывает terminateClient():
                out.writeUTF("/end");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
