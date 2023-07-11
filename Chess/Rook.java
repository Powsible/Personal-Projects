import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Rook here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Rook extends ChessPieces
{
    /**
     * Act - do whatever the Rook wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    // If the piece is clicked
    
    public void act()
    {
        if (Greenfoot.mouseClicked(this)) {
            ((Game)getWorld()).selectMove(this);
        }
    }
    
    public boolean Castling = true;
    
    // Constructor
    public Rook(boolean white){
        super(white);
        if (!white){
            setImage("BlackRook.png");
        }
    }
    
    // Tiles that it can attack
    public boolean ifTracedTile(Chessboard board, Tile piecePosition, Tile checkedPosition, Tile possibleBlock, Tile nullTile){
        if (piecePosition.getX() == checkedPosition.getX()){
            // Int to check if the checked position is higher on the board than the piece position (more compact code)
            int greaterYValue = (checkedPosition.getY() > piecePosition.getY()) ? 1: -1;
            for (int i = piecePosition.getY() + greaterYValue; i < 8 && i > -1; i = i + greaterYValue){
                // These if statements are properly in order
                
                // If the reference position matches the highlighted piece position
                if (checkedPosition.getY() == i){
                    return true;
                }
                // If the reference position hits a chess piece (non-null object)
                if (board.getTile(piecePosition.getX(), i).getPieceType() != null){
                    // The chess piece MUST not be the opposite colored king as that king can't go to a position beyond itself since those positions are not safe after the king moves
                    if ((!(board.getTile(piecePosition.getX(), i).getPieceType().getClass().equals(King.class) 
                    && board.getTile(piecePosition.getX(), i).getPieceType().isWhite() != piecePosition.getPieceType().isWhite())) && board.getTile(piecePosition.getX(), i) != nullTile){
                         return false;
                    }
                }
                // If checked for the aftermove with a blocked piece
                if (board.getTile(piecePosition.getX(), i) == possibleBlock){
                    return false;
                }
            }
        } else if (checkedPosition.getY() == piecePosition.getY()){
            // Int to check if the checked position is more to the right than the piece position
            int greaterXValue = (checkedPosition.getX() > piecePosition.getX()) ? 1: -1;
            for (int i = piecePosition.getX() + greaterXValue; i < 8 && i > -1; i = i + greaterXValue){
                // These if statements are properly in order
                
                // If the reference position matches the highlighted piece position
                if (checkedPosition.getX() == i){
                    return true;
                }
                // If the reference position hits a chess piece (non-null object)
                if (board.getTile(i, piecePosition.getY()).getPieceType() != null){
                    // System.out.print("{i: " + i + "}");
                    // System.out.println(board.getTile(i, piecePosition.getY()).getPieceType().isWhite());
                    // System.out.println(piecePosition.getPieceType());
                    // The chess piece MUST not be the opposite colored king as the king can't go to a position beyond itself since those positions are pinned
                    if ((!(board.getTile(i, piecePosition.getY()).getPieceType().getClass().equals(King.class) 
                    && board.getTile(i, piecePosition.getY()).getPieceType().isWhite() != piecePosition.getPieceType().isWhite()) && board.getTile(i, piecePosition.getY()) != nullTile)){
                        return false;
                    }
                }
                // If checked for the aftermove with a blocked piece
                if (board.getTile(i, piecePosition.getY()) == possibleBlock){
                    return false;
                }
            }
        }
        // If neither the selected piece and the checked position are in the same column or row
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
        
        // Eliminates the possibility that the target position is not aligned horizontally or vertically from initial position
        if (newPosition.getX() != oldPosition.getX() && oldPosition.getY() != newPosition.getY()){
            return false;
        }
        
        // Eliminates the possibility that there is an obstacle between the target position and the old position
        if (newPosition.getX() == oldPosition.getX()){
            // If they are in the same row
            int greaterYValue = (newPosition.getY() > oldPosition.getY()) ? 1: -1;
            for (int i = oldPosition.getY() + greaterYValue; (greaterYValue)*i < (greaterYValue)*newPosition.getY(); i = i + greaterYValue){
                // If there is a piece blocking the way between them
                if (board.getTile(oldPosition.getX(), i).getPieceType() != null){
                    return false;
                }
            }
        } else {
            // If they are in the same column
            int greaterXValue = (newPosition.getX() > oldPosition.getX()) ? 1: -1;
            for (int i = oldPosition.getX() + greaterXValue; (greaterXValue)*i < (greaterXValue)*newPosition.getX(); i = i + greaterXValue){
                // If there is a piece blocking the way between them
                if (board.getTile(i, oldPosition.getY()).getPieceType() != null){
                    return false;
                }
            }
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
        Castling = false;
    }
}
