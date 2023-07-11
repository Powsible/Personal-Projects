import greenfoot.*;  
import java.util.Scanner;
import java.io.*;
import greenfoot.GreenfootImage;

/**
 * Write a description of class ChessPieces here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
/* Includes Chapter 1 - 2 (Basics)
 * Chapter 3 (A lot of decision control between chess pieces)
 * Chapter 4 (A lot of loops to gather each tile between chess pieces and contribute to decision control)
 * Chapter 5 (A file of the chess position can be read to generate the position)
 * Chapter 6 (Overloaded methods of checking if a king is in check in 2 types of cases with different times)
 * Chapter 7 (An 8 by 8 array for the chess board)
 * Chapter 9 (Inheritance for the kinds of chess pieces)
 * Chapter 14 (MISSING!)
*/
public class Game extends World
{   
    // Creates the instance of the Chessboard Array
    private Chessboard board = new Chessboard();
    // The referenced piece in this class
    private ChessPieces clickedPiece;
    // Boolean to check if it is white's turn, the game has started or the game has ended
    private boolean whiteTurn = true, isGameEnded = false, isGameStarted = false;
    // If the king is in check
    public static boolean kingInCheck = false;
    // Possible En Passant pawn
    private Pawn InPassant = null;
    // Each number of turn is one turn for black and one turn for white combined
    private int numberOfTurns = 0;
    // Number of pieces remaining    
    private int numberOfPiecesRemaining;
    // For the notation of showing moves
    private String captured = "", x, y, Castle = "";
    // For the notation of showing moves
    private final String[] toLetter = {"a", "b", "c", "d", "e", "f", "g", "h"};
    // Sounds for moving/capturing pieces
    GreenfootSound movePieceSound = new GreenfootSound("ChessMoveSound.wav"), capturePieceSound = new GreenfootSound("CaptureSound.wav");
    // For the user to select the proper position
    Scanner kbInput = new Scanner (System.in), readFile = null;

    /**
     * Constructor for objects of class Game.
     * 
     */
    // Sets up the game
    public Game(){
        // Creates world
        super(8, 8, 60); 
        // Guides the user
        System.out.println("\nWelcome to chess! Click run to get started.");
        // In case of new Game(), it will stop the program from running before the "run" button is hit
        Greenfoot.stop();
    }
    
    public void act(){
        // Start game
        if (!isGameStarted){
            isGameStarted = true;
            prepareGame();
        }
        
        // Keeps in track when the user selects a move
        selectedMove();
    }
    
    // Check possible moves
    public void selectMove(ChessPieces selectedPiece){
        // Hides possible moves object which were present before (resets them just in case the user wants to use a different piece to move)
        removeObjects(getObjects(ShowPossibleMoves.class));
        // Referenced piece
        clickedPiece = selectedPiece;
        // Checks for which player can move pieces
        if (whiteTurn == selectedPiece.isWhite() && (!isGameEnded)){
            // Checks for all possible moves in the board
            for (int i = 0; i < 8; i++){
                for (int j = 0; j < 8; j++){
                    if(selectedPiece.legalMove(board, board.getTile(selectedPiece.getX(), 7-selectedPiece.getY()), board.getTile(i,j))){
                        // Adds the possible moves object that will be shown for the user
                        addObject(new ShowPossibleMoves(false), i, 7-j);
                    } 
                }
            }
            // Adds the object to show the user which piece is being selected to move
            addObject(new ShowPossibleMoves(true), selectedPiece.getX(), selectedPiece.getY());
        }
    }
    
