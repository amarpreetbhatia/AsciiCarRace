package com.game.hurdle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Manages the creation, movement, and removal of hurdles on the track.
 * This class follows the Single Responsibility Principle (SRP) by focusing only on
 * hurdle management, separate from track and game logic.
 */
public class HurdleManager {
    private final List<Hurdle> activeHurdles = new ArrayList<>();
    private final HurdleFactory hurdleFactory;
    private final Random random = new Random();
    
    // Track dimensions
    private final int trackWidth;
    private final int trackHeight;
    
    // Hurdle generation parameters
    private int hurdleGenerationCounter = 0;
    private int hurdleGenerationInterval = 10; // Generate hurdles every 10 updates by default
    
    /**
     * Creates a new hurdle manager for a track with the specified dimensions.
     * 
     * @param trackWidth the width of the track
     * @param trackHeight the height of the track
     */
    public HurdleManager(int trackWidth, int trackHeight) {
        this.trackWidth = trackWidth;
        this.trackHeight = trackHeight;
        this.hurdleFactory = new HurdleFactory(trackWidth);
    }
    
    /**
     * Updates the hurdle manager, moving all hurdles and potentially generating new ones.
     * 
     * @param difficultyLevel the current difficulty level
     * @return the score value of any hurdles that moved off the bottom of the track
     */
    public int update(int difficultyLevel) {
        int scoreValue = 0;
        
        // Move all hurdles
        for (Hurdle hurdle : activeHurdles) {
            hurdle.move();
        }
        
        // Remove hurdles that have moved off the bottom of the track
        Iterator<Hurdle> iterator = activeHurdles.iterator();
        while (iterator.hasNext()) {
            Hurdle hurdle = iterator.next();
            if (hurdle.getY() >= trackHeight) {
                scoreValue += hurdle.getScoreValue(); // Award points for avoided hurdles
                iterator.remove();
            }
        }
        
        // Potentially generate new hurdles
        hurdleGenerationCounter++;
        
        // Adjust generation interval based on difficulty
        hurdleGenerationInterval = Math.max(3, 10 - difficultyLevel);
        
        if (hurdleGenerationCounter >= hurdleGenerationInterval) {
            generateHurdles(difficultyLevel);
            hurdleGenerationCounter = 0;
        }
        
        return scoreValue;
    }
    
    /**
     * Generates new hurdles based on the current difficulty level.
     * 
     * @param difficultyLevel the current difficulty level
     */
    private void generateHurdles(int difficultyLevel) {
        // Determine if we should generate a pattern or single hurdle
        boolean generatePattern = random.nextInt(100) < difficultyLevel * 10;
        
        if (generatePattern && difficultyLevel >= 2) {
            // Generate a pattern of hurdles
            Hurdle[] pattern = hurdleFactory.createHurdlePattern(difficultyLevel);
            for (Hurdle hurdle : pattern) {
                activeHurdles.add(hurdle);
            }
        } else {
            // Generate a single random hurdle
            activeHurdles.add(hurdleFactory.createRandomHurdle(difficultyLevel));
        }
    }
    
    /**
     * Adds a specific hurdle to the track.
     * 
     * @param hurdle the hurdle to add
     */
    public void addHurdle(Hurdle hurdle) {
        activeHurdles.add(hurdle);
    }
    
    /**
     * Clears all hurdles from the track.
     */
    public void clearHurdles() {
        activeHurdles.clear();
        hurdleGenerationCounter = 0;
    }
    
    /**
     * Checks if any hurdle collides with a position.
     * 
     * @param x the x position to check
     * @param y the y position to check
     * @return the hurdle that collides with the position, or null if none
     */
    public Hurdle checkCollision(int x, int y) {
        for (Hurdle hurdle : activeHurdles) {
            if (hurdle.collidesWithPosition(x, y)) {
                return hurdle;
            }
        }
        return null;
    }
    
    /**
     * @return the list of active hurdles
     */
    public List<Hurdle> getActiveHurdles() {
        return new ArrayList<>(activeHurdles); // Return a defensive copy
    }
    
    /**
     * @return the number of active hurdles
     */
    public int getHurdleCount() {
        return activeHurdles.size();
    }
}
