package com.game;

import com.game.hurdle.Hurdle;
import com.game.hurdle.HurdleManager;
import com.game.hurdle.StandardHurdle;

import java.util.List;
import java.util.Random;

/**
 * The Track class represents the game track with boundaries and hurdles.
 * It manages the track state, scrolling, and collision detection.
 * This class follows the Single Responsibility Principle (SRP) by focusing only on
 * track data and logic, delegating hurdle management to the HurdleManager.
 */
public class Track {
    private final int width;
    private final int height;
    private final Random random = new Random();
    private final char BOUNDARY = '|';
    private final char EMPTY_SPACE = ' ';
    
    // Track data representation
    private char[][] trackData;
    
    // Track difficulty parameters
    private int difficultyLevel = 1;
    private final int MAX_DIFFICULTY = 5;
    private int distanceTraveled = 0;
    private final int DIFFICULTY_INCREASE_THRESHOLD = 500;
    
    // Hurdle management
    private final HurdleManager hurdleManager;
    
    /**
     * Creates a new track with the specified dimensions.
     * 
     * @param width the width of the track
     * @param height the height of the track
     */
    public Track(int width, int height) {
        this.width = width;
        this.height = height;
        this.trackData = new char[height][width];
        this.hurdleManager = new HurdleManager(width, height);
    }
    
    /**
     * Initializes the track with initial hurdles and boundaries.
     */
    public void initialize() {
        // Clear existing track data
        clearTrackData();
        
        // Set up track boundaries
        setupBoundaries();
        
        // Clear existing hurdles
        hurdleManager.clearHurdles();
        
        // Add some initial hurdles
        for (int i = 0; i < 5; i++) {
            int x = random.nextInt(width - 4) + 2; // Keep away from boundaries
            hurdleManager.addHurdle(new StandardHurdle(x, i * 3 + 5)); // Space them out vertically
        }
        
        // Reset difficulty and distance
        difficultyLevel = 1;
        distanceTraveled = 0;
        
        // Update the track data with hurdles
        updateTrackData();
    }
    
    /**
     * Clears the track data array.
     */
    private void clearTrackData() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                trackData[y][x] = EMPTY_SPACE;
            }
        }
    }
    
    /**
     * Sets up the track boundaries.
     */
    private void setupBoundaries() {
        for (int y = 0; y < height; y++) {
            trackData[y][0] = BOUNDARY;
            trackData[y][width - 1] = BOUNDARY;
        }
    }
    
    /**
     * Updates the track data representation with current hurdles.
     * This method should be called after any change to the track state.
     */
    private void updateTrackData() {
        // Clear the track (except boundaries)
        for (int y = 0; y < height; y++) {
            for (int x = 1; x < width - 1; x++) {
                trackData[y][x] = EMPTY_SPACE;
            }
        }
        
        // Add hurdles to the track data
        List<Hurdle> hurdles = hurdleManager.getActiveHurdles();
        for (Hurdle hurdle : hurdles) {
            int x = hurdle.getX();
            int y = hurdle.getY();
            if (isValidPosition(x, y)) {
                trackData[y][x] = hurdle.getSymbol();
            }
        }
    }
    
    /**
     * Checks if a position is valid within the track bounds.
     * 
     * @param x the x coordinate
     * @param y the y coordinate
     * @return true if the position is valid, false otherwise
     */
    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    
    /**
     * Adds a new hurdle at the top of the track.
     * This is a convenience method that delegates to the HurdleManager.
     */
    public void addHurdle() {
        // Let the HurdleManager handle hurdle creation
        hurdleManager.addHurdle(new StandardHurdle(random.nextInt(width - 4) + 2, 0));
        updateTrackData();
    }
    
    /**
     * Scrolls the track by moving all hurdles down.
     * This simulates the car moving forward.
     */
    public void scroll() {
        // Let the HurdleManager handle hurdle movement
        int scoreFromHurdles = hurdleManager.update(difficultyLevel);
        
        // Increment distance traveled
        distanceTraveled++;
        
        // Check if difficulty should increase
        if (distanceTraveled % DIFFICULTY_INCREASE_THRESHOLD == 0 && difficultyLevel < MAX_DIFFICULTY) {
            difficultyLevel++;
        }
        
        // Update the track data
        updateTrackData();
    }
    
    /**
     * Checks if the car has collided with any hurdle or boundary.
     * 
     * @param car the player's car
     * @return true if collision detected, false otherwise
     */
    public boolean checkCollision(Car car) {
        int carX = car.getX();
        int carY = car.getY();
        
        // Check if position is valid
        if (!isValidPosition(carX, carY)) {
            return true;
        }
        
        // Check collision with boundaries
        if (trackData[carY][carX] == BOUNDARY) {
            return true;
        }
        
        // Check collision with hurdles
        Hurdle collidedHurdle = hurdleManager.checkCollision(carX, carY);
        return collidedHurdle != null;
    }
    
    /**
     * Gets the hurdle that the car has collided with, if any.
     * 
     * @param car the player's car
     * @return the hurdle that the car collided with, or null if none
     */
    public Hurdle getCollidedHurdle(Car car) {
        return hurdleManager.checkCollision(car.getX(), car.getY());
    }
    
    /**
     * Gets a specific row of the track.
     * 
     * @param rowIndex the index of the row to retrieve
     * @return the characters in the specified row, or null if the index is invalid
     */
    public char[] getRow(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < height) {
            return trackData[rowIndex].clone();
        }
        return null;
    }
    
    /**
     * Gets a section of the track.
     * 
     * @param startRow the starting row index (inclusive)
     * @param endRow the ending row index (exclusive)
     * @return a 2D array containing the specified section of the track
     */
    public char[][] getSection(int startRow, int endRow) {
        startRow = Math.max(0, startRow);
        endRow = Math.min(height, endRow);
        int sectionHeight = endRow - startRow;
        
        if (sectionHeight <= 0) {
            return new char[0][0];
        }
        
        char[][] section = new char[sectionHeight][width];
        for (int i = 0; i < sectionHeight; i++) {
            System.arraycopy(trackData[startRow + i], 0, section[i], 0, width);
        }
        
        return section;
    }
    
    /**
     * Gets the entire track data.
     * 
     * @return a 2D array containing the entire track
     */
    public char[][] getTrackData() {
        char[][] copy = new char[height][width];
        for (int i = 0; i < height; i++) {
            System.arraycopy(trackData[i], 0, copy[i], 0, width);
        }
        return copy;
    }
    
    /**
     * @return the width of the track
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * @return the height of the track
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * @return the list of hurdles on the track
     */
    public List<Hurdle> getHurdles() {
        return hurdleManager.getActiveHurdles();
    }
    
    /**
     * @return the boundary character
     */
    public char getBoundary() {
        return BOUNDARY;
    }
    
    /**
     * @return the current difficulty level
     */
    public int getDifficultyLevel() {
        return difficultyLevel;
    }
    
    /**
     * @return the distance traveled
     */
    public int getDistanceTraveled() {
        return distanceTraveled;
    }
    
    /**
     * @return the hurdle manager
     */
    public HurdleManager getHurdleManager() {
        return hurdleManager;
    }
}
