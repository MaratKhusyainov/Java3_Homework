package Hometask6.Chat;

import Hometask6.Chat.Multiscene.ChatSceneApp;
import Hometask6.Chat.Multiscene.SceneFlow;
import Hometask6.Chat.Multiscene.Stageable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

//контроллер сцены LOGIN
public class AuthDialog implements Stageable, Initializable {
    private Stage stage;
    public Socket socket = ChatSceneApp.getScenes().get(SceneFlow.CHAT).getSocket();

    private static final int CLOSETIME = 20000;
    public static long startTime;
    static boolean authOk;
    String login;

    @FXML
    AnchorPane rootPane;

    @FXML
    TextField userName;

    @FXML
    PasswordField userPassword;

    @FXML
    Button buttonOk;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startTime = System.currentTimeMillis();
        System.out.println("Timer starts.");
        new Thread(()->{
            while (!authOk & !socket.isClosed()) {
                if (System.currentTimeMillis() - startTime > CLOSETIME) {
                    Platform.runLater(() -> {
                        try {
                            ((Stage)rootPane.getScene().getWindow()).close();
                            socket.close();
                            System.out.println("Timer stopped, connection closed.");
                        } catch (IOException | NullPointerException e) {
//                            e.printStackTrace();
                            System.out.println("Login window is already closed");
                        }
                    });
                    break;
                }
            }
        }).start();
//        stage.setOnCloseRequest();
    }


    public void submitUserPassword(ActionEvent actionEvent) {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            if (userName.getLength()!=0 && userPassword.getLength()!=0) {
                String authMessage = "/auth " + userName.getText() + " " + userPassword.getText();
//                    System.out.println(Thread.currentThread().getName().toString());
                login = userName.getText();
                userPassword.clear();
                userName.clear();
                out.writeUTF(authMessage);

                while(true) {
                    if (in.available() > 0) {
                        String strFromServer = in.readUTF();
                        if (strFromServer.startsWith("/authOk")) {
                            out.writeUTF("/login " + login); //если авторизация произошла, отправляем серверу логин
                            System.out.println("Authorized on server");
                            authOk = true;
                            ChatSceneApp.getScenes().get(SceneFlow.CHAT).setNick(strFromServer.split("\\s")[1]);
                            ChatController.setNick(strFromServer.split("\\s")[1]);
                            break;
                        }
                        if(strFromServer.startsWith("Incorrect")){
                            System.out.println("Wrong login/password");
                            break;
                        }
                    }
                }
            }
            if (authOk) {
                stage.setScene(ChatSceneApp.getScenes().get(SceneFlow.CHAT).getScene());
                stage.setTitle("Chat");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void exit(ActionEvent actionEvent) {
        ((Stage)rootPane.getScene().getWindow()).close();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }


}






