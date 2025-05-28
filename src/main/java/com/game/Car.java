package com.game;

/**
 * The Car class represents the player's car in the game.
 * It manages the car's position, movement, and state.
 * This class follows the Single Responsibility Principle (SRP) by focusing only on
 * car-specific logic and state management.
 */
public class Car {
    // Position
    private int x; // Horizontal position
    private int y; // Vertical position (fixed)
    
    // Car state
    private boolean crashed = false;
    private int speed = 1; // Default speed
    private int maxSpeed = 3;
    private int acceleration = 0;
    
    // Car appearance
    private final char SYMBOL = 'X';
    private final char CRASHED_SYMBOL = '#';
    
    // Movement constraints
    private int leftBoundary = 1;
    private int rightBoundary = 18; // Default, will be updated based on track width
    
    /**
     * Creates a new car at the specified position.
     * 
     * @param x the initial horizontal position
     * @param y the initial vertical position
     */
    public Car(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Sets the movement boundaries for the car.
     * 
     * @param leftBoundary the leftmost position the car can move to
     * @param rightBoundary the rightmost position the car can move to
     */
    public void setBoundaries(int leftBoundary, int rightBoundary) {
        this.leftBoundary = leftBoundary;
        this.rightBoundary = rightBoundary;
    }
    
    /**
     * Moves the car one position to the left.
     * Movement is constrained by the left boundary.
     * 
     * @return true if the car moved, false if it couldn't move
     */
    public boolean moveLeft() {
        if (x > leftBoundary) {
            x -= speed;
            // Ensure we don't go beyond the boundary
            if (x < leftBoundary) {
                x = leftBoundary;
            }
            return true;
        }
        return false;
    }
    
    /**
     * Moves the car one position to the right.
     * Movement is constrained by the right boundary.
     * 
     * @return true if the car moved, false if it couldn't move
     */
    public boolean moveRight() {
        if (x < rightBoundary) {
            x += speed;
            // Ensure we don't go beyond the boundary
            if (x > rightBoundary) {
                x = rightBoundary;
            }
            return true;
        }
        return false;
    }
    
    /**
     * Updates the car state for the current game tick.
     * This handles acceleration, deceleration, and recovery from crashes.
     */
    public void update() {
        // Handle acceleration
        if (acceleration > 0) {
            if (speed < maxSpeed) {
                speed++;
            }
            acceleration--;
        }
        
        // Recovery from crash
        if (crashed) {
            // 10% chance to recover each update
            if (Math.random() < 0.1) {
                crashed = false;
                speed = 1; // Reset speed after crash
            }
        }
    }
    
    /**
     * Accelerates the car, increasing its speed over time.
     */
    public void accelerate() {
        if (!crashed) {
            acceleration = 3; // Will increase speed over next 3 updates
        }
    }
    
    /**
     * Decelerates the car, decreasing its speed.
     */
    public void decelerate() {
        if (speed > 1) {
            speed--;
        }
    }
    
    /**
     * Sets the car state to crashed.
     * This reduces speed and changes the car's appearance.
     */
    public void crash() {
        this.crashed = true;
        this.speed = 0;
    }
    
    /**
     * Resets the car to its initial state.
     * 
     * @param initialX the initial x position
     * @param initialY the initial y position
     */
    public void reset(int initialX, int initialY) {
        this.x = initialX;
        this.y = initialY;
        this.crashed = false;
        this.speed = 1;
        this.acceleration = 0;
    }
    
    /**
     * @return true if the car is crashed, false otherwise
     */
    public boolean isCrashed() {
        return crashed;
    }
    
    /**
     * @return the horizontal position of the car
     */
    public int getX() {
        return x;
    }
    
    /**
     * @return the vertical position of the car
     */
    public int getY() {
        return y;
    }
    
    /**
     * @return the current speed of the car
     */
    public int getSpeed() {
        return speed;
    }
    
    /**
     * @return the maximum speed of the car
     */
    public int getMaxSpeed() {
        return maxSpeed;
    }
    
    /**
     * Sets the maximum speed of the car.
     * 
     * @param maxSpeed the new maximum speed
     */
    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }
    
    /**
     * @return the symbol representing the car
     */
    public char getSymbol() {
        return crashed ? CRASHED_SYMBOL : SYMBOL;
    }
}
