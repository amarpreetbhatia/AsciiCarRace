package com.game.hurdle;

import java.util.Random;

/**
 * Factory class for creating different types of hurdles.
 * This class follows the Factory Method pattern and Open/Closed Principle (OCP)
 * by allowing new hurdle types to be added without modifying existing code.
 */
public class HurdleFactory {
    private final Random random = new Random();
    private final int trackWidth;
    
    /**
     * Creates a new hurdle factory for a track with the specified width.
     * 
     * @param trackWidth the width of the track
     */
    public HurdleFactory(int trackWidth) {
        this.trackWidth = trackWidth;
    }
    
    /**
     * Creates a random hurdle at the top of the track.
     * The type of hurdle is determined by the difficulty level.
     * 
     * @param difficultyLevel the current difficulty level
     * @return a new hurdle
     */
    public Hurdle createRandomHurdle(int difficultyLevel) {
        // Determine a valid x position (avoiding track boundaries)
        int x = random.nextInt(trackWidth - 4) + 2; // Keep at least 1 space from boundaries
        
        // Determine hurdle type based on difficulty
        int hurdleTypeChance = random.nextInt(100);
        
        // Higher difficulty increases chances of more challenging hurdles
        if (difficultyLevel >= 4 && hurdleTypeChance < 20) {
            return new ZigzagHurdle(x, 0);
        } else if (difficultyLevel >= 2 && hurdleTypeChance < 30) {
            return new FastHurdle(x, 0);
        } else {
            return new StandardHurdle(x, 0);
        }
    }
    
    /**
     * Creates a specific type of hurdle at the specified position.
     * 
     * @param type the type of hurdle to create
     * @param x the horizontal position
     * @param y the vertical position
     * @return a new hurdle of the specified type
     */
    public Hurdle createHurdle(HurdleType type, int x, int y) {
        return switch (type) {
            case STANDARD -> new StandardHurdle(x, y);
            case ZIGZAG -> new ZigzagHurdle(x, y);
            case FAST -> new FastHurdle(x, y);
        };
    }
    
    /**
     * Creates a pattern of hurdles at the top of the track.
     * The pattern is determined by the difficulty level.
     * 
     * @param difficultyLevel the current difficulty level
     * @return an array of hurdles forming a pattern
     */
    public Hurdle[] createHurdlePattern(int difficultyLevel) {
        // Higher difficulty means more complex patterns
        int patternType = random.nextInt(Math.min(5, difficultyLevel + 1));
        
        return switch (patternType) {
            case 0 -> new Hurdle[]{ // Single hurdle
                createRandomHurdle(difficultyLevel)
            };
            case 1 -> new Hurdle[]{ // Two adjacent hurdles
                new StandardHurdle(trackWidth / 2 - 2, 0),
                new StandardHurdle(trackWidth / 2 + 2, 0)
            };
            case 2 -> new Hurdle[]{ // Three hurdles with gap in middle
                new StandardHurdle(2, 0),
                new StandardHurdle(trackWidth - 3, 0),
                difficultyLevel >= 3 ? new FastHurdle(trackWidth / 2, 0) : new StandardHurdle(trackWidth / 2, 0)
            };
            case 3 -> new Hurdle[]{ // Zigzag pattern
                new ZigzagHurdle(trackWidth / 3, 0),
                new ZigzagHurdle(2 * trackWidth / 3, 0)
            };
            case 4 -> new Hurdle[]{ // Mixed pattern
                new FastHurdle(trackWidth / 4, 0),
                new StandardHurdle(trackWidth / 2, 0),
                new ZigzagHurdle(3 * trackWidth / 4, 0)
            };
            default -> new Hurdle[]{ // Default single hurdle
                new StandardHurdle(trackWidth / 2, 0)
            };
        };
    }
    
    /**
     * Enum representing the different types of hurdles.
     */
    public enum HurdleType {
        STANDARD,
        ZIGZAG,
        FAST
    }
}
