import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class ShowPossibleMoves here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class ShowPossibleMoves extends Actor
{
    /**
     * Act - do whatever the ShowPossibleMoves wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
    }
    
    // If either the piece is mean't to highlight or just shows which piece is selected
    boolean highlightPiece;
    
    // Constructor
    public ShowPossibleMoves(boolean highlightPiece){
        this.highlightPiece = highlightPiece;
        // Each tile is 60 by 60 in pixels
        getImage().scale(60, 60);
        this.getImage().setTransparency(150);
        if (highlightPiece){
            // Changes image to a highlight piece
            this.setImage("SelectedPiece.png");
            this.getImage().setTransparency(50);
        } 
    }
    
    // Accessor
    public boolean getHighLightPiece(){return this.highlightPiece;}
}
