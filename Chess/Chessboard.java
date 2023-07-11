/**
 * Write a description of class Chessboard here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Chessboard  
{
    // Field for the 8 by 8 board to be created
    Tile[][] tile;
    
    public Chessboard(){
        // Creates the 8 by 8 board with each tile
        // HUGE note: The column on this tile is REVERSED compared to the column on the actual game class due to how greenfoot coordinate system works
        // So for example if an object is at (0,y) at this tile, then it MUST be set on (0, 7-y) on the game class
        tile = new Tile[8][8];
    }
    
    // Generates tile based from the file selected by the user (basically a setter)
    public void generateTile(ChessPieces piece, int x, int y){tile[x][y] = new Tile(piece, x, y);}
    
    // Accessor for a tile location (getter)
    public Tile getTile(int x, int y) {return tile[x][y];}
}
