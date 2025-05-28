package com.game;

import com.game.input.AIInputSource;
import com.game.input.InputHandler;
import com.game.input.KeyboardInputSource;

/**
 * Entry point for the ASCII Car Race Game.
 * This class initializes the game components and starts the game loop.
 */
public class Main {
    public static void main(String[] args) {
        // Parse command line arguments
        boolean useAI = false;
        boolean useColors = true;
        boolean showDebug = false;
        
        for (String arg : args) {
            if (arg.equalsIgnoreCase("--ai")) {
                useAI = true;
            } else if (arg.equalsIgnoreCase("--no-color")) {
                useColors = false;
            } else if (arg.equalsIgnoreCase("--debug")) {
                showDebug = true;
            }
        }
        
        // Create game components
        Car car = new Car(10, 5); // Starting position
        Track track = new Track(20, 40); // Width and height
        
        // Create input handler based on command line arguments
        InputHandler inputHandler;
        if (useAI) {
            inputHandler = new InputHandler(new AIInputSource());
        } else {
            inputHandler = new InputHandler(new KeyboardInputSource());
        }
        
        // Create renderer with colors enabled/disabled based on command line arguments
        Renderer renderer = new Renderer(useColors, showDebug);
        
        // Create and configure the game engine
        GameEngine gameEngine = new GameEngine(car, track, inputHandler, renderer);
        
        // Example of adding a game state observer (demonstrating OCP)
        gameEngine.addObserver(new GameEngine.GameStateObserver() {
            @Override
            public void onGameStateUpdate(GameEngine.GameState gameState) {
                // This could be extended with additional functionality without modifying GameEngine
                if (gameState.isGameOver()) {
                    System.out.println("Game has ended with score: " + gameState.getScore());
                }
            }
        });
        
        // Display welcome message
        System.out.println("Starting ASCII Car Race Game...");
        if (useAI) {
            System.out.println("AI mode enabled - computer will control the car");
        } else {
            System.out.println("Use 'a' and 'd' to move left and right");
            System.out.println("Use 'w' and 's' to accelerate and decelerate");
            System.out.println("Press Enter after each key press");
        }
        System.out.println("Game will start in 3 seconds...");
        
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Start the game
        gameEngine.start();
        
        // Add shutdown hook to ensure clean exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (gameEngine.isRunning()) {
                gameEngine.stop();
            }
        }));
    }
}
