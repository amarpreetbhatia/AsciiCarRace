package com.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The InputHandler class is responsible for capturing and processing user input.
 * It runs in a separate thread to avoid blocking the game loop.
 */
public class InputHandler {
    private Direction lastDirection = null;
    private final ExecutorService inputThread = Executors.newSingleThreadExecutor();
    private volatile boolean running = false;
    
    public enum Direction {
        LEFT, RIGHT
    }
    
    /**
     * Initializes the input handler and starts the input thread.
     */
    public void initialize() {
        running = true;
        inputThread.submit(this::readInput);
    }
    
    /**
     * Continuously reads user input from the console.
     */
    private void readInput() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Controls: 'a' for left, 'd' for right");
            
            while (running) {
                if (reader.ready()) {
                    String input = reader.readLine();
                    if (input != null && !input.isEmpty()) {
                        char key = input.charAt(0);
                        processKey(key);
                    }
                }
                Thread.sleep(50); // Small delay to prevent CPU hogging
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error reading input: " + e.getMessage());
        }
    }
    
    /**
     * Processes a key press and updates the direction.
     * 
     * @param key the key that was pressed
     */
    private void processKey(char key) {
        switch (key) {
            case 'a', 'A' -> lastDirection = Direction.LEFT;
            case 'd', 'D' -> lastDirection = Direction.RIGHT;
        }
    }
    
    /**
     * @return the last direction input by the user
     */
    public Direction getLastDirection() {
        return lastDirection;
    }
    
    /**
     * Clears the last direction after it has been processed.
     */
    public void clearLastDirection() {
        lastDirection = null;
    }
    
    /**
     * Shuts down the input handler thread.
     */
    public void shutdown() {
        running = false;
        inputThread.shutdownNow();
    }
}
