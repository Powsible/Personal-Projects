import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class King here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class King extends ChessPieces
{
    /**
     * Act - do whatever the King wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    // If the piece is clicked
    public void act()
    {
        if (Greenfoot.mouseClicked(this)) {
            ((Game)getWorld()).selectMove(this);
        }
    }
    
    // If castling is still available
    private boolean Castling = true;
    
    // Constructor
    public King(boolean white){
        super(white);
        if (!white){
            setImage("BlackKing.png");
        }
    }
    
    // Tiles that it can attack
    public boolean ifTracedTile(Chessboard board, Tile piecePosition, Tile checkedPosition, Tile possibleBlock, Tile nullTile){
        // If the enemy king is tracing the position
        if (Math.abs(checkedPosition.getX() - piecePosition.getX()) < 2 && Math.abs(checkedPosition.getY() - piecePosition.getY()) < 2){
            return true;
        }
        return false;
    }
    
    // Calculate legal moves
    public boolean legalMove(Chessboard board, Tile oldPosition, Tile newPosition){
        boolean ifCheck = Game.kingInCheck;
        // Eliminates the possibility that the target position is the same color
        if (newPosition.getPieceType() != null){
            if (newPosition.getPieceType().isWhite() == oldPosition.getPieceType().isWhite()){
                return false;
            } 
        }
        
        // Holds the possibility that the king will move more than 1 space for castling and the king MUST not be in check
        // Cannot castle if the king itself has already moved
        // Variable to shorten code. 1 = castle to the right, -1 = castle to the left, according to the equation 7*((castleThisSide+1)/2)
        int castleThisSide = (newPosition.getX() > oldPosition.getX()) ? 1: -1;
        if (Math.abs(oldPosition.getX() - newPosition.getX()) == 2 && oldPosition.getY() == newPosition.getY() && !ifCheck && Castling 
        && board.getTile(7*((castleThisSide+1)/2), oldPosition.getY()).getPieceType() != null){
                
            // 7*((castleThisSide+1)/2) where castleThisSide will be distinguished between 1 and -1 which will distinguish between castle king side or castle queen side
            // if king moved to right, then castleThisSide = 1 (king side castle), if not then castleThisSide = -1 (queen side castle)
                
            // Cannot happen if the rook on the either side has already moved or if the rook is not at the correct position
            if (board.getTile(7*((castleThisSide+1)/2), oldPosition.getY()).getPieceType().getClass().equals(Rook.class)){
                Rook rook = (Rook)board.getTile(7*((castleThisSide+1)/2), oldPosition.getY()).getPieceType();
                if (rook.Castling && (rook.isWhite() == oldPosition.getPieceType().isWhite()) ){
                    // Checking for obstacles
                    // newPosition.getY()).getX() - castleThisSide) ensures the rook isn't being checked
                    // castleThisSide is multiplied on both sides for the condition of the side the king is castling (queenside, kingside)
                    // oldPosition.getX() + castleThisSide;  so it doesn't check the king's position which obviously is not null
                    for (int i = oldPosition.getX() + castleThisSide; 
                    (castleThisSide)*i < (castleThisSide)*((6*(castleThisSide+1)/2)+1); i = i + castleThisSide){
                        // (castleThisSide)*(7*((castleThisSide+1)/2) - castleThisSide) so that it does not check the rook's position since it doesn't have to
                        if (board.getTile(i, oldPosition.getY()).getPieceType() == null){
                            // To check if each square is safe
                            if (!SafeSquare(board, oldPosition, board.getTile(i, oldPosition.getY()))){
                                return false;
                            }                            
                        } else {
                            // If there is blockage on the castle king 
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else if (Math.abs(oldPosition.getX() - newPosition.getX()) > 1 || Math.abs(oldPosition.getY() - newPosition.getY()) > 1){
            // Every move that makes the king move over 1 tile in the board
            return false;
        }
        
        // Eliminates the possibility that the king will be in danger in this new position
        return SafeSquare(board, oldPosition, newPosition);
    }
    
    // Checks if the square is safe
    
    public boolean SafeSquare(Chessboard board, Tile oldPosition, Tile checkedPosition){
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                Tile threateningTile = board.getTile(i, j);
                // If the target position is not null
                if (threateningTile.getPieceType() != null){
                    // If not the same color
                    if (threateningTile.getPieceType().isWhite() != oldPosition.getPieceType().isWhite()){
                        if (threateningTile.getPieceType().ifTracedTile(board, threateningTile, checkedPosition, null, null)){
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    // If the piece has moved
    public void setHasMoved(){
        Castling = false;
    }
    
    // Update location
    public void updateLocation(int start, int end){
        setLocation(start, end);
    }
}