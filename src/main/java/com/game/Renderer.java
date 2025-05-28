package com.game;

import com.game.input.InputSource;

import java.util.List;

/**
 * The Renderer class is responsible for displaying the game state to the console.
 * It renders the track, car, hurdles, and game information.
 * This class follows the Single Responsibility Principle (SRP) by focusing only on
 * rendering concerns, decoupled from game logic.
 */
public class Renderer {
    // ANSI color codes for enhanced visual experience
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String BRIGHT_RED = "\u001B[91m";
    
    // Rendering configuration
    private final boolean useColors;
    private final boolean showDebugInfo;
    
    // Visual effect tracking
    private boolean showingBoundaryEffect = false;
    private int boundaryEffectX = -1;
    private int boundaryEffectY = -1;
    private InputSource.Direction boundaryEffectDirection;
    private int boundaryEffectDuration = 0;
    
    /**
     * Creates a new Renderer with default settings.
     */
    public Renderer() {
        this(true, false);
    }
    
    /**
     * Creates a new Renderer with specified settings.
     * 
     * @param useColors whether to use ANSI colors in the output
     * @param showDebugInfo whether to show debug information
     */
    public Renderer(boolean useColors, boolean showDebugInfo) {
        this.useColors = useColors;
        this.showDebugInfo = showDebugInfo;
    }
    
    /**
     * Renders the current game state to the console.
     * 
     * @param car the player's car
     * @param track the game track
     * @param score the current score
     * @param remainingSeconds the remaining game time in seconds
     */
    public void render(Car car, Track track, int score, long remainingSeconds) {
        // Clear console
        clearConsole();
        
        // Build the complete display
        StringBuilder display = new StringBuilder();
        
        // Add header with game info
        appendHeader(display, score, remainingSeconds, car.getSpeed(), track.getDifficultyLevel(), track.getDistanceTraveled());
        
        // Get track data and render it with the car
        appendGameDisplay(display, car, track);
        
        // Add footer with controls
        appendFooter(display);
        
        // Add debug info if enabled
        if (showDebugInfo) {
            appendDebugInfo(display, car, track);
        }
        
        // Print the complete display to console
        System.out.println(display);
        
        // Update visual effects
        updateVisualEffects();
    }
    
    /**
     * Updates any active visual effects.
     */
    private void updateVisualEffects() {
        // Update boundary hit effect
        if (showingBoundaryEffect) {
            boundaryEffectDuration--;
            if (boundaryEffectDuration <= 0) {
                showingBoundaryEffect = false;
            }
        }
    }
    
    /**
     * Shows a visual effect when the car hits a boundary.
     * 
     * @param x the x position of the effect
     * @param y the y position of the effect
     * @param direction the direction the car was trying to move
     */
    public void showBoundaryHitEffect(int x, int y, InputSource.Direction direction) {
        showingBoundaryEffect = true;
        boundaryEffectX = x;
        boundaryEffectY = y;
        boundaryEffectDirection = direction;
        boundaryEffectDuration = 2; // Show for 2 frames
    }
    
    /**
     * Clears the console screen.
     */
    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    
    /**
     * Appends the game header with score and time information.
     * 
     * @param sb the StringBuilder to append to
     * @param score the current score
     * @param remainingSeconds the remaining game time in seconds
     * @param carSpeed the current car speed
     * @param difficultyLevel the current difficulty level
     * @param distance the distance traveled
     */
    private void appendHeader(StringBuilder sb, int score, long remainingSeconds, 
                             int carSpeed, int difficultyLevel, int distance) {
        if (useColors) {
            sb.append(CYAN);
        }
        
        sb.append("╔══════════════════════════════════════════════════════════╗\n");
        sb.append("║                    ASCII CAR RACE                        ║\n");
        sb.append("╠══════════════════════════════════════════════════════════╣\n");
        
        // Format the score with color based on value
        String scoreColor = score >= 0 ? GREEN : RED;
        String scoreDisplay = String.format("%s%d%s", useColors ? scoreColor : "", score, useColors ? CYAN : "");
        
        // Format the time with color based on urgency
        String timeColor = remainingSeconds > 10 ? GREEN : (remainingSeconds > 5 ? YELLOW : RED);
        String timeDisplay = String.format("%s%d%s", useColors ? timeColor : "", remainingSeconds, useColors ? CYAN : "");
        
        sb.append(String.format("║ Score: %-10s Time: %-5s Speed: %-2d Level: %-2d ║\n", 
                               scoreDisplay, timeDisplay, carSpeed, difficultyLevel));
        sb.append("╚══════════════════════════════════════════════════════════╝\n\n");
        
        if (useColors) {
            sb.append(RESET);
        }
    }
    
