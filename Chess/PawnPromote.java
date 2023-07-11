import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class PawnPromote here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class PawnPromote extends Actor
{
    /**
     * Act - do whatever the PawnPromote wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    
    // Integer that is to be grabbed by a getter up to a switch method where this value will indicate the piece promoted
    int i;
    
    // If a piece-image object is clicked by the user, they have selected a pawn promotion.
    public void act()
    {
        // Cool transparency animation
        int j = getImage().getTransparency();
        if (j < 250){
            try
            {
                Thread.sleep(1);
            }
            catch (InterruptedException ie)
            {
                ie.printStackTrace();
            }
            j = j + 10;
            getImage().setTransparency(j);
        }
        if (i != 0 && Greenfoot.mouseClicked(this)) {
            ((Game)getWorld()).pawnPromote();
        }
    }
    
    // Constructor
    public PawnPromote(int i){
        this.i = i;
        switch (i){
            case 0: 
                this.getImage().setTransparency(0);
                getImage().drawImage(new GreenfootImage("Select a piece to promote it", 30, null, null), 120, 120);
                break;
            case 1:
                this.setImage("WhiteKnight.png");
                break;
            case 2:
                this.setImage("WhiteBishop.png");
                break;
            case 3: 
                this.setImage("WhiteRook.png");
                break;
            case 4:
                this.setImage("WhiteQueen.png");
                break;
        }     
    }
    
    // Accessor
    public int getI(){return i;}
}
