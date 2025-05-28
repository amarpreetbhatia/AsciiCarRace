package com.game.input;

/**
 * Interface defining an input source for the game.
 * This interface enables the Liskov Substitution Principle (LSP) by allowing
 * different input implementations to be used interchangeably.
 */
public interface InputSource {
    
    /**
     * Enum representing possible movement directions.
     */
    enum Direction {
        LEFT, RIGHT, UP, DOWN
    }
    
    /**
     * Initializes the input source.
     */
    void initialize();
    
    /**
     * Gets the current direction from the input source.
     * 
     * @return the current direction, or null if no direction is active
     */
    Direction getCurrentDirection();
    
    /**
     * Clears the current direction.
     */
    void clearCurrentDirection();
    
    /**
     * Checks if the input source is running.
     * 
     * @return true if the input source is running, false otherwise
     */
    boolean isRunning();
    
    /**
     * Shuts down the input source.
     */
    void shutdown();
}
