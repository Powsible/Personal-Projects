import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Queen here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Queen extends ChessPieces
{
    /**
     * Act - do whatever the Queen wants to do. This method is called whenever
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
    public Queen(boolean white){
        super(white);
        if (!white){
            setImage("BlackQueen.png");
        }
    }
    
    // Tiles that it can attack
    public boolean ifTracedTile(Chessboard board, Tile piecePosition, Tile checkedPosition, Tile possibleBlock, Tile nullTile){
        // Case when the target piece is diagonal
        if (Math.abs(checkedPosition.getX() - piecePosition.getX()) == Math.abs(piecePosition.getY() - checkedPosition.getY())){
            // Integers to check if the new position is top right, top left, bottom left, or bottom right of the old position
            int greaterXValue = (checkedPosition.getX() > piecePosition.getX()) ? 1: -1;
            int greaterYValue = (checkedPosition.getY() > piecePosition.getY()) ? 1: -1;
            for (int i = piecePosition.getX() + greaterXValue, j = piecePosition.getY() + greaterYValue; 
            i > -1 && i < 8 &&  j > -1 && j < 8; i = i + greaterXValue, j = j + greaterYValue){
                // If the reference position matches the highlighted piece position
                if (i == checkedPosition.getX() && j == checkedPosition.getY()){
                    return true;
                }
                // If the reference position hits a chess piece (non-null object)
                if (board.getTile(i, j).getPieceType() != null){
                    // Must exclude the king since the king cannot just move backwards off the diagonal trace or it will be illegal move as it is in check
                    if (!(board.getTile(i, j).getPieceType().getClass().equals(King.class) 
                    && board.getTile(i, j).getPieceType().isWhite() != piecePosition.getPieceType().isWhite()) && board.getTile(i, j) != nullTile){
                        return false;
                    }
                }
                // Possible block piece
                if (board.getTile(i, j) == possibleBlock){
                    return false;
                }
            }
        } else if (piecePosition.getX() == checkedPosition.getX()){
            // For when the queen is vertical to the position. The rest applies like the rook
            int greaterYValue = (checkedPosition.getY() > piecePosition.getY()) ? 1: -1;
            for (int i = piecePosition.getY() + greaterYValue; i < 8 && i > -1; i = i + greaterYValue){
                // Target position
                if (checkedPosition.getY() == i){
                    return true;
                }
                // If the reference position hits a chess piece (non-null object)
                if (board.getTile(piecePosition.getX(), i).getPieceType() != null){
                    if (!(board.getTile(piecePosition.getX(), i).getPieceType().getClass().equals(King.class) 
                    && board.getTile(piecePosition.getX(), i).getPieceType().isWhite() != piecePosition.getPieceType().isWhite()) && board.getTile(piecePosition.getX(), i) != nullTile){
                        return false;
                    }
                }
                // The assumed piece that is blocking the path
                if (board.getTile(piecePosition.getX(), i) == possibleBlock){
                    return false;
                }
            }
        } else if (checkedPosition.getY() == piecePosition.getY()){
            // For when the queen is horizontal to the position. The rest applies like the rook
            int greaterXValue = (checkedPosition.getX() > piecePosition.getX()) ? 1: -1;
            for (int i = piecePosition.getX() + greaterXValue; i < 8 && i > -1; i = i + greaterXValue){
                // If it matches to the position
                if (checkedPosition.getX() == i){
                    return true;
                }
                // If the reference position hits a chess piece (non-null object)
                if (board.getTile(i, piecePosition.getY()).getPieceType() != null){
                    if (!(board.getTile(i, piecePosition.getY()).getPieceType().getClass().equals(King.class) 
                    && board.getTile(i, piecePosition.getY()).getPieceType().isWhite() != piecePosition.getPieceType().isWhite()) && board.getTile(i, piecePosition.getY()) != nullTile){
                        return false;
                    }
                }
                // The assumed piece that is blocking the path
                if (board.getTile(i, piecePosition.getY()) == possibleBlock){
                    return false;
                }
            }
        }
        // If neither the selected piece is in column, row, nor diagonal with the checked piece
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
        
        // Eliminates the possibility that there is an obstacle between the target position and the old position
        if (Math.abs(newPosition.getX() - oldPosition.getX()) == Math.abs(oldPosition.getY() - newPosition.getY())){
            // Eliminates the possibility that there is an obstacle between the target position and the old position
            // Integers to check if the new position is top right, top left, bottom left, or bottom right of the old position
            int greaterXValue = (newPosition.getX() > oldPosition.getX()) ? 1: -1;
            int greaterYValue = (newPosition.getY() > oldPosition.getY()) ? 1: -1;
            for (int i = oldPosition.getX() + greaterXValue, j = oldPosition.getY() + greaterYValue; (greaterXValue)*i < (greaterXValue)*newPosition.getX(); i = i + greaterXValue, j = j + greaterYValue){
                if (board.getTile(i, j).getPieceType() != null){
                    return false;
                }
            }
        } else if (newPosition.getX() == oldPosition.getX()){
            // If the newPosition is upwards
            if (newPosition.getY() > oldPosition.getY()){
                for (int i = oldPosition.getY() + 1; newPosition.getY() > i; i++){
                    if (board.getTile(oldPosition.getX(), i).getPieceType() != null){
                        return false;
                    }
                }
            } else {
                // If downwards
                for (int i = oldPosition.getY() - 1; newPosition.getY() < i; i--){
                    if (board.getTile(oldPosition.getX(), i).getPieceType() != null){
                        return false;
                    }
                }
            }
        } else if (newPosition.getY() == oldPosition.getY()){
            // If the newPosition is rightwards
            if (newPosition.getX() > oldPosition.getX()){
                for (int i = oldPosition.getX() + 1; newPosition.getX() > i; i++){
                    if (board.getTile(i, oldPosition.getY()).getPieceType() != null){
                        return false;
                    }
                }
            } else {
                // If the newPosition is leftwards
                for (int i = oldPosition.getX() - 1; newPosition.getX() < i; i--){
                    if (board.getTile(i, oldPosition.getY()).getPieceType() != null){
                        return false;
                    }
                }
            }
        } else {
            // Eliminates the possibility that the target position is not aligned diagonally or vertically or horizontally aligned from initial position
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
