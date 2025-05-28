package com.game.hurdle;

/**
 * Base class for all hurdles in the game.
 * This class follows the Single Responsibility Principle (SRP) by focusing only on
 * hurdle-specific properties and behavior.
 */
public abstract class Hurdle {
    // Position
    protected int x;
    protected int y;
    
    // Appearance
    protected char symbol;
    
    // Behavior
    protected int damageValue = 50;
    protected int scoreValue = 0;
    
    /**
     * Creates a new hurdle at the specified position.
     * 
     * @param x the horizontal position
     * @param y the vertical position
     */
    public Hurdle(int x, int y) {
        this.x = x;
        this.y = y;
        this.symbol = '*'; // Default symbol
    }
    
    /**
     * Moves the hurdle down one position.
     */
    public void moveDown() {
        y++;
    }
    
    /**
     * Moves the hurdle in a custom pattern.
     * This can be overridden by subclasses to create different movement patterns.
     */
    public void move() {
        moveDown(); // Default behavior is to move down
    }
    
    /**
     * @return the horizontal position of the hurdle
     */
    public int getX() {
        return x;
    }
    
    /**
     * @return the vertical position of the hurdle
     */
    public int getY() {
        return y;
    }
    
    /**
     * @return the symbol representing the hurdle
     */
    public char getSymbol() {
        return symbol;
    }
    
    /**
     * @return the damage value of the hurdle when hit by the car
     */
    public int getDamageValue() {
        return damageValue;
    }
    
    /**
     * @return the score value of the hurdle when avoided
     */
    public int getScoreValue() {
        return scoreValue;
    }
    
    /**
     * Checks if this hurdle collides with a position.
     * 
     * @param posX the x position to check
     * @param posY the y position to check
     * @return true if there is a collision, false otherwise
     */
    public boolean collidesWithPosition(int posX, int posY) {
        return x == posX && y == posY;
    }
}