    // When a user has selected a move
    public void selectedMove(){
        for (ShowPossibleMoves selection: getObjects(ShowPossibleMoves.class)){
            // If the user selects a move
            // Note: It should have already been coded that whatever object of "ShowPossibleMoves" that the player chooses is a valid move
            if (Greenfoot.mouseClicked(selection) && selection.getHighLightPiece() == false && isGameStarted) {
                // Tiles for starting position and destination
                Tile destination = board.getTile(selection.getX(), 7 - selection.getY()); 
                Tile startingPosition = board.getTile(clickedPiece.getX(), 7-clickedPiece.getY());
                // Castling! Can only occur for a king that moves 2 squares in the board. This is only to move the rook and not the king itself
                if (clickedPiece.getClass().equals(King.class) && Math.abs(startingPosition.getX() - destination.getX()) == 2){
                    // Efficient instead of 2 if statements. Determines if castling queen side or king side
                    int castleThisSide = (destination.getX() > startingPosition.getX()) ? 1: -1;
                    Castle = (destination.getX() > startingPosition.getX()) ? "O-O": "O-O-O";
                    // The tile will ask for 7*((castleThisSide+1)/2 (my made up equation), if castleThisSide = 1, then the rook from the 8th row will be grabbed
                    // if castleThisSide = -1, then the rook from the first row will be grabbed
                    // Sadly, castling might not work on other chess positions other than standard, where the rooks are not in the first or last row.
                    
                    Tile rook = board.getTile(7*((castleThisSide+1)/2), startingPosition.getY());
                    
                    // If castling queen side, the rook will be right of the king
                    // If castling king side, the rook will be left of the king
                    rook.getPieceType().setHasMoved();
                    rook.getPieceType().updateLocation(destination.getX() - castleThisSide, 7 - destination.getY());
                    board.getTile(destination.getX() - castleThisSide, destination.getY()).setPieceType(rook.getPieceType());
                    
                    // Does NOT make the rook null. It makes its initial position before (7, 0) null
                    rook.setPieceType(null);
                }
                                
                // If the new position has an opposing colored piece and is captured
                if (destination.getPieceType() != null){
                    // Causes an error when InPassant is captured while it is set to null
                    if (destination.getPieceType() == InPassant){
                        InPassant = null;
                    }
                    removeObject(destination.getPieceType());
                    // For notation of capturing a piece, checks if it is a pawn
                    if (clickedPiece.getClass().equals(Pawn.class)){
                        captured = toLetter[startingPosition.getX()] + "x";
                    } else {
                        captured = "x";
                    }
                    // Sound if something is captured
                    capturePieceSound.play();
                    // If there is insufficient amount of pieces left in the board then it should be stalemate (When 2 kings are remaining)
                    numberOfPiecesRemaining--;
                    if (isGameStarted && !isGameEnded && numberOfTurns > 0 && numberOfPiecesRemaining < 3){
                        isGameEnded = true;
                        addObject(new GameEnded(false, true), 240, 240);
                    }
                } else {
                    // Sound if nothing is captured
                    movePieceSound.play();
                }
        
                // The object will be teleported to the destination
                clickedPiece.updateLocation(destination.getX(), 7 - destination.getY());
                // The piece has moved
                clickedPiece.setHasMoved();
                // The piece goes to the destination for the record of the 2-dimensional board tile
                destination.setPieceType(clickedPiece);
                            
                // If the case is InPassant and not captured
                if (InPassant != null){
                    // Must be the same class (Pawn for pawn)
                    if (clickedPiece.getClass() == InPassant.getClass()){
                        // Controls the direction of the en passant
                        int w = (InPassant.isWhite()) ? 1: -1;
                        // Must be in the same X and the same destination
                        if (clickedPiece.getY() - InPassant.getY() == w && destination.getX() == InPassant.getX()){
                            // MUST be performed so that the pawn's tile is not classified as a chess piece anymore
                            board.getTile(InPassant.getX(), 7 - InPassant.getY()).setPieceType(null);
                            // Remove InPassant pawn
                            removeObject(InPassant);
                        } 
                    }
                }
                
                // Gives the other color the turn
                whiteTurn = !whiteTurn;
                // Only change when each player get a turn
                if (!whiteTurn){
                    numberOfTurns++;
                }
                
                // Removes all objects that show possible moves as the turn is done
                removeObjects(getObjects(ShowPossibleMoves.class));
                
                // Pawn no longer in passant after a turn if it is not captured
                if (InPassant != null){
                    InPassant.setEnPassant(false);
                    InPassant = null;
                }
                
                // En passant (checks if the piece is the pawn and if the pawn has moved 2 tiles)
                if (destination.getPieceType().getClass().equals(Pawn.class) && (Math.abs(startingPosition.getY() - destination.getY()) == 2)){
                    Pawn pawn = (Pawn)destination.getPieceType();
                    pawn.setEnPassant(true);
                    InPassant = pawn;
                }
                
                // The initial position of the moved piece should always be empty
                startingPosition.setPieceType(null);
                
                // Notation for showing moves
                x = toLetter[destination.getX()];
                // Conversion from the board tile array to the actual chess position is +1 since board tile array is from 0-7 while chess is from 1-8
                y = String.valueOf(destination.getY() + 1);
                System.out.print(showMoves(destination, startingPosition));
                // Resets the variables that helps to show moves
                captured = "";
                Castle = "";
                
                // Check if the king is in check after the move
                kingInCheck = isKingChecked();
                
                // Resets referenced piece
                clickedPiece = null;
                
                // Stalemate MUST be checked after checkmate is checked which is when king is checked or else, it might end up stalemate when the king is checkmated
                if (stalemate() && !isGameEnded){
                    isGameEnded = true;
                    // Second argument can be whatever
                    addObject(new GameEnded(false, true), 240, 240);
                }
            }
        }    
    }
    
