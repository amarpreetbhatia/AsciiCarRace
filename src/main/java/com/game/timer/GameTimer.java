package com.game.timer;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The GameTimer class is responsible for tracking game time.
 * It runs in a separate thread and notifies listeners when time events occur.
 * This class follows the Single Responsibility Principle (SRP) by focusing only on
 * time management, separate from other game concerns.
 */
public class GameTimer implements Runnable {
    // Timer configuration
    private final long durationSeconds;
    private final int updateIntervalMs;
    
    // Timer state
    private Instant startTime;
    private Duration elapsedTime = Duration.ZERO;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread timerThread;
    
    // Listeners
    private final List<GameTimerListener> listeners = new ArrayList<>();
    
    /**
     * Creates a new game timer with the specified duration.
     * 
     * @param durationSeconds the duration of the timer in seconds
     * @param updateIntervalMs how often to update the timer in milliseconds
     */
    public GameTimer(long durationSeconds, int updateIntervalMs) {
        this.durationSeconds = durationSeconds;
        this.updateIntervalMs = updateIntervalMs;
    }
    
    /**
     * Adds a listener to be notified of timer events.
     * 
     * @param listener the listener to add
     */
    public void addListener(GameTimerListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Removes a listener.
     * 
     * @param listener the listener to remove
     */
    public void removeListener(GameTimerListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Starts the timer.
     */
    public void start() {
        if (running.get()) {
            return; // Already running
        }
        
        startTime = Instant.now();
        elapsedTime = Duration.ZERO;
        running.set(true);
        
        timerThread = new Thread(this);
        timerThread.setDaemon(true); // Make it a daemon thread so it doesn't prevent JVM exit
        timerThread.start();
        
        // Notify listeners that the timer has started
        for (GameTimerListener listener : listeners) {
            listener.onTimerStart();
        }
    }
    
    /**
     * Stops the timer.
     */
    public void stop() {
        if (!running.get()) {
            return; // Already stopped
        }
        
        running.set(false);
        
        if (timerThread != null) {
            timerThread.interrupt();
            try {
                timerThread.join(500); // Wait up to 500ms for the thread to terminate
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Notify listeners that the timer has stopped
        for (GameTimerListener listener : listeners) {
            listener.onTimerStop(elapsedTime);
        }
    }
    
    /**
     * Resets the timer.
     */
    public void reset() {
        boolean wasRunning = running.get();
        
        if (wasRunning) {
            stop();
        }
        
        elapsedTime = Duration.ZERO;
        
        if (wasRunning) {
            start();
        }
        
        // Notify listeners that the timer has been reset
        for (GameTimerListener listener : listeners) {
            listener.onTimerReset();
        }
    }
    
    /**
     * The main run method for the timer thread.
     */
    @Override
    public void run() {
        while (running.get()) {
            // Calculate elapsed time
            elapsedTime = Duration.between(startTime, Instant.now());
            
            // Check if time is up
            if (elapsedTime.getSeconds() >= durationSeconds) {
                // Notify listeners that time is up
                for (GameTimerListener listener : listeners) {
                    listener.onTimeUp(elapsedTime);
                }
                
                // Stop the timer
                stop();
                break;
            }
            
            // Notify listeners of the time update
            for (GameTimerListener listener : listeners) {
                listener.onTimerUpdate(elapsedTime);
            }
            
            // Sleep for the update interval
            try {
                Thread.sleep(updateIntervalMs);
            } catch (InterruptedException e) {
                if (running.get()) {
                    // If we're still supposed to be running, log the error
                    System.err.println("Timer thread interrupted: " + e.getMessage());
                }
                // If interrupted while shutting down, exit gracefully
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    /**
     * @return the elapsed time
     */
    public Duration getElapsedTime() {
        return elapsedTime;
    }
    
    /**
     * @return the remaining time in seconds
     */
    public long getRemainingSeconds() {
        return Math.max(0, durationSeconds - elapsedTime.getSeconds());
    }
    
    /**
     * @return true if the timer is running, false otherwise
     */
    public boolean isRunning() {
        return running.get();
    }
    
    /**
     * @return the total duration of the timer in seconds
     */
    public long getDurationSeconds() {
        return durationSeconds;
    }
    
    /**
     * Interface for objects that want to be notified of timer events.
     */
    public interface GameTimerListener {
        /**
         * Called when the timer starts.
         */
        void onTimerStart();
        
        /**
         * Called when the timer is updated.
         * 
         * @param elapsedTime the elapsed time
         */
        void onTimerUpdate(Duration elapsedTime);
        
        /**
         * Called when the timer stops.
         * 
         * @param elapsedTime the final elapsed time
         */
        void onTimerStop(Duration elapsedTime);
        
        /**
         * Called when the timer is reset.
         */
        void onTimerReset();
        
        /**
         * Called when the timer reaches its duration.
         * 
         * @param elapsedTime the final elapsed time
         */
        void onTimeUp(Duration elapsedTime);
    }
}
