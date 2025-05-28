package com.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * The Track class represents the game track with boundaries and hurdles.
 * It manages the track state, scrolling, and collision detection.
 */
public class Track {
    private final int width;
    private final int height;
    private final List<Hurdle> hurdles = new ArrayList<>();
    private final Random random = new Random();
    private final char BOUNDARY = '|';
    
    public Track(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    /**
     * Initializes the track with initial hurdles.
     */
    public void initialize() {
        hurdles.clear();
        // Add some initial hurdles
        for (int i = 0; i < 5; i++) {
            addHurdle();
        }
    }
    
    /**
     * Adds a new hurdle at the top of the track.
     */
    public void addHurdle() {
        int x = random.nextInt(width - 2) + 1; // Avoid placing hurdles on boundaries
        hurdles.add(new Hurdle(x, 0));
    }
    
    /**
     * Scrolls the track by moving all hurdles down.
     */
    public void scroll() {
        // Move all hurdles down
        for (Hurdle hurdle : hurdles) {
            hurdle.moveDown();
        }
        
        // Remove hurdles that have moved off the bottom of the track
        Iterator<Hurdle> iterator = hurdles.iterator();
        while (iterator.hasNext()) {
            Hurdle hurdle = iterator.next();
            if (hurdle.getY() >= height) {
                iterator.remove();
            }
        }
    }
    
    /**
     * Checks if the car has collided with any hurdle.
     * 
     * @param car the player's car
     * @return true if collision detected, false otherwise
     */
    public boolean checkCollision(Car car) {
        int carX = car.getX();
        int carY = car.getY();
        
        // Check collision with hurdles
        for (Hurdle hurdle : hurdles) {
            if (hurdle.getX() == carX && hurdle.getY() == carY) {
                return true;
            }
        }
        
        // Check collision with boundaries
        return carX <= 0 || carX >= width - 1;
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
        return hurdles;
    }
    
    /**
     * @return the boundary character
     */
    public char getBoundary() {
        return BOUNDARY;
    }
}