    /**
     * Appends the main game display with track and car.
     * 
     * @param sb the StringBuilder to append to
     * @param car the player's car
     * @param track the game track
     */
    private void appendGameDisplay(StringBuilder sb, Car car, Track track) {
        char[][] trackData = track.getTrackData();
        int carX = car.getX();
        int carY = car.getY();
        
        for (int y = 0; y < track.getHeight(); y++) {
            for (int x = 0; x < track.getWidth(); x++) {
                if (y == carY && x == carX) {
                    // Render car
                    if (useColors) {
                        sb.append(car.isCrashed() ? RED : GREEN);
                    }
                    sb.append(car.getSymbol());
                    if (useColors) {
                        sb.append(RESET);
                    }
                } else if (showingBoundaryEffect && y == boundaryEffectY && 
                          (x == boundaryEffectX - 1 || x == boundaryEffectX + 1)) {
                    // Show boundary hit effect
                    if (useColors) {
                        sb.append(BRIGHT_RED);
                    }
                    
                    // Show different effect based on direction
                    if (boundaryEffectDirection == InputSource.Direction.LEFT && x == boundaryEffectX - 1) {
                        sb.append('!');
                    } else if (boundaryEffectDirection == InputSource.Direction.RIGHT && x == boundaryEffectX + 1) {
                        sb.append('!');
                    } else {
                        sb.append(trackData[y][x]);
                    }
                    
                    if (useColors) {
                        sb.append(RESET);
                    }
                } else {
                    // Render track element with appropriate color
                    char element = trackData[y][x];
                    if (useColors) {
                        if (element == track.getBoundary()) {
                            sb.append(BLUE);
                        } else if (element == '*') { // Hurdle symbol
                            sb.append(YELLOW);
                        }
                    }
                    sb.append(element);
                    if (useColors) {
                        sb.append(RESET);
                    }
                }
            }
            sb.append('\n');
        }
        sb.append('\n');
    }
    
    /**
     * Appends the footer with control information.
     * 
     * @param sb the StringBuilder to append to
     */
    private void appendFooter(StringBuilder sb) {
        if (useColors) {
            sb.append(CYAN);
        }
        
        sb.append("╔══════════════════════════════════════════════════════════╗\n");
        sb.append("║                      CONTROLS                            ║\n");
        sb.append("╠══════════════════════════════════════════════════════════╣\n");
        sb.append("║ 'a' - Move Left  |  'd' - Move Right                     ║\n");
        sb.append("║ 'w' - Accelerate |  's' - Decelerate                     ║\n");
        sb.append("╚══════════════════════════════════════════════════════════╝\n");
        
        if (useColors) {
            sb.append(RESET);
        }
    }
    
    /**
     * Appends debug information about the game state.
     * 
     * @param sb the StringBuilder to append to
     * @param car the player's car
     * @param track the game track
     */
    private void appendDebugInfo(StringBuilder sb, Car car, Track track) {
        sb.append("\n--- DEBUG INFO ---\n");
        sb.append(String.format("Car position: (%d, %d)\n", car.getX(), car.getY()));
        sb.append(String.format("Car crashed: %s\n", car.isCrashed()));
        sb.append(String.format("Car speed: %d/%d\n", car.getSpeed(), car.getMaxSpeed()));
        sb.append(String.format("Track size: %d x %d\n", track.getWidth(), track.getHeight()));
        sb.append(String.format("Hurdle count: %d\n", track.getHurdles().size()));
        sb.append(String.format("Boundary effect: %s\n", showingBoundaryEffect ? "active" : "inactive"));
    }
    
    /**
     * Displays the game over screen with the final score.
     * 
     * @param finalScore the player's final score
     */
    public void showGameOver(int finalScore) {
        clearConsole();
        
        StringBuilder sb = new StringBuilder();
        
        if (useColors) {
            sb.append(YELLOW);
        }
        
        sb.append("\n\n");
        sb.append("╔══════════════════════════════════════════════════════════╗\n");
        sb.append("║                      GAME OVER                           ║\n");
        sb.append("╠══════════════════════════════════════════════════════════╣\n");
        
        // Format the score with color based on value
        String scoreColor = finalScore >= 500 ? GREEN : (finalScore >= 100 ? YELLOW : RED);
        String scoreDisplay = String.format("%s%d%s", useColors ? scoreColor : "", finalScore, useColors ? YELLOW : "");
        
        sb.append(String.format("║              Final Score: %-10s              ║\n", scoreDisplay));
        sb.append("╠══════════════════════════════════════════════════════════╣\n");
        sb.append("║              Thank you for playing!                      ║\n");
        sb.append("╚══════════════════════════════════════════════════════════╝\n");
        
        if (useColors) {
            sb.append(RESET);
        }
        
        System.out.println(sb);
    }
    
    /**
     * Renders a specific section of the track for preview or debugging.
     * 
     * @param track the game track
     * @param startRow the starting row index
     * @param endRow the ending row index
     */
    public void renderTrackSection(Track track, int startRow, int endRow) {
        char[][] section = track.getSection(startRow, endRow);
        
        StringBuilder sb = new StringBuilder();
        sb.append("Track section preview:\n");
        
        for (char[] row : section) {
            for (char c : row) {
                if (useColors) {
                    if (c == track.getBoundary()) {
                        sb.append(BLUE);
                    } else if (c == '*') { // Hurdle symbol
                        sb.append(YELLOW);
                    }
                }
                sb.append(c);
                if (useColors) {
                    sb.append(RESET);
                }
            }
            sb.append('\n');
        }
        
        System.out.println(sb);
    }
}
