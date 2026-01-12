package pingPong;

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class GameManager {
    
    private Stage primaryStage;
    private Scene gameScene;
    private Pane gamePane;
    private Scene startScene;
    private Scene gameOverScene;
    
    private Paddle player1Paddle;
    private Paddle player2Paddle;
    private Ball ball;
    
    private Label scoreLabel;
    private Label rallyLabel;
    private Label readyLabel;
    
    private int player1Score = 0;
    private int player2Score = 0;
    private int rallyCount = 0;
    private boolean gameRunning = false;
    private boolean waitingForSpaceBar = true;
    private boolean isAIOpponent = false;
    private long lastCollisionTime = 0;
    
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int WINNING_SCORE = 5;
    private static final long COLLISION_COOLDOWN = 300_000_000; 
    private static final int RALLY_SPEED_INCREASE_THRESHOLD = 10; 
    private AnimationTimer gameLoop;
    
    private static final Color PADDLE_COLOR_P1 = Color.rgb(255, 50, 50);
    private static final Color PADDLE_COLOR_P2 = Color.rgb(50, 150, 255);
    private static final Color BALL_COLOR = Color.rgb(255, 255, 100);
    private static final Color TEXT_COLOR = Color.WHITE;
 
    public GameManager() {
        initializeStartScreen();
    }
    
    private void initializeStartScreen() {
        VBox startScreen = new VBox(20);
        startScreen.setAlignment(Pos.CENTER);
        startScreen.setStyle("-fx-background-color: rgb(0, 10, 30);");
        
        Label titleLabel = new Label("PING PONG");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 64));
        titleLabel.setTextFill(Color.WHITE);
        
        Label selectModeLabel = new Label("Select Game Mode:");
        selectModeLabel.setFont(Font.font("Arial", 28));
        selectModeLabel.setTextFill(Color.WHITE);
        
        Button onePlayerButton = new Button("1 Player vs AI");
        onePlayerButton.setFont(Font.font("Arial", 18));
        onePlayerButton.setPrefWidth(200);
        onePlayerButton.setStyle("-fx-background-color: #FF3232; -fx-text-fill: white;");
        onePlayerButton.setOnAction(e -> {
            isAIOpponent = true;
            initializeGame();
            resetGame(); 
        });
        
        Button twoPlayerButton = new Button("2 Players");
        twoPlayerButton.setFont(Font.font("Arial", 18));
        twoPlayerButton.setPrefWidth(200);
        twoPlayerButton.setStyle("-fx-background-color: #3296FF; -fx-text-fill: white;");
        twoPlayerButton.setOnAction(e -> {
            isAIOpponent = false;
            initializeGame();
            resetGame(); 
        });
        
        startScreen.getChildren().addAll(titleLabel, selectModeLabel, onePlayerButton, twoPlayerButton);
        startScene = new Scene(startScreen, WIDTH, HEIGHT);
    }
    
    private void initializeGame() {
        gamePane = new Pane();
        gamePane.setPrefSize(WIDTH, HEIGHT);
        gamePane.setStyle("-fx-background-color: rgb(0, 10, 30);");
        
        Line centerLine = new Line(WIDTH / 2, 0, WIDTH / 2, HEIGHT);
        centerLine.setStroke(Color.rgb(255, 255, 255, 0.4));
        centerLine.getStrokeDashArray().addAll(15d, 10d);
        centerLine.setStrokeWidth(2);
        
        player1Paddle = new Paddle(30, HEIGHT / 2 - 50, 15, 100, PADDLE_COLOR_P1);
        player2Paddle = new Paddle(WIDTH - 45, HEIGHT / 2 - 50, 15, 100, PADDLE_COLOR_P2);
        
        ball = new Ball(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20, BALL_COLOR);
        
        scoreLabel = new Label("0 - 0");
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        scoreLabel.setTextFill(TEXT_COLOR);
        scoreLabel.setLayoutX(WIDTH / 2 - 50);
        scoreLabel.setLayoutY(20);
        
        rallyLabel = new Label("Rally: 0");
        rallyLabel.setFont(Font.font("Arial", 18));
        rallyLabel.setTextFill(TEXT_COLOR);
        rallyLabel.setLayoutX(WIDTH / 2 - 40);
        rallyLabel.setLayoutY(70);
        
        readyLabel = new Label("When ready, Press the Space Bar");
        readyLabel.setFont(Font.font("Arial", 24));
        readyLabel.setTextFill(TEXT_COLOR);
        readyLabel.setLayoutX(WIDTH / 2 - 170);
        readyLabel.setLayoutY(HEIGHT / 2 - 50);
        readyLabel.setTextAlignment(TextAlignment.CENTER);
        
        gamePane.getChildren().addAll(centerLine, player1Paddle.getShape(), player2Paddle.getShape(), 
                ball.getShape(), scoreLabel, rallyLabel, readyLabel);
        
        gameScene = new Scene(gamePane, WIDTH, HEIGHT);
        
        setupControls();
        
        primaryStage.setScene(gameScene);
        
        if (gameLoop != null) {
            gameLoop.stop();
        }
        
        startGameLoop();
    }
    
    private void showGameOverScreen(String winner) {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        
        VBox gameOverScreen = new VBox(30);
        gameOverScreen.setAlignment(Pos.CENTER);
        gameOverScreen.setStyle("-fx-background-color: rgb(0, 10, 30);");
        
        Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.setFont(Font.font("Arial", FontWeight.BOLD, 64));
        gameOverLabel.setTextFill(TEXT_COLOR);
        
        Label winnerLabel = new Label(winner + " WINS!");
        winnerLabel.setFont(Font.font("Arial", 36));
        winnerLabel.setTextFill(TEXT_COLOR);
        
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button playAgainButton = new Button("Play Again");
        playAgainButton.setFont(Font.font("Arial", 18));
        playAgainButton.setPrefSize(150, 50);
        playAgainButton.setStyle("-fx-background-color: #32CD32; -fx-text-fill: white;");
        playAgainButton.setOnAction(e -> {
            resetGame();
            primaryStage.setScene(gameScene);
            startGameLoop();
        });
        
        Button mainMenuButton = new Button("Main Menu");
        mainMenuButton.setFont(Font.font("Arial", 18));
        mainMenuButton.setPrefSize(150, 50);
        mainMenuButton.setStyle("-fx-background-color: #FF9600; -fx-text-fill: white;");
        mainMenuButton.setOnAction(e -> {
            primaryStage.setScene(startScene);
        });
        
        buttonBox.getChildren().addAll(playAgainButton, mainMenuButton);
        
        gameOverScreen.getChildren().addAll(gameOverLabel, winnerLabel, buttonBox);
        
        gameOverScene = new Scene(gameOverScreen, WIDTH, HEIGHT);
        primaryStage.setScene(gameOverScene);
    }

    private void resetGame() {
        player1Score = 0;
        player2Score = 0;
        rallyCount = 0;
        updateScore();
        updateRallyCounter();
        resetBall();
        waitForSpaceBar();
        gameRunning = false;
        
        if (player1Paddle != null && player2Paddle != null) {
            player1Paddle.setY(HEIGHT / 2 - 50);
            player2Paddle.setY(HEIGHT / 2 - 50);
        }
    }

    private void setupControls() {
        gameScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.W) {
                player1Paddle.setUp(true);
            }
            if (e.getCode() == KeyCode.S) {
                player1Paddle.setDown(true);
            }
            
            if (!isAIOpponent) {
                if (e.getCode() == KeyCode.UP) {
                    player2Paddle.setUp(true);
                }
                if (e.getCode() == KeyCode.DOWN) {
                    player2Paddle.setDown(true);
                }
            }
            
            if (e.getCode() == KeyCode.SPACE && waitingForSpaceBar) {
                waitingForSpaceBar = false;
                readyLabel.setVisible(false);
                resetBall();
                gameRunning = true;
            }
        });
        
        gameScene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.W) {
                player1Paddle.setUp(false);
            }
            if (e.getCode() == KeyCode.S) {
                player1Paddle.setDown(false);
            }
            
            if (!isAIOpponent) {
                if (e.getCode() == KeyCode.UP) {
                    player2Paddle.setUp(false);
                }
                if (e.getCode() == KeyCode.DOWN) {
                    player2Paddle.setDown(false);
                }
            }
        });
    }
    
    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update(now);
            }
        };
        gameLoop.start();
    }

    private void update(long now) {
        player1Paddle.update(HEIGHT);
        
        if (isAIOpponent) {
            updateAI();
        } else {
            player2Paddle.update(HEIGHT);
        }
        
        if (gameRunning) {
            ball.update();
            
            checkCollisions(now);
        }
    }
    
    private void updateAI() {
        if (ball.getXVelocity() > 0) {
            double targetY = ball.getY() + ball.getHeight() / 2 - player2Paddle.getHeight() / 2;
            double currentY = player2Paddle.getY();
            
            double reactionChance = 0.25; 
            double errorFactor = Math.random() * 60 - 30; 
            
            if (Math.random() > reactionChance) {
                player2Paddle.setUp(false);
                player2Paddle.setDown(false);
                return;
            }
            
            targetY += errorFactor;
            
            double aiSpeed = 5.0;
            if (targetY < currentY - aiSpeed) {
                player2Paddle.setUp(true);
                player2Paddle.setDown(false);
            } else if (targetY > currentY + aiSpeed) {
                player2Paddle.setUp(false);
                player2Paddle.setDown(true);
            } else {
                player2Paddle.setUp(false);
                player2Paddle.setDown(false);
            }
        } else {
            double centerY = HEIGHT / 2 - player2Paddle.getHeight() / 2;
            double currentY = player2Paddle.getY();
            
            if (Math.abs(centerY - currentY) > 50) { 
                if (centerY < currentY) {
                    player2Paddle.setUp(true);
                    player2Paddle.setDown(false);
                } else {
                    player2Paddle.setUp(false);
                    player2Paddle.setDown(true);
                }
            }
        }
        
        player2Paddle.update(HEIGHT);
    }

    private void checkCollisions(long now) {
        boolean collisionHandled = false;
        
        if (ball.getY() <= 0) {
            ball.setY(0); 
            ball.reverseYDirection();
            collisionHandled = true;
        } else if (ball.getY() + ball.getHeight() >= HEIGHT) {
            ball.setY(HEIGHT - ball.getHeight());
            ball.reverseYDirection();
            collisionHandled = true;
        }
        
        if (ball.getX() <= 0) {
            player2Score++;
            updateScore();
            rallyCount = 0;
            updateRallyCounter();
            checkWinCondition();
            collisionHandled = true;
        } else if (ball.getX() + ball.getWidth() >= WIDTH) {
            player1Score++;
            updateScore();
            rallyCount = 0;
            updateRallyCounter();
            checkWinCondition();
            collisionHandled = true;
        }
        
        boolean canCollide = (now - lastCollisionTime) > COLLISION_COOLDOWN;
        
        if (!collisionHandled && canCollide && ball.collidesWith(player1Paddle)) {
            if (ball.getXVelocity() < 0) {
                ball.setX(player1Paddle.getX() + player1Paddle.getWidth());
            }
            handlePaddleCollision(player1Paddle);
            lastCollisionTime = now;
            collisionHandled = true;
        }
        
        if (!collisionHandled && canCollide && ball.collidesWith(player2Paddle)) {
            if (ball.getXVelocity() > 0) {
                ball.setX(player2Paddle.getX() - ball.getWidth());
            }
            handlePaddleCollision(player2Paddle);
            lastCollisionTime = now;
        }
    }
    
    private void handlePaddleCollision(Paddle paddle) {
        ball.reverseXDirection();
        
        double ballCenter = ball.getY() + ball.getHeight() / 2;
        double paddleCenter = paddle.getY() + paddle.getHeight() / 2;
        double relativeIntersect = (ballCenter - paddleCenter) / (paddle.getHeight() / 2);
        
        double newYVelocity = relativeIntersect * 5;
        ball.setYVelocity(newYVelocity);
        
        double hitPosition = Math.abs(relativeIntersect);
        
        rallyCount++;
        
        if (rallyCount % RALLY_SPEED_INCREASE_THRESHOLD == 0) {
            ball.speedUp();
            
            paddle.flash();
        }
        
        if (hitPosition < 0.2) {
            paddle.flash();
        }
        
        updateRallyCounter();
    }
    

    private void updateScore() {
        scoreLabel.setText(player1Score + " - " + player2Score);
        waitForSpaceBar();
    }
    
    private void updateRallyCounter() {
        rallyLabel.setText("Rally: " + rallyCount);
    }

    private void checkWinCondition() {
        if (player1Score >= WINNING_SCORE) {
            gameRunning = false;  
            showGameOverScreen("PLAYER 1");
        } else if (player2Score >= WINNING_SCORE) {
            gameRunning = false; 
            showGameOverScreen(isAIOpponent ? "AI" : "PLAYER 2");
        }
    }
  
    private void waitForSpaceBar() {
        gameRunning = false;
        waitingForSpaceBar = true;
        readyLabel.setVisible(true);
    }
    
    private void resetBall() {
        ball.setX(WIDTH / 2 - ball.getWidth() / 2);
        ball.setY(HEIGHT / 2 - ball.getHeight() / 2);
        
        double angle = Math.random() * Math.PI / 2 - Math.PI / 4;
        if (Math.random() < 0.5) {
            angle += Math.PI;
        }
        
        double speed = 5.0;
        ball.setVelocity(Math.cos(angle) * speed, Math.sin(angle) * speed);
        
        ball.resetSpeed();
    }
    
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public Scene getStartScene() {
        return startScene;
    }
}