    // Creates objects which provide selections for the user for their promoted pawn
    public void pawnPromoteSelection(ChessPieces piece){
        // Referenced piece
        clickedPiece = piece;
        // The background of the selection with a specific location
        addObject(new PawnPromote(0), 240, 240);
        // The pieces to select for the pawn promotion
        for (int i = 0; i < 5; i++){
            PawnPromote p = new PawnPromote(i);
            addObject(p, i+1, 4);
        }
    }
    
    // When the user has selected a pawn to promote
    public void pawnPromote(){
        ChessPieces newPiece = null;
        boolean hasSelected = false;
        for (PawnPromote selection: getObjects(PawnPromote.class)){
            // I is 0 for the background which can be clicked and counted as an object, so this if statement reassigns the user to click again
            if (Greenfoot.mouseClicked(selection) && selection.getI() > 0){
                removeObjects(getObjects(PawnPromote.class));
                hasSelected = true;
                // Selection control
                switch (selection.getI()){
                    case 1: {
                        newPiece = new Knight(clickedPiece.isWhite());
                        break;
                    }
                    case 2: {
                        newPiece = new Bishop(clickedPiece.isWhite());
                        break;
                    }
                    case 3: {
                        newPiece = new Rook(clickedPiece.isWhite());
                        break;
                    }
                    case 4: {
                        newPiece = new Queen(clickedPiece.isWhite());
                        break;
                    }
                }
                addObject(newPiece, clickedPiece.getX(), clickedPiece.getY());
                board.generateTile(newPiece, clickedPiece.getX(), 7 - clickedPiece.getY());
                // Removes the pawn instance of the piece as it has been promoted
                removeObject(clickedPiece);
                // For show moves notation
                System.out.print("=" + newPiece.getClass().getName().charAt(0));
            }
        }
    }
    
