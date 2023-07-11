/**
 * Write a description of class Tile here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Tile  
{   
    // The type of chess piece
    private ChessPieces pieceType;
    // The location of the piece in the chess board
    private int x, y;
    
    // Constructor
    public Tile(ChessPieces pieceType, int x, int y){
        this.pieceType = pieceType;
        this.x = x;
        this.y = y;
    }
    
    // Accessors and Mutators
    public int getX(){return this.x;}
  
    public void setX(int newX){this.x = newX;}
  
    public int getY(){return this.y;}
  
    public void setY(int newY){this.y = newY;}
    
    public ChessPieces getPieceType(){return this.pieceType;}
  
    public void setPieceType(ChessPieces newPieceType){this.pieceType = newPieceType;}
    
    
    
    
}


