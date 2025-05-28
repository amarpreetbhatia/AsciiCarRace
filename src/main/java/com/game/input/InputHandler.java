package com.game.input;

/**
 * The InputHandler class manages input sources and provides a unified interface
 * for the game to interact with input.
 * This class follows the Liskov Substitution Principle (LSP) by working with
 * any implementation of InputSource.
 */
public class InputHandler {
    // The current input source
    private final InputSource inputSource;
    
    /**
     * Creates a new InputHandler with the specified input source.
     * 
     * @param inputSource the input source to use
     */
    public InputHandler(InputSource inputSource) {
        this.inputSource = inputSource;
    }
    
    /**
     * Creates a new InputHandler with a default keyboard input source.
     */
    public InputHandler() {
        this(new KeyboardInputSource());
    }
    
    /**
     * Initializes the input handler and its input source.
     */
    public void initialize() {
        inputSource.initialize();
    }
    
    /**
     * Gets the last direction input by the user.
     * 
     * @return the current direction, or null if no direction is set
     */
    public InputSource.Direction getLastDirection() {
        return inputSource.getCurrentDirection();
    }
    
    /**
     * Clears the last direction after it has been processed.
     */
    public void clearLastDirection() {
        inputSource.clearCurrentDirection();
    }
    
    /**
     * Shuts down the input handler and its input source.
     */
    public void shutdown() {
        inputSource.shutdown();
    }
    
    /**
     * Checks if the input handler is running.
     * 
     * @return true if the input handler is running, false otherwise
     */
    public boolean isRunning() {
        return inputSource.isRunning();
    }
}
