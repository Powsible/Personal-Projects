import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Knight here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Knight extends ChessPieces
{
    /**
     * Act - do whatever the Knight wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    // If the piece is clicked
    public void act()
    {
        if (Greenfoot.mouseClicked(this)) {
            ((Game)getWorld()).selectMove(this);
        }
    }
    
    private boolean hasMoved = false;
    
    // Constructor
    public Knight(boolean white){
        super(white);
        if (!white){
            setImage("BlackKnight.png");
        }
    }
    
    // Tiles that it can attack
    public boolean ifTracedTile(Chessboard board, Tile piecePosition, Tile checkedPosition, Tile possibleBlock, Tile nullTile){
        // If the knight is targeting the checked position using an L shape
        if (((Math.abs(piecePosition.getX() - checkedPosition.getX()) == 1 && Math.abs(piecePosition.getY() - checkedPosition.getY()) == 2) || 
        (Math.abs(piecePosition.getX() - checkedPosition.getX()) == 2 && Math.abs(checkedPosition.getY() - piecePosition.getY()) == 1))){
            return true;
        }
        return false;
    }
    
    // Calculate legal moves
    public boolean legalMove(Chessboard board, Tile oldPosition, Tile newPosition){
        // Eliminates the possibility that the target position is the same color
        if (newPosition.getPieceType() != null){
            if (newPosition.getPieceType().isWhite() == oldPosition.getPieceType().isWhite()){
                return false;
            }
        }
        
        // Eliminates the possibility that the target position is not an L shaped move
        if (!((Math.abs(oldPosition.getX() - newPosition.getX()) == 1 && Math.abs(oldPosition.getY() - newPosition.getY()) == 2) || 
        (Math.abs(oldPosition.getX() - newPosition.getX()) == 2 && Math.abs(newPosition.getY() - oldPosition.getY()) == 1))){
            return false;
        }
        
        // Checks if the king is in check after the move
        return !((Game)getWorld()).isKingChecked(oldPosition, newPosition);
    }
    
    // Update location
    public void updateLocation(int start, int end){
        setLocation(start, end);
    }
    
    // If the piece has moved
    public void setHasMoved(){
        hasMoved = true;
    }
}