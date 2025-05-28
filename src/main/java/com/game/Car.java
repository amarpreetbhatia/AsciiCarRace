package com.game;

/**
 * The Car class represents the player's car in the game.
 * It manages the car's position and state.
 */
public class Car {
    private int x; // Horizontal position
    private int y; // Vertical position (fixed)
    private boolean crashed = false;
    private final char SYMBOL = 'X';
    
    public Car(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Moves the car one position to the left.
     */
    public void moveLeft() {
        if (x > 1) { // Prevent moving outside the left boundary
            x--;
        }
    }
    
    /**
     * Moves the car one position to the right.
     */
    public void moveRight() {
        if (x < 18) { // Prevent moving outside the right boundary (assuming track width of 20)
            x++;
        }
    }
    
    /**
     * Sets the car state to crashed.
     */
    public void crash() {
        this.crashed = true;
    }
    
    /**
     * Resets the car's crashed state.
     */
    public void reset() {
        this.crashed = false;
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
     * @return the symbol representing the car
     */
    public char getSymbol() {
        return SYMBOL;
    }
}
