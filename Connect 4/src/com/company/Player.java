package com.company;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Player {
    private final int difficulty;
    private final String symbol, enemySymbol, EMPTY_SPOT = "_";
    private static final int INFINITY = 2147483647, NEGATIVE_INFINITY = -2147483648;
    private int turns = 0;

    public Player(int difficulty, String symbol){
        this.difficulty = difficulty;
        this.symbol = symbol;
        enemySymbol = getEnemySymbol(symbol);
    }

    public String getSymbol(){
        return symbol;
    }

    private String getEnemySymbol(String symbol){return symbol.equals("X") ? "O": "X";}

    public int initiateMove(Board board, ArrayList<Integer> possibleMoves) {
        /*
         * PURPOSE: Initiate a move from a player or a computer.
         *
         * @param Board board: The current board of the game, which will be used to determine computer moves.
         * @param ArrayList<Integer> possibleMoves: The valid moves that can be played.
         *
         * @return int move: The move played by a player or a computer.
         */
        int move = difficulty == 0 ? userMove(possibleMoves): computerMove(board, possibleMoves); // Coordinate move, not the move entered by the player.

        System.out.println(symbol + " is played on column " + (move + 1));
        turns++;

        return move;
    }

    private int userMove(ArrayList<Integer> possibleMoves){
        /*
         * PURPOSE: Initiate a move from a player (difficulty = 0).
         *
         * @param ArrayList<Integer> possibleMoves: The valid moves that can be played.
         *
         * @return int move: The move played by a player.
         */
        Scanner input = new Scanner(System.in);
        int move = -1;

        boolean validInput = false;
        while (!validInput){
            System.out.print("You are " + symbol + ". Enter your move (" + (possibleMoves.get(0) + 1));
            for (int index = 1; index < possibleMoves.size(); index++){
                System.out.print("," + (possibleMoves.get(index) + 1));
            }
            System.out.println(").");
            String userInput = input.next();
            if (Main.isNumeric(userInput) && possibleMoves.contains(Integer.parseInt(userInput) - 1)){
                validInput = true;
                move = Integer.parseInt(userInput) - 1;
            } else {
                System.out.println("Invalid move. Try again.");
            }
        }

        return move;
    }

    private int computerMove(Board board, ArrayList<Integer> possibleMoves){
        /*
         * PURPOSE: Initiate a move from a computer.
         *          1 - Computer: Random Moves (SUPER Easy)
         *          2 - Computer: Easy
         *          3 - Computer: Normal
         *          4 - Computer: Hard
         *          5 - Computer: Insane
         * @param Board board: The current board of the game, which will be used to determine computer moves.
         * @param ArrayList<Integer> possibleMoves: The valid moves that can be played.
         * @return int move: The move played by a computer.
         */
        int move;
        if (difficulty > 1 && difficulty < 5) { // Easy, Medium, and Hard Difficulty.
            ArrayList<Integer> rowPossibleMoves = getRowPossibleMoves(board, possibleMoves);
            int selfQuadruplingMove = getQuadruplingMove(board, rowPossibleMoves, possibleMoves, symbol),
                    enemyQuadruplingMove = getQuadruplingMove(board, rowPossibleMoves, possibleMoves, enemySymbol);

            if (selfQuadruplingMove != -1){ // Prioritize a move that wins the game.
                move = selfQuadruplingMove;
            } else if (enemyQuadruplingMove != -1){ // The second priority is to prevent the opponent from winning. This is the last intelligence of easy difficulty.
                move = enemyQuadruplingMove;
            } else if (difficulty > 2){ // Medium and hard Difficulty. Score each valid move.
                int highestMoveScore = -1000000, bestMove = -1;
                // Choose the move with the highest score.
                for (int index = 0; index < possibleMoves.size(); index++){
                    int moveScore = getMoveScore(board, rowPossibleMoves.get(index), possibleMoves.get(index), symbol);
                    if (moveScore > highestMoveScore || (moveScore == highestMoveScore && Math.random() > 0.5)){
                        highestMoveScore = moveScore;
                        bestMove = possibleMoves.get(index);
                    }
                }

                move = bestMove;
            } else { // The easy difficulty will play a random valid move instead of having a scoring system.
                move = getRandomIntItem(possibleMoves);
            }

        } else if (difficulty == 5){ // Insane Difficulty. Not working properly or in progress.
            if (turns == 0){ // Best first moves.
                if (getRowPossibleMoves(board, possibleMoves).get(1) != 5) {
                    move = 2;
                } else if (getRowPossibleMoves(board, possibleMoves).get(5) != 5){
                    move = 4;
                } else {
                    move = 3;
                }
            } else { // The algorithm idea looked up online.
                move = minimax(board, 4, true, possibleMoves, -1, NEGATIVE_INFINITY, INFINITY, symbol).get(1); // Useless 4th parameter
            }
        } else { // This is the easiest difficulty = 1. It will randomly play valid moves.
            move = getRandomIntItem(possibleMoves);
        }

        return move;
    }

    private int getMoveScore(Board board, int rowMove, int columnMove, String sym){
        /*
         * PURPOSE: Determine a move's score for medium and higher difficulties.
         *
         * @param Board board: The current board of the game, which will be used to determine computer moves.
         * @param int rowMove: The row coordinate of a move.
         * @param int columnMove: The column coordinate of a move.
         *
         * @return int score: The score of the move.
         */
        int score = 0;

        score += isLosingNextMove(board, columnMove, sym) ? -10000: 0;
        score += isGuaranteedWinningNextMove(board, columnMove, sym) ? 4000: 0;
        score += isNotAvoidingGuaranteedLosingNextMoves(board, columnMove, sym) ? -1600: 0;
        score += isLosingNextMove(board, columnMove, getEnemySymbol(sym)) ? -640: 0; // Wait for the opponent to play this move.
        score += lineCount(board, rowMove, columnMove, sym, 3, false) > 0 ? (makesWinningThreat(board, columnMove, sym) ? 256: 101): 0;
        score += lineCount(board, rowMove, columnMove, sym, 3, true) > 0 ? (makesWinningThreat(board, columnMove, getEnemySymbol(sym)) ? 255: 100): 0;
        if (difficulty > 3){ // Hard difficulty attributes only.
            score += columnMove == 3 ? 102*(rowMove+1)/6: 0;
            score += columnMove == 4 || columnMove == 2 ? 42*(rowMove+1)/6: 0;
            score += columnMove == 1 || columnMove == 5 ? 42*(rowMove+1)/6: 0;
            score += lineCount(board, rowMove, columnMove, sym, 2, false)*18;
            score += lineCount(board, rowMove, columnMove, getEnemySymbol(sym), 2, true)*17;
        }

        return score;
    }

    private ArrayList<Integer> getRowPossibleMoves(Board board, ArrayList<Integer> possibleMoves){
        /*
         * PURPOSE: Get the row coordinate of the possible moves with a column coordinate.
         *
         * @param Board board: The current board of the game that will determine the position of the moves.
         * @param ArrayList<Integer> possibleMoves: An array of possible moves with a column coordinate.
         *
         * @return ArrayList<Integer> rowPossibleMoves: The row coordinate of the possible moves with a column coordinate.
         */
        ArrayList<Integer> rowPossibleMoves = new ArrayList<>();
        for (int column: possibleMoves){
            int rowCoordinate = 0;
            while (rowCoordinate != board.getBoard().length - 1 && board.getBoard()[rowCoordinate + 1][column].equals(EMPTY_SPOT)){
                rowCoordinate++;
            }
            rowPossibleMoves.add(rowCoordinate);
        }

        return rowPossibleMoves;
    }

    private int itemFrequency(ArrayList<String> arrayList, String item){
        /*
         * PURPOSE: Get the frequency of an item in an array list.
         *
         * @param ArrayList<String> arrayList: The list that will be searched for an item.
         * @param String item: The item that will have its frequency determined.
         *
         * @return int count: The number of times an item has been seen in an array list.
         */
        int count = 0;
        for (String arrayItem: arrayList) {
            if (arrayItem.equals(item)) {
                count++;
            }
        }

        return count;
    }

    private int getRandomIntItem(ArrayList<Integer> array){
        /*
         * PURPOSE: Get a random item from an array.
         *
         * @param ArrayList<Integer> array: The list that will be used to get an item.
         *
         * @return array.get((int)(Math.random() * array.size())): The item randomly taken from an array.
         */
        return array.get((int)(Math.random() * array.size()));
    }

    private int lineCount(Board board, int rowCoordinate, int columnCoordinate, String sym, int numLines, boolean includeVerticalLineCount){
        /*
         * PURPOSE: Determine whether a position on the board has a symbol alignment
         *          with a certain number of lines in horizontal, vertical, and diagonal directions.
         *
         * @param Board board: The board that will be examined for a symbol.
         * @param int rowCoordinate: The row coordinate of a position to be checked.
         * @param int columnCoordinate: The column coordinate of a position to be checked.
         * @param int numLines: The number of symbol alignment to be checked.
         * @param String sym: The symbol that will be checked on a position.
         * @param boolean includeVerticalLineCount: It is true if to include vertical lines.
         *
         * @return int lineCount: While a maximum of 3 or 4, the frequency of alignment among
         *                        horizontal, vertical, left, and right diagonal directions.
         */
        int lineCount = 0;
        Board assumedBoard = board.cloneBoard();
        assumedBoard.updateBoard(columnCoordinate, sym);
        String[][] arrayBoard = assumedBoard.getBoard();

        if (rowCoordinate < 7 - numLines && includeVerticalLineCount){ // Vertical. A column coordinate of four means the move is too deep to have a triple.
            ArrayList<String> toCheck = new ArrayList<>();
            for (int i = 0; i < numLines; i++){
                toCheck.add(arrayBoard[rowCoordinate+i][columnCoordinate]);
            }
            if (itemFrequency(toCheck, sym) == numLines){
                lineCount++;
            }
        }

        boolean horizontalLineFound = false;
        for (int columnIndex = columnCoordinate - 3; columnIndex < columnCoordinate + 1 && !horizontalLineFound; columnIndex++){ // Horizontal.
            if (columnIndex >= 0 && columnIndex <= arrayBoard[rowCoordinate].length - 4){
                ArrayList<String> toCheck = new ArrayList<>(Arrays.asList(arrayBoard[rowCoordinate]).subList(columnIndex, 4 + columnIndex));
                if (itemFrequency(toCheck, sym) == numLines && itemFrequency(toCheck, EMPTY_SPOT) == 4 - numLines){
                    lineCount++;
                    horizontalLineFound = true;
                }
            }
        }

        boolean leftDiagonalFound = false, rightDiagonalFound = false;
        for (int diagonalIndex = -3; diagonalIndex < 1 && (!leftDiagonalFound || !rightDiagonalFound); diagonalIndex++){
            if (!leftDiagonalFound &&
                    columnCoordinate + diagonalIndex >= 0 && columnCoordinate + diagonalIndex <= arrayBoard[0].length - 4 &&
                    rowCoordinate + diagonalIndex >= 0 && rowCoordinate + diagonalIndex <= arrayBoard.length - 4){ // Verify if the index is out of bounds.

                ArrayList<String> toCheck = new ArrayList<>(); // Left Diagonal.
                for (int i = 0; i < 4; i++){
                    toCheck.add(arrayBoard[rowCoordinate + diagonalIndex + i][columnCoordinate + diagonalIndex + i]);
                }

                if (itemFrequency(toCheck, sym) == numLines && itemFrequency(toCheck, EMPTY_SPOT) == 4 - numLines){
                    lineCount++;
                    leftDiagonalFound = true;
                }
            }

            if (!rightDiagonalFound &&
                    columnCoordinate + diagonalIndex >= 0 && columnCoordinate + diagonalIndex <= arrayBoard[0].length - 4 &&
                    rowCoordinate - diagonalIndex >= 3 && rowCoordinate - diagonalIndex <= arrayBoard.length - 1){

                ArrayList<String> toCheck = new ArrayList<>(); // Right Diagonal.
                for (int i = 0; i < 4; i++){
                    toCheck.add(arrayBoard[rowCoordinate - diagonalIndex - i][columnCoordinate + diagonalIndex + i]);
                }

                if (itemFrequency(toCheck, sym) == numLines && itemFrequency(toCheck, EMPTY_SPOT) == 4 - numLines){
                    lineCount++;
                    rightDiagonalFound = true;
                }
            }
        }

        return lineCount;
    }

    private int getQuadruplingMove(Board board, ArrayList<Integer> rowPossibleMoves, ArrayList<Integer> possibleMoves, String sym){
        /*
         * PURPOSE: Determine a move from a list of moves from a player that threatens a quadruple line of their pieces on the board.
         *
         * @param Board board: The board that will be examined for player's moves.
         * @param ArrayList<Integer> rowPossibleMoves: The list of row coordinates of a position to be checked.
         * @param ArrayList<Integer> possibleMoves: The list of column coordinates of a position to be checked.
         * @param String sym: The symbol that will be checked on a position.
         *
         * @return int quadruplingMove: A move from a list of moves by a player threatens
         *                              a quadruple line of their pieces on the board.
         */
        int quadruplingMove = -1;
        for (int i = 0; i < possibleMoves.size(); i++){
            if (lineCount(board, rowPossibleMoves.get(i), possibleMoves.get(i), sym, 4, true) > 0){
                quadruplingMove = possibleMoves.get(i);
            }
        }

        return quadruplingMove;
    }

    private boolean isLosingNextMove(Board board, int move, String sym){
        /*
         * PURPOSE: Determine whether a move allows a winning move for the opponent or the corresponding opposing symbol.
         *
         * @param Board board: The board that will be examined for the move.
         * @param int move: The move that will be analyzed for the outcome.
         * @param String sym: The symbol that will be checked on the board.
         *
         * @return boolean losingNextMove: The boolean is true if a move allows a winning move for the
         *                                 opponent or the corresponding opposing symbol and is false otherwise.
         */
        boolean losingNextMove = false;
        Board assumedBoard = board.cloneBoard();
        assumedBoard.updateBoard(move, sym);

        ArrayList<Integer> assumedPossibleMoves = assumedBoard.getPossibleMoves(),
                assumedRowPossibleMoves = getRowPossibleMoves(assumedBoard, assumedPossibleMoves);

        for (int i = 0; i < assumedPossibleMoves.size() && !losingNextMove; i++){
            if (lineCount(assumedBoard, assumedRowPossibleMoves.get(i), assumedPossibleMoves.get(i), getEnemySymbol(sym), 4, true) > 0){
                losingNextMove = true;
            }
        }
        return losingNextMove;
    }

    private boolean isGuaranteedWinningNextMove(Board board, int move, String sym){
        /*
         * PURPOSE: Determine whether a move is a guaranteed winning move, whatever the opponent plays after it.
         *
         * @param Board board: The board that will be examined for the move.
         * @param int move: The move that will be analyzed for the outcome.
         * @param String sym: The symbol that will be checked on the board.
         *
         * @return boolean guaranteedWinningNextMove: The boolean is true if a move is a guaranteed winning move,
         *                                            whatever the opponent plays after it, and false otherwise.
         */
        boolean guaranteedWinningNextMove = true;
        Board assumedBoard = board.cloneBoard();
        assumedBoard.updateBoard(move, sym);
        ArrayList<Integer> assumedPossibleMoves = assumedBoard.getPossibleMoves();

        for (Integer assumedPossibleMove : assumedPossibleMoves) {
            if (!isLosingNextMove(assumedBoard, assumedPossibleMove, getEnemySymbol(sym))) {
                guaranteedWinningNextMove = false;
                break;
            }
        }
        return guaranteedWinningNextMove;
    }

    private boolean isNotAvoidingGuaranteedLosingNextMoves(Board board, int move, String sym){
        /*
         * PURPOSE: Determine whether a move does not avoid a guaranteed winning next move for the opponent.
         *
         * @param Board board: The board that will be examined for the move.
         * @param int move: The move that will be analyzed for the outcome.
         * @param String sym: The symbol that will be checked on the board.
         *
         * @return boolean guaranteedWinningNextMove: The boolean is true if a move does not avoid a guaranteed
         *                                            winning next move for the opponent and is false otherwise.
         */
        boolean notAvoidingGuaranteedWinningNextMove = false;
        Board assumedBoard = board.cloneBoard();
        assumedBoard.updateBoard(move, sym);
        ArrayList<Integer> assumedPossibleMoves = assumedBoard.getPossibleMoves();

        // Filter moves that allow a guarantee of winning the next two moves for the opponent.
        for (Integer assumedPossibleMove : assumedPossibleMoves) {
            if (isGuaranteedWinningNextMove(assumedBoard, assumedPossibleMove, getEnemySymbol(sym))) {
                notAvoidingGuaranteedWinningNextMove = true;
                break;
            }
        }
        return notAvoidingGuaranteedWinningNextMove;
    }

    private boolean makesWinningThreat(Board board, int move, String sym){
        /*
         * PURPOSE: Determine whether a move makes a future winning threat, which is not the case when the move is immediately a winning threat to an opponent.
         *
         * @param Board board: The board that will be examined for the move.
         * @param int move: The move that will be analyzed for the outcome.
         * @param String sym: The symbol that will be checked on the board.
         *
         * @return boolean guaranteedWinningNextMove: The boolean is true if a move makes a future
         *                                            winning threat and is false otherwise.
         */
        Board assumedBoard = board.cloneBoard();
        assumedBoard.updateBoard(move, sym);
        ArrayList<Integer> assumedPossibleMoves = assumedBoard.getPossibleMoves();

        return getQuadruplingMove(assumedBoard, getRowPossibleMoves(assumedBoard, assumedPossibleMoves), assumedPossibleMoves, sym) == -1;
    }

    private ArrayList<Integer> minimax(Board board, int depth, boolean max, ArrayList<Integer> possibleMoves, int previousMove, int alpha, int beta, String sym) { // Alpha beta implemented.
        /*
         *** INCOMPLETE ***
         * PURPOSE: Use the minimax algorithm provided by the internet to simulate the hardest AI.
         */
        if (depth == 0 || possibleMoves.size() == 0 || !board.getWinner().equals("?")) {
            if (depth != 0) {
                if (board.getWinner().equals(sym)) {
                    return new ArrayList<>(List.of(1000000, previousMove)); // Win
                } else if (board.getWinner().equals(getEnemySymbol(sym))) {
                    return new ArrayList<>(List.of(-1000000, previousMove)); // Lose
                } else {
                    return new ArrayList<>(List.of(0, previousMove)); // Draw
                }
            } else {
                return new ArrayList<>(List.of(getMoveScore(board, getRowPossibleMoves(board, new ArrayList<>(List.of(previousMove))).get(0), previousMove, sym), previousMove));
            }
        }

        int score = max ? NEGATIVE_INFINITY : INFINITY;
        String newSym = max ? sym : getEnemySymbol(sym);
        int bestMove = getRandomIntItem(possibleMoves); // Don't really need to initialize this.
        for (int move : possibleMoves) {
            Board assumedBoard = board.cloneBoard();
            assumedBoard.updateBoard(move, newSym);
            ArrayList<Integer> assumedPossibleMoves = assumedBoard.getPossibleMoves();
            int newScore = max ? minimax(assumedBoard, depth - 1, false, assumedPossibleMoves, move, alpha, beta, newSym).get(0):
                    minimax(assumedBoard, depth - 1, true, assumedPossibleMoves, move, alpha, beta, newSym).get(0);
            if (max){
                if (newScore > score){
                    score = newScore;
                    bestMove = move;
                    alpha = Math.max(alpha, score);
                }
            } else {
                if (newScore < score){
                    score = newScore;
                    bestMove = move;
                    beta = Math.min(beta, score);
                }
            }
            if (alpha >= beta){
                break;
            }
        }
        return new ArrayList<>(List.of(score, bestMove));
    }
}
