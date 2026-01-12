package pingPong;

import javafx.animation.FadeTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Paddle {
    private Rectangle shape;
    private double x;
    private double y;
    private double width;
    private double height;
    private final double SPEED = 7.0;
    private boolean isUp;
    private boolean isDown;
    private Color originalColor;
    
    public Paddle(double x, double y, double width, double height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.originalColor = color;
        
        this.shape = new Rectangle(x, y, width, height);
        this.shape.setFill(color);
        
        // Add subtle glow effect
        this.shape.setArcWidth(10);
        this.shape.setArcHeight(10);
        
        this.isUp = false;
        this.isDown = false;
    }

    public void update(double screenHeight) {
        if (isUp && y > 0) {
            y -= SPEED;
        }
        if (isDown && y + height < screenHeight) {
            y += SPEED;
        }
        
        shape.setY(y);
    }

    public void flash() {
        Color flashColor = Color.WHITE;
        
        FadeTransition fadeOut = new FadeTransition(Duration.millis(100), shape);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.7);
        fadeOut.setOnFinished(e -> {
            shape.setFill(flashColor);
            
            FadeTransition fadeIn = new FadeTransition(Duration.millis(100), shape);
            fadeIn.setFromValue(0.7);
            fadeIn.setToValue(1.0);
            fadeIn.setOnFinished(event -> shape.setFill(originalColor));
            fadeIn.play();
        });
        
        fadeOut.play();
    }

    public Rectangle getShape() {
        return shape;
    }

    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public void setY(double y) {
        this.y = y;
        shape.setY(y);
    }

    public double getWidth() {
        return width;
    }
    
    public double getHeight() {
        return height;
    }

    public void setUp(boolean up) {
        this.isUp = up;
    }

    public void setDown(boolean down) {
        this.isDown = down;
    }
}