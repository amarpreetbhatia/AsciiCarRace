package com.game;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The GameEngine class is responsible for the game loop and game logic.
 * It implements Runnable to allow execution in a separate thread.
 * This class follows the Open/Closed Principle (OCP) by allowing extension
 * through game state observers without modifying the core game loop.
 */
public class GameEngine implements Runnable {
    // Game components
    private final Car car;
    private final Track track;
    private final InputHandler inputHandler;
    private final Renderer renderer;
    private final Random random = new Random();
    
    // Game state
    private final AtomicBoolean running = new AtomicBoolean(false);
    private int score = 0;
    private Instant gameStartTime;
    
    // Game configuration
    private final long GAME_DURATION_SECONDS = 60; // 1 minute game
    private final int TICK_RATE_MS = 50; // 20 ticks per second
    private final int RENDER_INTERVAL = 2; // Render every 2 ticks (10 FPS)
    
    // Thread for game loop
    private Thread gameThread;
    
    // List of game state observers (for OCP)
    private final List<GameStateObserver> observers = new ArrayList<>();

    /**
     * Interface for game state observers following the Observer pattern.
     * This allows extending the game with new features without modifying the GameEngine.
     */
    public interface GameStateObserver {
        void onGameStateUpdate(GameState gameState);
    }
    
    /**
     * Immutable class representing the current game state.
     * This encapsulates all relevant game state information for observers.
     */
    public static class GameState {
        private final int score;
        private final long remainingSeconds;
        private final boolean isGameOver;
        
        public GameState(int score, long remainingSeconds, boolean isGameOver) {
            this.score = score;
            this.remainingSeconds = remainingSeconds;
            this.isGameOver = isGameOver;
        }
        
        public int getScore() {
            return score;
        }
        
        public long getRemainingSeconds() {
            return remainingSeconds;
        }
        
        public boolean isGameOver() {
            return isGameOver;
        }
    }

    public GameEngine(Car car, Track track, InputHandler inputHandler, Renderer renderer) {
        this.car = car;
        this.track = track;
        this.inputHandler = inputHandler;
        this.renderer = renderer;
    }
    
    /**
     * Adds a game state observer.
     * This allows extending the game's functionality without modifying this class (OCP).
     * 
     * @param observer the observer to add
     */
    public void addObserver(GameStateObserver observer) {
        observers.add(observer);
    }
    
    /**
     * Removes a game state observer.
     * 
     * @param observer the observer to remove
     */
    public void removeObserver(GameStateObserver observer) {
        observers.remove(observer);
    }

    /**
     * Starts the game loop in a separate thread.
     */
    public void start() {
        if (running.get()) {
            return; // Already running
        }
        
        // Initialize game components
        gameStartTime = Instant.now();
        inputHandler.initialize();
        track.initialize();
        
        // Start the game loop in a new thread
        running.set(true);
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    /**
     * Stops the game loop.
     */
    public void stop() {
        running.set(false);
        if (gameThread != null) {
            try {
                gameThread.join(1000); // Wait up to 1 second for the thread to terminate
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * The main game loop implementation.
     * This method runs in a separate thread when start() is called.
     */
    @Override
    public void run() {
        int tickCount = 0;
        long lastTickTime = System.currentTimeMillis();
        
        while (running.get()) {
            // Calculate time since last tick
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - lastTickTime;
            
            // If it's time for the next tick
            if (elapsedTime >= TICK_RATE_MS) {
                // Update game state
                update();
                
                // Render every RENDER_INTERVAL ticks
                if (tickCount % RENDER_INTERVAL == 0) {
                    render();
                }
                
                // Check if game time is up
                Duration elapsed = Duration.between(gameStartTime, Instant.now());
                if (elapsed.getSeconds() >= GAME_DURATION_SECONDS) {
                    endGame();
                }
                
                // Update tick tracking
                lastTickTime = currentTime;
                tickCount++;
                
                // Notify observers of game state change
                notifyObservers();
            }
            
            // Sleep to reduce CPU usage
            try {
                Thread.sleep(Math.max(1, TICK_RATE_MS - elapsedTime));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Updates the game state including car position, track scrolling, and collision detection.
     */
    private void update() {
        if (!running.get()) return;
        
        // Process player input
        handleInput();
        
        // Move the track (scrolling effect)
        track.scroll();
        
        // Add new hurdles randomly
        if (random.nextInt(10) < 3) { // 30% chance each update
            track.addHurdle();
        }
        
        // Check for collisions
        if (track.checkCollision(car)) {
            car.crash();
            // Penalty for hitting hurdles
            score -= 50;
        } else {
            // Increment score (distance traveled)
            score++;
        }
    }

    /**
     * Processes player input to control the car.
     */
    private void handleInput() {
        InputHandler.Direction direction = inputHandler.getLastDirection();
        if (direction != null) {
            switch (direction) {
                case LEFT -> car.moveLeft();
                case RIGHT -> car.moveRight();
            }
            inputHandler.clearLastDirection();
        }
    }

    /**
     * Renders the current game state.
     */
    private void render() {
        if (!running.get()) return;
        
        // Calculate remaining time
        Duration elapsed = Duration.between(gameStartTime, Instant.now());
        long remainingSeconds = GAME_DURATION_SECONDS - elapsed.getSeconds();
        
        // Render the game
        renderer.render(car, track, score, remainingSeconds);
    }

    /**
     * Ends the game and displays the final score.
     */
    private void endGame() {
        running.set(false);
        inputHandler.shutdown();
        renderer.showGameOver(score);
        
        // Notify observers of game over
        notifyObservers();
    }
    
    /**
     * Notifies all observers of the current game state.
     */
    private void notifyObservers() {
        Duration elapsed = Duration.between(gameStartTime, Instant.now());
        long remainingSeconds = Math.max(0, GAME_DURATION_SECONDS - elapsed.getSeconds());
        GameState state = new GameState(score, remainingSeconds, !running.get());
        
        for (GameStateObserver observer : observers) {
            observer.onGameStateUpdate(state);
        }
    }
    
    /**
     * @return the current score
     */
    public int getScore() {
        return score;
    }
    
    /**
     * @return true if the game is running, false otherwise
     */
    public boolean isRunning() {
        return running.get();
    }
}
