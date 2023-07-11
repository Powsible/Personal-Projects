import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Pawn here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Pawn extends ChessPieces
{
    /**
     * Act - do whatever the Pawn wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    // If the piece is clicked
    public void act(){
        if (Greenfoot.mouseClicked(this)) {
            ((Game)getWorld()).selectMove(this);
        }
        
        // Promotes to queen when white reaches 0 in the green foot board (or 7 in Chessboard class)
        // Promotes to queen when black reaches 7 in the green foot board (or 0 in Chessboard class)
        if (-3.5*(ifWhite-1)-this.getY() == 0){
            ((Game)getWorld()).pawnPromoteSelection(this);
        }
    }
    
    // If the piece has moved
    int moved = 1;
    // Value to shorten the legal move method. 
    int ifWhite = 1;
    boolean enPassant = false;
    
    // Constructor
    public Pawn(boolean white){
        super(white);
        if (!white){
            setImage("BlackPawn.png");
            ifWhite = -1;
        }
    }
    
    // Tiles that it can attack
    public boolean ifTracedTile(Chessboard board, Tile piecePosition, Tile checkedPosition, Tile possibleBlock, Tile nullTile){
        // If the pawn is targeting a square where it can capture
        if (Math.abs(piecePosition.getX() - checkedPosition.getX()) == 1 && ifWhite*(checkedPosition.getY() - piecePosition.getY()) == 1 ){
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
        
        // Eliminates the possibility that the target position is not in the same column or unless if the target position can be captured
        if (oldPosition.getX() != newPosition.getX()){
            if (newPosition.getPieceType() != null){
                if (Math.abs(oldPosition.getX() - newPosition.getX()) == 1 && ifWhite*(newPosition.getY() - oldPosition.getY()) == 1 
                && oldPosition.getPieceType().isWhite() != newPosition.getPieceType().isWhite()){
                    // Returns to the case if the king is check after this position
                    return !((Game)getWorld()).isKingChecked(oldPosition, newPosition);
                }
            } else {
                // EN PASSANT
                // Must first verify if the checked position is outside the board and the destination is not null. 
                // Technically it is impossible for the destination to not be null if the pawn beside it is in pessant
                
                // For an EnPassant piece to the left of the pawn
                // Must first verify that the piece is a pawn
                if (oldPosition.getX() != 0 && board.getTile(oldPosition.getX() - 1, oldPosition.getY()).getPieceType() != null){
                    if (board.getTile(oldPosition.getX() - 1, oldPosition.getY()).getPieceType().getClass() == oldPosition.getPieceType().getClass()
                    && board.getTile(oldPosition.getX() - 1, oldPosition.getY()).getPieceType().isWhite() != oldPosition.getPieceType().isWhite()
                    && board.getTile(oldPosition.getX() - 1, oldPosition.getY() + ifWhite) == newPosition){
                        Pawn p = (Pawn)board.getTile(oldPosition.getX() - 1, oldPosition.getY()).getPieceType();
                        // The piece must have been set enPassant 
                        if (p.getEnPassant()){
                            // Returns to the case if the king is check after this position
                            return !((Game)getWorld()).isKingChecked(oldPosition, newPosition);
                        }
                    }
                }
                if (oldPosition.getX() != 7 && board.getTile(oldPosition.getX() + 1, oldPosition.getY()).getPieceType() != null){
                    // For an EnPassant piece to the right of the pawn
                    // Must first verify that the piece is an opposite colored pawn (if doesn't work, must verify if its not null)
                    if (board.getTile(oldPosition.getX() + 1, oldPosition.getY()).getPieceType().getClass() == oldPosition.getPieceType().getClass()
                    && board.getTile(oldPosition.getX() + 1, oldPosition.getY()).getPieceType().isWhite() != oldPosition.getPieceType().isWhite()
                    && board.getTile(oldPosition.getX() + 1, oldPosition.getY() + ifWhite) == newPosition){
                        Pawn p = (Pawn)board.getTile(oldPosition.getX() + 1, oldPosition.getY()).getPieceType();
                        // The piece must have been set enPassant 
                        if (p.getEnPassant()){
                            return !((Game)getWorld()).isKingChecked(oldPosition, newPosition);
                        }
                    }
                }
            }
            // Must first verify if the checked position is outside the board
            return false;
        } else if (ifWhite*(newPosition.getY() - oldPosition.getY()) > 1 + moved || ifWhite*(newPosition.getY() - oldPosition.getY()) < 1){
            // Eliminates the possibility that the target position is too far for the pawn or if the target position makes the pawn move backwards 
            // It will also check if the pawn has moved, a case where the pawn can move 2 tiles
            return false;
        }

        // Now that all possibilities are eliminated other than the pawn's general moves, it should also eliminate the possibility that if something is blocking the pawn
        if (moved == 1){
            if (newPosition.getPieceType() != null || board.getTile(oldPosition.getX(), oldPosition.getY() + ifWhite).getPieceType() != null){
            return false;
            }
        } else {
            if (newPosition.getPieceType() != null){
                return false;
            }
        }
        
        // Checks if the king is in check after the move
        return !((Game)getWorld()).isKingChecked(oldPosition, newPosition);
    }

    // Update location
    public void updateLocation(int start, int end){
        setLocation(start, end);
    }
    
    // Accessors and Mutators for EnPessant
    public void setEnPassant(boolean newEnPassant){
        enPassant = newEnPassant;
    }

    public boolean getEnPassant(){
        return enPassant;
    }
    
    // If the piece has moved
    public void setHasMoved(){
        moved = 0;
    }
}
