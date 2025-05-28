package com.game;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The GameEngine class is responsible for the game loop and game logic.
 * It updates the game state, checks for collisions, and manages the game timer.
 */
public class GameEngine {
    private final Car car;
    private final Track track;
    private final InputHandler inputHandler;
    private final Renderer renderer;
    private final Random random = new Random();
    private boolean isRunning = false;
    private int score = 0;
    private Instant gameStartTime;
    private final long GAME_DURATION_SECONDS = 60; // 1 minute game
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public GameEngine(Car car, Track track, InputHandler inputHandler, Renderer renderer) {
        this.car = car;
        this.track = track;
        this.inputHandler = inputHandler;
        this.renderer = renderer;
    }

    /**
     * Starts the game loop and initializes the game components.
     */
    public void start() {
        isRunning = true;
        gameStartTime = Instant.now();
        
        // Set up input handling
        inputHandler.initialize();
        
        // Initialize track with hurdles
        track.initialize();
        
        // Schedule game update task (runs every 100ms)
        scheduler.scheduleAtFixedRate(this::update, 0, 100, TimeUnit.MILLISECONDS);
        
        // Schedule rendering task (runs every 50ms for smoother display)
        scheduler.scheduleAtFixedRate(this::render, 0, 50, TimeUnit.MILLISECONDS);
    }

    /**
     * Updates the game state including car position, track scrolling, and collision detection.
     */
    private void update() {
        if (!isRunning) return;
        
        // Check if game time is up
        Duration elapsed = Duration.between(gameStartTime, Instant.now());
        if (elapsed.getSeconds() >= GAME_DURATION_SECONDS) {
            endGame();
            return;
        }
        
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
        if (!isRunning) return;
        
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
        isRunning = false;
        inputHandler.shutdown();
        scheduler.shutdown();
        renderer.showGameOver(score);
    }
}
