package com.game;

/**
 * The Renderer class is responsible for displaying the game state to the console.
 * It renders the track, car, hurdles, and game information.
 * This class follows the Single Responsibility Principle (SRP) by focusing only on
 * rendering concerns, decoupled from game logic.
 */
public class Renderer {
    
    /**
     * Renders the current game state to the console.
     * 
     * @param car the player's car
     * @param track the game track
     * @param score the current score
     * @param remainingSeconds the remaining game time in seconds
     */
    public void render(Car car, Track track, int score, long remainingSeconds) {
        // Clear console (ANSI escape code)
        System.out.print("\033[H\033[2J");
        System.out.flush();
        
        // Get the track data
        char[][] trackData = track.getTrackData();
        
        // Add car to the display (without modifying the track data)
        int carX = car.getX();
        int carY = car.getY();
        
        // Create a StringBuilder for the entire display
        StringBuilder sb = new StringBuilder();
        
        // Add header with game info
        sb.append("ASCII Car Race - Score: ").append(score)
          .append(" | Time: ").append(remainingSeconds).append("s")
          .append(" | Difficulty: ").append(track.getDifficultyLevel())
          .append(" | Distance: ").append(track.getDistanceTraveled())
          .append("\n");
        
        // Add the game display
        for (int y = 0; y < track.getHeight(); y++) {
            for (int x = 0; x < track.getWidth(); x++) {
                // If this is the car's position, render the car instead of the track element
                if (y == carY && x == carX) {
                    sb.append(car.getSymbol());
                } else {
                    sb.append(trackData[y][x]);
                }
            }
            sb.append('\n');
        }
        
        // Add controls reminder
        sb.append("\nControls: 'a' for left, 'd' for right\n");
        
        // Print to console
        System.out.println(sb);
    }
    
    /**
     * Displays the game over screen with the final score.
     * 
     * @param finalScore the player's final score
     */
    public void showGameOver(int finalScore) {
        System.out.println("\n\n");
        System.out.println("=========================");
        System.out.println("       GAME OVER        ");
        System.out.println("=========================");
        System.out.println("Final Score: " + finalScore);
        System.out.println("=========================");
        System.out.println("\nThank you for playing!");
    }
}
