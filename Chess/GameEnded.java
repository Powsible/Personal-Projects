import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class GameEnded here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class GameEnded extends Actor
{
    /**
     * Act - do whatever the GameEnded wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    
    // String for the piece color that wins
    
    boolean isPrinted = true;
    
    public void act()
    {
        // Cool transparency animation
        // Get transparency of the object that starts at 0
        int i = getImage().getTransparency();
        if (i < 200){
            try
            {
                // Delay for each frame of animation
                Thread.sleep(10);
            }
            catch (InterruptedException ie)
            {
                ie.printStackTrace();
            }
            // Increment transparency
            i = i + 10;
            getImage().setTransparency(i);
        } 
        if (isPrinted){
            // Game Over
            System.out.println("\nGame Over! Click reset to start a new game");
            isPrinted = false;
        }
    }
    
    // If not checkmate, then it is stalemate
    public GameEnded(boolean isCheckmate, boolean isWhiteWinner){
        getImage().setTransparency(0);
        if (isCheckmate){
            // Checkmate case
            String winner = (isWhiteWinner) ? "White": "Black";
            getImage().drawImage(new GreenfootImage("Checkmate! " + winner + " wins!", 50, null, null), 45, 240);
        } else {
            // Stalemate case
            getImage().setTransparency(100);
            getImage().drawImage(new GreenfootImage("Stalemate!", 50, null, null), 180, 240);            
        }
    }
}