    // Prepares the game
    public void prepareGame(){
        // Variables for each next of the file
        ChessPieces piece = null;
        int xPosition = 0, yPosition = 0;
        boolean ifWhite;
        
        // Asks the user to enter the position
        System.out.println("\nEnter the position you wish to play. (standard, chess960, KnightsVSBishops, PawnOnly, etc.)");
        File inputFile = new File (String.format(kbInput.next() + ".txt"));
        
        // Checks if the file is invalid
        try {
            readFile = new Scanner (inputFile);
        } catch(Exception e){
            System.out.println("File for the position is not found. Click new Game() to try again.");
            kbInput.close();
            System.exit(0);
            return;
        }
        
        // Generates empty tiles (tiles that are not occupied at the beginning of chess game)
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                board.generateTile(null, i, j);
            }
        }
        
        while (readFile.hasNext()){
            if (readFile.next().equalsIgnoreCase("white")){
                ifWhite = true;
            } else {
                ifWhite = false;
            }
            String chessPiece = readFile.next();
            xPosition = readFile.nextInt() - 1;
            yPosition = readFile.nextInt() - 1;
        
            // Generates pieces based on information from file    
        
            switch (chessPiece){
                case "Bishop": {
                    piece = new Bishop(ifWhite);
                    break;
                }
                case "Rook": {
                    piece = new Rook(ifWhite);
                    break;
                }
                case "Queen": {
                    piece = new Queen(ifWhite);
                    break;
                }
                case "King": {
                    piece = new King(ifWhite);
                    break;
                }
                case "Pawn": {
                    piece = new Pawn(ifWhite);
                    break;
                }
                case "Knight": {
                    piece = new Knight(ifWhite);
                    break;
                }
                default:
                    System.out.println("An error is found in the file.");
                    System.exit(0);
            }
            // Adds the object to the Chessboard
            addObject(piece, xPosition, 7-yPosition);
            board.generateTile(piece, xPosition, yPosition);
            numberOfPiecesRemaining++;
        }
        // Close the file and kbInput
        readFile.close();
        kbInput.close();
    }
    
    // Overloaded methods
    
    // Method to check if the king is checked after a turn
    public boolean isKingChecked(){
        // Grab the king
        for (ChessPieces king: getObjects(ChessPieces.class)){
            if (king.isWhite() == whiteTurn && king.getClass().equals(King.class)){
                for (int i = 0; i < 8; i++){
                    for (int j = 0; j < 8; j++){
                        // Piece that threatens the king, it is better to get the tile rather than the chess piece
                        Tile threateningPiece = board.getTile(i, j);
                        // If the target position is not null
                        if (threateningPiece.getPieceType() != null){
                            // If not the same color
                            if (threateningPiece.getPieceType().isWhite() != king.isWhite()){
                                // Check if the opposite colored piece is threatening to capture the king otherwise the king is not in check
                                // Last 2 arguments are null because the method should not assume anything unlike the method below
                                if (threateningPiece.getPieceType().ifTracedTile(board, threateningPiece, board.getTile(king.getX(), 7 - king.getY()), null, null)){
                                    // Check if no available moves. If no available moves then checkmate
                                    if (ifCheckmate(king)){
                                        isGameEnded = true;
                                        // # means checkmate in chess notation
                                        System.out.print("#");
                                        // The person with the opposite color to the checkmated king wins
                                        addObject(new GameEnded(true, !king.isWhite()), 240, 240);
                                    } else {
                                        // Notation for check
                                        System.out.print("+");
                                    }
                                    return true;
                                }
                            }
                        }
                    }
                }
            } 
        }
        return false;
    }
    
    // Method to check before the turn if the king is still checked after a certain condition move between a piece of the same color and its end position
    // Example: Checks if the enemy piece checking the king can be captured AND after that move, this method will check if the king is still in check
    // after the move (if its a double check or the piece was pinned to the king so it cannot move)
    // https://docs.google.com/document/d/1ajN2TpCbbchQb5FicrfBzH2_4C8iRHDPQv61RN-Jt9s/edit?usp=sharing
    // 2 extra arguments for the assumed position
    public boolean isKingChecked(Tile assumedPosition1, Tile assumedPosition2){
        // Grab the king
        for (ChessPieces king: getObjects(ChessPieces.class)){
            if (king.isWhite() == whiteTurn && king.getClass().equals(King.class)){
                for (int i = 0; i < 8; i++){
                    for (int j = 0; j < 8; j++){
                        // Piece that threatens the king
                        Tile threateningPiece = board.getTile(i, j);
                        Tile blockingPiece = null;
                        // If the target position is not null
                        if (threateningPiece.getPieceType() != null){
                            // Checks if the threateningPiece is the piece that is going to be captured
                            if (assumedPosition2 != threateningPiece){
                                blockingPiece = assumedPosition2;
                                // If not the same color, or the ignored positions
                                if (threateningPiece.getPieceType().isWhite() != king.isWhite()){
                                    // Check if the opposite colored piece is threatening to capture the king otherwise the king is not in check
                                    if (threateningPiece.getPieceType().ifTracedTile(board, threateningPiece, board.getTile(king.getX(), 7 - king.getY()), blockingPiece, assumedPosition1)){
                                        return true;
                                    }
                                }
                            }
                            // Not done yet, it still needs to check for more pieces that are possibly is checking the king
                        }
                    }
                }
            }
        }
        return false;
    }
    
    // Checks for checkmate (no available moves)
    public boolean ifCheckmate(ChessPieces king){
        // Grabs each piece with the same color as the king that is in check
        for (ChessPieces checkedPieces: getObjects(ChessPieces.class)){
            if (king.isWhite() == checkedPieces.isWhite()){
                // Checks if each piece has legal moves. If it doesn't then move on to the next. 
                // If there is no more, then there is no available moves and must be checkmate
                for (int i = 0; i < 8; i++){
                    for (int j = 0; j < 8; j++){
                        if(checkedPieces.legalMove(board, board.getTile(checkedPieces.getX(), 7-checkedPieces.getY()), board.getTile(i,j))){
                            // If one piece has a legal move then it should already return false
                            return false;
                        } 
                    }
                }
            }
        }
        // If none of them have legal moves, it must be checkmate
        return true;
    }
    
    // Checking for stalemate (This method is run BEFORE the checkmate to ensure that an actual checkmate isn't a stalemate)
    public boolean stalemate(){
        for (ChessPieces pieces: getObjects(ChessPieces.class)){
            if (pieces.isWhite() == whiteTurn){
                // Checks if a specific player with the turn has legal moves. If it doesn't then move on to the next 
                // If there is no more, then there is no available moves and must be stalemate
                for (int i = 0; i < 8; i++){
                    for (int j = 0; j < 8; j++){
                        // If one piece has a legal move then it should already return false
                        if(pieces.legalMove(board, board.getTile(pieces.getX(), 7-pieces.getY()), board.getTile(i,j))){
                            return false;
                        } 
                    }
                }
            }
        }
        // If none of them have legal moves
        return true;
    }
    
    // All the moves that have been played as a printed string
    public String showMoves(Tile destination, Tile startingPosition){
        // Knight should be N instead of K
        String pieceInitial = (clickedPiece.getClass().equals(Knight.class)) ? "N": String.valueOf(clickedPiece.getClass().getName().charAt(0));
        // Pawns should not have an initial
        if (clickedPiece.getClass().getName().charAt(0) == 'P'){
            pieceInitial = "";
        } 
        // If the same type of piece can hit the same destination, then it must be shown in column or row (x or y) which of those pieces in the board is moved to that location (Ex: Rha8)
        for (ChessPieces piece: getObjects(ChessPieces.class)){
            // If same piece and same color and has to not be a pawn
            // It also cannot refer to itself
            if (piece.isWhite() == clickedPiece.isWhite() && clickedPiece.getClass().equals(piece.getClass()) && !clickedPiece.getClass().equals(Pawn.class)
            && piece != clickedPiece){
                // If it can trace or hit the clicked piece's final destination
                // System.out.println(piece.getX() + "" + (7 - piece.getY()) + "\n" + clickedPiece.getX() + "" + (7 - clickedPiece.getY()));
                if (piece.ifTracedTile(board, board.getTile(piece.getX(), 7 - piece.getY()), board.getTile(clickedPiece.getX(), 7 - clickedPiece.getY()), null, null)){
                    // If not on same row, it must assign the coordinate of column instead
                    // For the format, it will be before the captured string which will be after the piece name
                    if (piece.getX() == startingPosition.getX()){
                        captured = String.valueOf(clickedPiece.getY()) + captured;
                    } else {
                        captured = toLetter[startingPosition.getX()] + captured;
                    }
                }
            }
        }
        // Chess notation for castling
        // To make white and black turn separate
        if (clickedPiece.isWhite()){
            if (Castle.equals("O-O-O") || Castle.equals("O-O")){
                return String.format("\n%d. %s", numberOfTurns, Castle);
            } else {
                return String.format("\n%d. %s%s%s%s", numberOfTurns, pieceInitial, captured, x, y);
            }
        }
        if (Castle.equals("O-O-O") || Castle.equals("O-O")){
            return String.format(", %s" , numberOfTurns, Castle);
        } else {
            return String.format(", %s%s%s%s", pieceInitial, captured, x, y);
        }
    }
}