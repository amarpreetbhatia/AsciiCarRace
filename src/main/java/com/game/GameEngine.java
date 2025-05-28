package com.game;

import com.game.input.InputHandler;
import com.game.input.InputSource;

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
    private final int RENDER_INTERVAL = 1; // Render every tick for smooth animation
    private final int FRAME_RATE_LIMIT_MS = 33; // ~30 FPS limit
    
    // Animation and rendering
    private long lastRenderTime = 0;
    private boolean firstRender = true;
    
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
        private final int carSpeed;
        private final int difficultyLevel;
        private final int carPositionX;
        private final int carPositionY;
        
        public GameState(int score, long remainingSeconds, boolean isGameOver, 
                         int carSpeed, int difficultyLevel, int carPositionX, int carPositionY) {
            this.score = score;
            this.remainingSeconds = remainingSeconds;
            this.isGameOver = isGameOver;
            this.carSpeed = carSpeed;
            this.difficultyLevel = difficultyLevel;
            this.carPositionX = carPositionX;
            this.carPositionY = carPositionY;
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
        
        public int getCarSpeed() {
            return carSpeed;
        }
        
        public int getDifficultyLevel() {
            return difficultyLevel;
        }
        
        public int getCarPositionX() {
            return carPositionX;
        }
        
        public int getCarPositionY() {
            return carPositionY;
        }
    }

    /**
     * Creates a new GameEngine with the specified components.
     * 
     * @param car the player's car
     * @param track the game track
     * @param inputHandler the input handler
     * @param renderer the renderer
     */
    public GameEngine(Car car, Track track, InputHandler inputHandler, Renderer renderer) {
        this.car = car;
        this.track = track;
        this.inputHandler = inputHandler;
        this.renderer = renderer;
        
        // Set car boundaries based on track boundaries
        updateCarBoundaries();
    }
    
    /**
     * Updates the car's movement boundaries based on the track boundaries.
     * This ensures the car cannot move outside the track.
     */
    private void updateCarBoundaries() {
        // Set boundaries to keep car inside the track (accounting for track boundaries)
        int leftBoundary = 1; // One position right of the left boundary
        int rightBoundary = track.getWidth() - 2; // One position left of the right boundary
        
        car.setBoundaries(leftBoundary, rightBoundary);
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
        
        // Position car near bottom center of track
        int startX = track.getWidth() / 2;
        int startY = track.getHeight() - 3;
        car.reset(startX, startY);
        
        // Update car boundaries
        updateCarBoundaries();
        
        // Reset rendering state
        lastRenderTime = 0;
        firstRender = true;
        
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
        
        // Show initial game state
        render();
        
        while (running.get()) {
            // Calculate time since last tick
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - lastTickTime;
            
            // If it's time for the next tick
            if (elapsedTime >= TICK_RATE_MS) {
                // Update game state
                update();
                
                // Render based on render interval and frame rate limit
                if (tickCount % RENDER_INTERVAL == 0) {
                    long timeSinceLastRender = currentTime - lastRenderTime;
                    if (timeSinceLastRender >= FRAME_RATE_LIMIT_MS || firstRender) {
                        render();
                        lastRenderTime = currentTime;
                        firstRender = false;
                    }
                }
                
                // Check if game time is up
                Duration elapsed = Duration.between(gameStartTime, Instant.now());
                if (elapsed.getSeconds() >= GAME_DURATION_SECONDS) {
                    endGame();
                    break;
                }
                
                // Update tick tracking
                lastTickTime = currentTime;
                tickCount++;
                
                // Notify observers of game state change
                notifyObservers();
            }
            
            // Sleep to reduce CPU usage
            try {
                long sleepTime = Math.max(1, TICK_RATE_MS - (System.currentTimeMillis() - lastTickTime));
                Thread.sleep(sleepTime);
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
        
        // Update car state
        car.update();
        
        // Process player input
        handleInput();
        
        // Ensure car stays within boundaries after movement
        enforceCarBoundaries();
        
        // Move the track (scrolling effect)
        track.scroll();
        
        // Add new hurdles randomly based on difficulty
        int hurdleChance = 2 + track.getDifficultyLevel();
        if (random.nextInt(10) < hurdleChance) { // Chance increases with difficulty
            track.addHurdle();
        }
        
        // Check for collisions
        if (track.checkCollision(car)) {
            car.crash();
            // Penalty for hitting hurdles
            score -= 50;
        } else if (!car.isCrashed()) {
            // Increment score (distance traveled + speed bonus)
            score += car.getSpeed();
        }
    }

    /**
     * Ensures the car stays within the track boundaries.
     * This is a safety check in addition to the boundary checks in the Car class.
     */
    private void enforceCarBoundaries() {
        int x = car.getX();
        int leftBoundary = 1; // One position right of the left boundary
        int rightBoundary = track.getWidth() - 2; // One position left of the right boundary
        
        // If car somehow got outside boundaries, move it back
        if (x < leftBoundary) {
            car.reset(leftBoundary, car.getY());
        } else if (x > rightBoundary) {
            car.reset(rightBoundary, car.getY());
        }
    }

    /**
     * Processes player input to control the car.
     * Handles movement with boundary checks to prevent the car from leaving the track.
     */
    private void handleInput() {
        InputSource.Direction direction = inputHandler.getLastDirection();
        if (direction != null) {
            boolean moved = false;
            
            switch (direction) {
                case LEFT:
                    moved = car.moveLeft();
                    if (!moved && !car.isCrashed()) {
                        // If car couldn't move left, it's at the boundary
                        renderer.showBoundaryHitEffect(car.getX(), car.getY(), direction);
                    }
                    break;
                    
                case RIGHT:
                    moved = car.moveRight();
                    if (!moved && !car.isCrashed()) {
                        // If car couldn't move right, it's at the boundary
                        renderer.showBoundaryHitEffect(car.getX(), car.getY(), direction);
                    }
                    break;
                    
                case UP:
                    car.accelerate();
                    break;
                    
                case DOWN:
                    car.decelerate();
                    break;
            }
            
            // Clear the input after processing
            inputHandler.clearLastDirection();
        }
    }

    /**
     * Renders the current game state.
     * This method uses the Renderer to display the game state to the console.
     * The Renderer handles clearing the console for smooth animation.
     */
    private void render() {
        if (!running.get()) return;
        
        // Calculate remaining time
        Duration elapsed = Duration.between(gameStartTime, Instant.now());
        long remainingSeconds = Math.max(0, GAME_DURATION_SECONDS - elapsed.getSeconds());
        
        // Render the game (the renderer will clear the console)
        renderer.render(car, track, score, remainingSeconds);
    }

    /**
     * Ends the game and displays the final score.
     */
    private void endGame() {
        running.set(false);
        inputHandler.shutdown();
        
        // Show game over screen
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
        GameState state = new GameState(
            score, 
            remainingSeconds, 
            !running.get(),
            car.getSpeed(),
            track.getDifficultyLevel(),
            car.getX(),
            car.getY()
        );
        
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
