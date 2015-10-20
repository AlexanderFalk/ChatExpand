package Chat;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Main extends Application
{
    public Stage stage;

    // ? means 'then' ... : means 'else'
    private boolean isServer = true;

    private TextField userText;
    private TextArea chatWindow;
    private NetworkConnection connection = isServer ? createServer() : createClient();
    public Button clientBt = new Button("CLIENT");
    public Button serverBt = new Button("SERVER");

    public String message;


    public Parent createGUI()
    {
        GridPane grid = new GridPane();
        clientBt.setPrefSize(75, 75);
        serverBt.setPrefSize(75, 75);

        clientBt.setOnAction(e ->
        {
            isServer = false;
            popUpStage();
        });

        serverBt.setOnAction(e ->
        {
            isServer = true;
            popUpStage();
        });

        grid.add(clientBt, 0, 0);
        grid.add(serverBt, 1, 0);
        grid.setAlignment(Pos.CENTER);

        grid.setPrefSize(225, 150);
        return grid;
    }

    public void init() throws Exception
    {
        connection.startConnection();
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        primaryStage.setTitle("Chat App");
        primaryStage.setScene(new Scene(createGUI()));
        primaryStage.show();
    }

    public void stop() throws Exception
    {
        connection.closeConnection();
    }

    public Server createServer()
    {
        return new Server(55555, data ->
        {

            Platform.runLater(() ->
            {
                chatWindow.appendText(data.toString() + "\n");
            });
        });
    }

    private Client createClient()
    {
        return new Client("127.0.0.1", 55555, data ->
        {
            Platform.runLater(() ->
            {
                chatWindow.appendText(data.toString() + "\n");
            });
        });
    }

    private Stage popUpStage()
    {
        stage = new Stage();
        BorderPane root = new BorderPane();
        HBox text = new HBox();

        userText = new TextField();
        userText.setOnAction(e ->
        {
            message = isServer ? "Server: " : "Client: ";
            message +=  userText.getText();
            userText.clear();

            chatWindow.appendText(message + "\n");

            try
            {
                connection.sendMessage(message);
            }
            catch (Exception e1)
            {
                chatWindow.appendText("Failed to send \n");
            }
        });

        chatWindow = new TextArea();
        chatWindow.setPrefSize(300, 150);
        chatWindow.setVisible(true);

        text.setAlignment(Pos.CENTER);
        text.setPadding(new Insets(10));
        text.getChildren().add(userText);
        root.setBottom(userText);
        root.setCenter(chatWindow);

        stage.setScene(new Scene(root));
        stage.show();
        return stage;
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
