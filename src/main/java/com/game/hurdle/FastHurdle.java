package com.game.hurdle;

/**
 * A fast hurdle that moves down the track more quickly than standard hurdles.
 * This hurdle is more difficult to avoid due to its speed.
 */
public class FastHurdle extends Hurdle {
    
    /**
     * Creates a new fast hurdle at the specified position.
     * 
     * @param x the horizontal position
     * @param y the vertical position
     */
    public FastHurdle(int x, int y) {
        super(x, y);
        this.symbol = '>';
        this.damageValue = 60;
        this.scoreValue = 15;
    }
    
    @Override
    public void move() {
        // Fast hurdles move down twice as fast
        moveDown();
        moveDown();
    }
}
