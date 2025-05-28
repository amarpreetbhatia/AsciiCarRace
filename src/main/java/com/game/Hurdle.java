package com.game;

/**
 * The Hurdle class represents obstacles on the track that the player must avoid.
 */
public class Hurdle {
    private int x; // Horizontal position
    private int y; // Vertical position
    private final char SYMBOL = '*';
    
    public Hurdle(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Moves the hurdle down one position.
     */
    public void moveDown() {
        y++;
    }
    
    /**
     * @return the horizontal position of the hurdle
     */
    public int getX() {
        return x;
    }
    
    /**
     * @return the vertical position of the hurdle
     */
    public int getY() {
        return y;
    }
    
    /**
     * @return the symbol representing the hurdle
     */
    public char getSymbol() {
        return SYMBOL;
    }
}
