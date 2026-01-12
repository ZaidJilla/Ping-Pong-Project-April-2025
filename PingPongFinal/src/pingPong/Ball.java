package pingPong;

import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Ball {
    private Circle shape;
    private double x;
    private double y;
    private double width;
    private double height;
    private double xVelocity;
    private double yVelocity;
    private final double NORMAL_SPEED = 4.0;  
    private final double FAST_SPEED = 6.5;    
    private double speedMultiplier = 1.0;
    

    public Ball(double x, double y, double width, double height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        
        double radius = Math.min(width, height) / 2;
        this.shape = new Circle(x + radius, y + radius, radius);
        this.shape.setFill(color);
        
        Glow glow = new Glow();
        glow.setLevel(0.5);
        this.shape.setEffect(glow);
        
        this.xVelocity = NORMAL_SPEED;
        this.yVelocity = 0;
    }

    public void update() {
        x += xVelocity;
        y += yVelocity;
        
        double radius = shape.getRadius();
        shape.setCenterX(x + radius);
        shape.setCenterY(y + radius);
    }
    
    public boolean collidesWith(Paddle paddle) {
        double ballCenterX = shape.getCenterX();
        double ballCenterY = shape.getCenterY();
        double ballRadius = shape.getRadius();
        
        double closestX = Math.max(paddle.getX(), Math.min(ballCenterX, paddle.getX() + paddle.getWidth()));
        double closestY = Math.max(paddle.getY(), Math.min(ballCenterY, paddle.getY() + paddle.getHeight()));
        
        double distanceX = ballCenterX - closestX;
        double distanceY = ballCenterY - closestY;
        
        return (distanceX * distanceX + distanceY * distanceY) < (ballRadius * ballRadius);
    }
    
    public void reverseXDirection() {
        xVelocity = -xVelocity;
    }

    public void reverseYDirection() {
        yVelocity = -yVelocity;
    }

    public void speedUp() {
        speedMultiplier = FAST_SPEED / NORMAL_SPEED;
        double magnitude = Math.sqrt(xVelocity * xVelocity + yVelocity * yVelocity);
        double normalizedX = xVelocity / magnitude;
        double normalizedY = yVelocity / magnitude;
        
        xVelocity = normalizedX * FAST_SPEED;
        yVelocity = normalizedY * FAST_SPEED;
        
        Glow glow = (Glow) shape.getEffect();
        glow.setLevel(1.0);
    }
    
    public void resetSpeed() {
        double magnitude = Math.sqrt(xVelocity * xVelocity + yVelocity * yVelocity);
        double normalizedX = xVelocity / magnitude;
        double normalizedY = yVelocity / magnitude;
        
        xVelocity = normalizedX * NORMAL_SPEED;
        yVelocity = normalizedY * NORMAL_SPEED;
        speedMultiplier = 1.0;
        
        // Reset glow effect
        Glow glow = (Glow) shape.getEffect();
        glow.setLevel(0.5);
    }
    
    public void increaseSpeed(double factor) {
        double currentSpeed = Math.sqrt(xVelocity * xVelocity + yVelocity * yVelocity);
        double newSpeed = currentSpeed * factor;
        
        // Normalize and scale velocity
        double normalizedX = xVelocity / currentSpeed;
        double normalizedY = yVelocity / currentSpeed;
        xVelocity = normalizedX * newSpeed;
        yVelocity = normalizedY * newSpeed;
        
        // Slightly increase glow effect
        Glow glow = (Glow) shape.getEffect();
        glow.setLevel(Math.min(1.5, glow.getLevel() + 0.1));
    }
    
    public Circle getShape() {
        return shape;
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
        this.shape.setCenterX(x + shape.getRadius());
    }

    public void setY(double y) {
        this.y = y;
        this.shape.setCenterY(y + shape.getRadius());
    }
    
    public double getWidth() {
        return width;
    }
    
    public double getHeight() {
        return height;
    }

    public double getXVelocity() {
        return xVelocity;
    }

    public void setVelocity(double xVel, double yVel) {
        this.xVelocity = xVel;
        this.yVelocity = yVel;
    }

    public void setYVelocity(double yVel) {
        this.yVelocity = yVel;
    }
}