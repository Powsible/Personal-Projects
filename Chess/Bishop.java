import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Bishop here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Bishop extends ChessPieces
{
    /**
     * Act - do whatever the Bishop wants to do. This method is called whenever
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
    public Bishop(boolean white){
        super(white);
        if (!white){
            setImage("BlackBishop.png");
        }
    }
    
    // Tiles that it can attack
    public boolean ifTracedTile(Chessboard board, Tile piecePosition, Tile checkedPosition, Tile possibleBlock, Tile nullTile){
        // Integers to check if the new position is top right, top left, bottom left, or bottom right of the old position
        int greaterXValue = (checkedPosition.getX() > piecePosition.getX()) ? 1: -1;
        int greaterYValue = (checkedPosition.getY() > piecePosition.getY()) ? 1: -1;
        
        // If the position is not in diagonal with the bishop
        if (Math.abs(checkedPosition.getX() - piecePosition.getX()) != Math.abs(piecePosition.getY() - checkedPosition.getY())){
            return false;
        }
        for (int i = piecePosition.getX() + greaterXValue, j = piecePosition.getY() + greaterYValue; i > -1 && i < 8 &&  j > -1 && j < 8; i = i + greaterXValue, j = j + greaterYValue){
            // If the reference position matches the highlighted piece position
            if (i == checkedPosition.getX() && j == checkedPosition.getY()){
                return true;
            }
            
            // If the reference position hits a chess piece (non-null object)
            if (board.getTile(i, j).getPieceType() != null){
                // Must exclude the king since the king cannot just move backwards off (and parallel to) the diagonal trace or it will be illegal move as it is in check
                if (!(board.getTile(i, j).getPieceType().getClass().equals(King.class) 
                && board.getTile(i, j).getPieceType().isWhite() != piecePosition.getPieceType().isWhite()) && board.getTile(i, j) != nullTile){
                    return false;
                }
            }
            // The assumed piece that is blocking the path
            if (board.getTile(i, j) == possibleBlock){
                return false;
            }
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
        
        // Eliminates the possibility that the target position is not aligned diagonally from initial position
        if (Math.abs(newPosition.getX() - oldPosition.getX()) != Math.abs(oldPosition.getY() - newPosition.getY())){
            return false;
        }
         
        // Integers to check if the new position is top right, top left, bottom left, or bottom right of the old position
        int greaterXValue = (newPosition.getX() > oldPosition.getX()) ? 1: -1;
        int greaterYValue = (newPosition.getY() > oldPosition.getY()) ? 1: -1;
        
        // Also (greaterYValue)*j < (greaterYValue)*newPosition.getY() 
        // Eliminates the possibility that there is an obstacle between the target position and the old position
        for (int i = oldPosition.getX() + greaterXValue, j = oldPosition.getY() + greaterYValue; (greaterXValue)*i < (greaterXValue)*newPosition.getX(); i = i + greaterXValue, j = j + greaterYValue){
            if (board.getTile(i, j).getPieceType() != null){
                return false;
            }
        }
        
        // EXCLUDE new position and the old position itself to check if the king is in check after the move
        // The new position can be the captured piece which can be the one checking the king
        return !((Game)getWorld()).isKingChecked(oldPosition, newPosition);
        
    }
    
    // UpdateLocation
    public void updateLocation(int start, int end){
        setLocation(start, end);
    }
    
    // If the piece has moved
    public void setHasMoved(){
        hasMoved = true;
    }
}
