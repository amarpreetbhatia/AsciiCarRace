package com.game.hurdle;

/**
 * A zigzag hurdle that moves in a zigzag pattern down the track.
 * This hurdle is more difficult to avoid than standard hurdles.
 */
public class ZigzagHurdle extends Hurdle {
    private int moveCounter = 0;
    private int direction = 1; // 1 for right, -1 for left
    
    /**
     * Creates a new zigzag hurdle at the specified position.
     * 
     * @param x the horizontal position
     * @param y the vertical position
     */
    public ZigzagHurdle(int x, int y) {
        super(x, y);
        this.symbol = 'Z';
        this.damageValue = 75;
        this.scoreValue = 20;
    }
    
    @Override
    public void move() {
        // Move down every time
        moveDown();
        
        // Move horizontally every 2 steps
        moveCounter++;
        if (moveCounter % 2 == 0) {
            // Check if we need to change direction (to avoid going off-track)
            if (x <= 2) {
                direction = 1; // Move right
            } else if (x >= 17) { // Assuming track width of 20
                direction = -1; // Move left
            }
            
            // Move horizontally
            x += direction;
        }
    }
}
