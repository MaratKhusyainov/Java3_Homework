package Hometask6.Chat.Multiscene;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

//создаем сокет клиента, сцены и показываем сцену LOGIN
public class ChatSceneApp extends Application {
    private static final String LOGIN_FXML = "../AuthDialog.fxml";
    private static final String CHAT_FXML = "../ChatClient.fxml";


    private static Map<SceneFlow, FxmlInfo> scenes = new HashMap<>();

    public static void main(String[] args) {
        launch(args);
    }

    public static void updateScene(SceneFlow sceneFlow, FxmlInfo info) {
        scenes.put(sceneFlow, info);
    }

    public static Map<SceneFlow, FxmlInfo> getScenes() {
        return scenes;
    }

    private Socket socket;

    @Override
    public void start(Stage primaryStage) {
        socket = null;
        try {
            socket = new Socket("localhost", 8189);
        } catch (IOException e) {
            e.printStackTrace();
        }
        scenes.put(SceneFlow.LOGIN, new FxmlInfo(LOGIN_FXML, SceneFlow.LOGIN, primaryStage, socket));
        scenes.put(SceneFlow.CHAT, new FxmlInfo(CHAT_FXML, SceneFlow.CHAT, primaryStage, socket));
        primaryStage.setScene(scenes.get(SceneFlow.LOGIN).getScene());
        primaryStage.setTitle("Login");
        //закрываем сокет и завершаем программу в окне LOGIN по нажатию [x], не дожидаясь истечения 120 сек:
        primaryStage.setOnCloseRequest(event->{
            try {
                socket.close();
                System.out.println("Socket closed: " + socket.isClosed());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        primaryStage.show();
    }
}