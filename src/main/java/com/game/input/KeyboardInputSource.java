package com.game.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Implementation of InputSource that reads from the keyboard.
 * This class runs in its own thread to continuously listen for keyboard input.
 */
public class KeyboardInputSource implements InputSource, Runnable {
    // Thread-safe storage for the current direction
    private final AtomicReference<Direction> currentDirection = new AtomicReference<>(null);
    
    // Queue for buffering inputs when they come in faster than they're processed
    private final BlockingQueue<Character> inputQueue = new LinkedBlockingQueue<>();
    
    // Thread control
    private volatile boolean running = false;
    private Thread inputThread;
    
    /**
     * Maps a key character to a direction.
     * 
     * @param key the key character
     * @return the corresponding direction, or null if not a direction key
     */
    private Direction directionFromKey(char key) {
        return switch (Character.toLowerCase(key)) {
            case 'a' -> Direction.LEFT;
            case 'd' -> Direction.RIGHT;
            case 'w' -> Direction.UP;
            case 's' -> Direction.DOWN;
            default -> null;
        };
    }
    
    @Override
    public void initialize() {
        if (running) {
            return; // Already running
        }
        
        running = true;
        inputThread = new Thread(this);
        inputThread.setDaemon(true); // Make it a daemon thread so it doesn't prevent JVM exit
        inputThread.start();
        
        // Print instructions
        System.out.println("Controls: 'a' for left, 'd' for right, 'w' for accelerate, 's' for decelerate");
    }
    
    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (running) {
                try {
                    // Check if there's input available
                    if (reader.ready()) {
                        int input = reader.read();
                        if (input != -1) {
                            // Add to the queue for processing
                            inputQueue.offer((char) input);
                        }
                    }
                    
                    // Process any queued inputs
                    processInputQueue();
                    
                    // Small delay to prevent CPU hogging
                    Thread.sleep(10);
                } catch (IOException | InterruptedException e) {
                    if (running) {
                        System.err.println("Error reading input: " + e.getMessage());
                    }
                    // If interrupted while shutting down, exit gracefully
                    if (Thread.interrupted()) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Fatal error in input handler: " + e.getMessage());
        }
    }
    
    /**
     * Processes any queued input characters.
     */
    private void processInputQueue() {
        Character key;
        while ((key = inputQueue.poll()) != null) {
            processKey(key);
        }
    }
    
    /**
     * Processes a key press and updates the direction.
     * 
     * @param key the key that was pressed
     */
    private void processKey(char key) {
        Direction direction = directionFromKey(key);
        if (direction != null) {
            currentDirection.set(direction);
        }
    }
    
    @Override
    public Direction getCurrentDirection() {
        return currentDirection.get();
    }
    
    @Override
    public void clearCurrentDirection() {
        currentDirection.set(null);
    }
    
    @Override
    public boolean isRunning() {
        return running;
    }
    
    @Override
    public void shutdown() {
        running = false;
        if (inputThread != null) {
            inputThread.interrupt();
            try {
                inputThread.join(500); // Wait up to 500ms for the thread to terminate
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
