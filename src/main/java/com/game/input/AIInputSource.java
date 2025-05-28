package com.game.input;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

/**
 * An implementation of InputSource that simulates AI-controlled input.
 * This demonstrates the Liskov Substitution Principle (LSP) by providing
 * a different implementation that can be used in place of KeyboardInputSource.
 */
public class AIInputSource implements InputSource, Runnable {
    // Thread-safe storage for the current direction
    private final AtomicReference<Direction> currentDirection = new AtomicReference<>(null);
    
    // Thread control
    private volatile boolean running = false;
    private Thread aiThread;
    
    // AI decision making
    private final Random random = new Random();
    private final int DECISION_INTERVAL_MS = 500; // Make a new decision every 500ms
    
    @Override
    public void initialize() {
        if (running) {
            return; // Already running
        }
        
        running = true;
        aiThread = new Thread(this);
        aiThread.setDaemon(true);
        aiThread.start();
        
        System.out.println("AI Input Source activated");
    }
    
    @Override
    public void run() {
        while (running) {
            try {
                // Make a new decision
                makeDecision();
                
                // Wait before making another decision
                Thread.sleep(DECISION_INTERVAL_MS);
            } catch (InterruptedException e) {
                if (running) {
                    System.err.println("AI input source interrupted: " + e.getMessage());
                }
                // If interrupted while shutting down, exit gracefully
                if (Thread.interrupted()) {
                    break;
                }
            }
        }
    }
    
    /**
     * Makes an AI decision about which direction to move.
     */
    private void makeDecision() {
        // Simple AI logic: randomly choose a direction
        int decision = random.nextInt(10);
        
        Direction newDirection = switch (decision) {
            case 0, 1, 2 -> Direction.LEFT;  // 30% chance to move left
            case 3, 4, 5 -> Direction.RIGHT; // 30% chance to move right
            case 6, 7 -> Direction.UP;       // 20% chance to accelerate
            case 8 -> Direction.DOWN;        // 10% chance to decelerate
            default -> null;                 // 10% chance to do nothing
        };
        
        currentDirection.set(newDirection);
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
        if (aiThread != null) {
            aiThread.interrupt();
            try {
                aiThread.join(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
