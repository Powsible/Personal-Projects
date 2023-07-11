import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class ChessPieces here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public abstract class ChessPieces extends Actor
{
    /**
     * Act - do whatever the ChessPieces wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act()
    {
        
    }
    
    // Constructor
    public ChessPieces(boolean white){
        this.white = white;
    }
    
    // Color of the piece
    private boolean white;
    
    // Abstract method for finding legal moves of each piece
    public abstract boolean legalMove(Chessboard board, Tile piecePosition, Tile checkedPosition);
    
    // Set location of each piece after each move
    public abstract void updateLocation(int start, int end);
    
    // Set if a piece has moved
    public abstract void setHasMoved();
    
    // Boolean for traced tiles of each piece
    // The last 2 arguments can be null and they only matter on pieces like queen, rook, and bishop that can't jump through pieces
    public abstract boolean ifTracedTile(Chessboard board, Tile oldPosition, Tile newPosition, Tile possibleBlock, Tile nullTile);
    // possibleBlock - this position is blocked by a piece by assumption
    // nullTile - this position is empty by assumption
  
    // Accessors and Mutators
    public boolean isWhite() {return this.white;}
    public void setWhite(boolean newWhite){this.white = newWhite;}
}

