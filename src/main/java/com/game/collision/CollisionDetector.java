package com.game.collision;

import com.game.Car;
import com.game.Track;
import com.game.hurdle.Hurdle;

/**
 * The CollisionDetector class is responsible for detecting collisions between game objects.
 * This class follows the Single Responsibility Principle (SRP) by focusing only on
 * collision detection logic, separate from game state management.
 */
public class CollisionDetector {
    
    /**
     * Checks if the car has collided with any hurdle or track boundary.
     * 
     * @param car the player's car
     * @param track the game track
     * @return a CollisionResult object containing collision information
     */
    public CollisionResult checkCollision(Car car, Track track) {
        int carX = car.getX();
        int carY = car.getY();
        
        // Check if car is within valid track bounds
        if (carX < 0 || carX >= track.getWidth() || carY < 0 || carY >= track.getHeight()) {
            return new CollisionResult(true, CollisionType.OUT_OF_BOUNDS, null);
        }
        
        // Check collision with track boundaries
        char[][] trackData = track.getTrackData();
        if (trackData[carY][carX] == track.getBoundary()) {
            return new CollisionResult(true, CollisionType.BOUNDARY, null);
        }
        
        // Check collision with hurdles
        Hurdle collidedHurdle = track.getHurdleManager().checkCollision(carX, carY);
        if (collidedHurdle != null) {
            return new CollisionResult(true, CollisionType.HURDLE, collidedHurdle);
        }
        
        // No collision detected
        return new CollisionResult(false, CollisionType.NONE, null);
    }
    
    /**
     * Enum representing different types of collisions.
     */
    public enum CollisionType {
        NONE,           // No collision
        BOUNDARY,       // Collision with track boundary
        HURDLE,         // Collision with a hurdle
        OUT_OF_BOUNDS   // Car is outside the track bounds
    }
    
    /**
     * Class representing the result of a collision check.
     */
    public static class CollisionResult {
        private final boolean collision;
        private final CollisionType type;
        private final Hurdle hurdle;
        
        /**
         * Creates a new collision result.
         * 
         * @param collision whether a collision occurred
         * @param type the type of collision
         * @param hurdle the hurdle involved in the collision (if any)
         */
        public CollisionResult(boolean collision, CollisionType type, Hurdle hurdle) {
            this.collision = collision;
            this.type = type;
            this.hurdle = hurdle;
        }
        
        /**
         * @return true if a collision occurred, false otherwise
         */
        public boolean isCollision() {
            return collision;
        }
        
        /**
         * @return the type of collision
         */
        public CollisionType getType() {
            return type;
        }
        
        /**
         * @return the hurdle involved in the collision, or null if not a hurdle collision
         */
        public Hurdle getHurdle() {
            return hurdle;
        }
    }
}
