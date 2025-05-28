package com.game.hurdle;

/**
 * A standard hurdle that moves straight down the track.
 * This is the basic hurdle type in the game.
 */
public class StandardHurdle extends Hurdle {
    
    /**
     * Creates a new standard hurdle at the specified position.
     * 
     * @param x the horizontal position
     * @param y the vertical position
     */
    public StandardHurdle(int x, int y) {
        super(x, y);
        this.symbol = '*';
        this.damageValue = 50;
        this.scoreValue = 10;
    }
    
    @Override
    public void move() {
        // Standard hurdles just move straight down
        moveDown();
    }
}
