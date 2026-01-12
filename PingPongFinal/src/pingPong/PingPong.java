package pingPong;

import javafx.application.Application;
import javafx.stage.Stage;

public class PingPong extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        GameManager gameManager = new GameManager();
        
        primaryStage.setTitle("Ping Pong Game");
        primaryStage.setScene(gameManager.getStartScene());
        primaryStage.setResizable(false);
        
        primaryStage.show();
        
        gameManager.setPrimaryStage(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}