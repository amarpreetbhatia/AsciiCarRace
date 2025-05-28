package com.game;

/**
 * The Renderer class is responsible for displaying the game state to the console.
 * It renders the track, car, hurdles, and game information.
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
        
        // Create a 2D representation of the game
        char[][] display = new char[track.getHeight()][track.getWidth()];
        
        // Fill with empty spaces
        for (int y = 0; y < track.getHeight(); y++) {
            for (int x = 0; x < track.getWidth(); x++) {
                display[y][x] = ' ';
            }
        }
        
        // Add track boundaries
        for (int y = 0; y < track.getHeight(); y++) {
            display[y][0] = track.getBoundary();
            display[y][track.getWidth() - 1] = track.getBoundary();
        }
        
        // Add hurdles
        for (Hurdle hurdle : track.getHurdles()) {
            int x = hurdle.getX();
            int y = hurdle.getY();
            if (y >= 0 && y < track.getHeight() && x >= 0 && x < track.getWidth()) {
                display[y][x] = hurdle.getSymbol();
            }
        }
        
        // Add car
        if (car.getY() >= 0 && car.getY() < track.getHeight() && 
            car.getX() >= 0 && car.getX() < track.getWidth()) {
            display[car.getY()][car.getX()] = car.getSymbol();
        }
        
        // Render the display
        StringBuilder sb = new StringBuilder();
        
        // Add header with game info
        sb.append("ASCII Car Race - Score: ").append(score)
          .append(" | Time: ").append(remainingSeconds).append("s\n");
        
        // Add the game display
        for (int y = 0; y < track.getHeight(); y++) {
            for (int x = 0; x < track.getWidth(); x++) {
                sb.append(display[y][x]);
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
