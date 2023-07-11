package com.company;
import java.util.*;

public class Player {
    private final int difficulty; // difficulty of 0 means player.
    private final String symbol, enemySymbol; // X or O
    private int turns = 0; // number of turns the player/computer has played
    public static final char[] letters = {'a', 'b', 'c'}; // the letters in the board
    public static final ArrayList<String> center_segment = new ArrayList<>(Arrays.asList("a2", "b1", "c2", "b3")),
            corners = new ArrayList<>(Arrays.asList("a1", "a3", "c1", "c3")); // corners and center_segment coordinates
    public static Scanner userInput = new Scanner(System.in);

    public Player(int difficulty, String symbol){
        this.difficulty = difficulty;
        this.symbol = symbol;
        this.enemySymbol = symbol.equals("X")? "O": "X";
    }

    public void initiateMove(String[][] board, ArrayList<String> possibleMoves){
        /*
         * PURPOSE: Initiate a move based on whether it is a player or a computer.
         * @param String[][] board: The updated Tic-tac-toe board.
         * @param ArrayList<String> possibleMoves: The available moves for the player.
         */
        String move = "?";
        if (difficulty == 0){
            boolean validInput = false;
            while (!validInput){
                System.out.println("You are " + symbol + ". Choose your move " + possibleMoves + ":");
                String input = userInput.next();
                if (possibleMoves.contains(input)){
                    validInput = true;
                    move = input;
                } else if (Arrays.asList(Main.ALL_MOVES).contains(input)) {
                    System.out.println("Spot is already taken.");
                } else {
                    System.out.println("Invalid move.");
                }
            }
        } else if (doubleSymbols(symbol, board).size() != 0){ // Easy mode.
            move = numToLetter(getRandomIntItem(doubleSymbols(symbol, board)));
        } else if (doubleSymbols(enemySymbol, board).size() != 0){
            move = numToLetter(getRandomIntItem(doubleSymbols(enemySymbol, board)));
        } else if (difficulty > 1 && turns == 0) { // Medium mode.
            // Must not play a center_segment move after first turn, or we lose on a medium level.
            if (possibleMoves.contains("b2")) {
                move = "b2";
            } else {
                move = getRandomStringItem(getCommonArrays(possibleMoves, corners));
            }
        } else if (difficulty > 2){ // Impossible mode.
            if (symbol.equals("X")){
                if (turns == 1){
                    if (getCommonArrays(possibleMoves, corners).size() == 3){
                        /*
                        Opposite Corner allows us to trick the opponent.
                        If they ever play a center segment, they lose.
                        Second parameter is enemy corners.
                         */
                        move = getRandomStringItem(aligned(getCommonArrays(corners, possibleMoves), getCommonArrays(corners, getSpots(board, enemySymbol)), false));
                    } else {
                        /*
                        This is a guaranteed win we do not want to miss! (Opponent plays corner segment)
                         */
                        move = getRandomStringItem(getCommonArrays(possibleMoves, corners));
                    }
                } else if (turns == 2 && getCommonArrays(possibleMoves, corners).size() == 2){
                    /*
                    But now which corner should we choose? The one that traps the opponent!
                    Apparently the corner that traps the opponent does not inline with the opponent's center segment play.
                    Second parameter is enemy center segment.
                     */
                    move = getRandomStringItem(aligned(getCommonArrays(corners, possibleMoves), getCommonArrays(center_segment, getSpots(board, enemySymbol)), false));
                } else {
                    move = getRandomStringItem(possibleMoves);
                }
            } else {
                if (turns == 1){
                    if (board[1][1].equals(enemySymbol)) {
                        /*
                         Opponent played center after their first turn, assuming we play a corner move after.
                         The previous double symbol checker handles the rest of opponent's moves except when
                         the opponent plays a corner move opposite to their first corner move.
                         If we play a center segment move, the opponent will be forced to play a corner move,
                         trapping us.
                         So, we must play a corner move.
                         */
                        move = getRandomStringItem(getCommonArrays(possibleMoves, corners));
                    } else if (getCommonArrays(possibleMoves, corners).size() == 2){
                        /*
                         Opponent played corner after their first turn, assuming we play the middle move after.
                         The previous double symbol checker handles the rest of opponent's moves except when
                         the opponent plays a corner move opposite to their first corner move.
                         If we play a corner move, the opponent will be forced to play the last corner move,
                         trapping us.
                         So, we must play a center segment move.
                         */
                        move = getRandomStringItem(getCommonArrays(possibleMoves, center_segment));
                    } else if (getCommonArrays(possibleMoves, corners).size() == 3){
                         /*
                         Opponent has a corner and a center segment, assuming both not threatening to win.
                         We have the center.
                         We cannot play a move that is opposite of the row/column of the center segment
                         or the user will trap us with the L shape.
                         Can only play the other moves besides the above.
                         */
                        ArrayList <String> moves = new ArrayList<>();
                        String enemyCenterSegment = getRandomStringItem(getCommonArrays(center_segment, getSpots(board, enemySymbol)));

                        if (enemyCenterSegment.charAt(0) == 'b'){
                            moves.add("a2");
                            moves.add("c2");
                            moves.add("a" + enemyCenterSegment.charAt(1));
                            moves.add("c" + enemyCenterSegment.charAt(1));
                        } else {
                            moves.add("b1");
                            moves.add("b3");
                            moves.add(enemyCenterSegment.charAt(0) + "1");
                            moves.add(enemyCenterSegment.charAt(0) + "3");
                        }
                        move = getRandomStringItem(getCommonArrays(possibleMoves, moves));
                    } else if (getCommonArrays(possibleMoves, corners).size() == 4){
                        /*
                         Opponent has 2 center segments, and we have the center.
                         We cannot play a move that is not horizontally or vertically touching either these segments
                         or we will lose if the user traps us.
                         We can only play the corner moves that prevents this trap.
                         */
                        move = getRandomStringItem(aligned(getCommonArrays(corners, possibleMoves), getCommonArrays(center_segment, getSpots(board, enemySymbol)), true));
                    } else {
                        move = getRandomStringItem(possibleMoves);
                    }
                } else if (turns == 2 && (getCommonArrays(possibleMoves, corners).size() == 3) && (getCommonArrays(possibleMoves, center_segment).size() == 1)){
                    /*
                    Special case where we cannot play a center segment move, and a certain corner move. Or we are trapped.
                    Second parameter is our center segment. We want our corner move not aligned with it.
                     */
                    move = getRandomStringItem(aligned(getCommonArrays(possibleMoves, corners), getCommonArrays(center_segment, getSpots(board, symbol)), false));
                } else {
                    move = getRandomStringItem(possibleMoves);
                }
            }
        } else if (difficulty == 2 && turns == 1){
            // To balance medium difficulty
            move = getRandomStringItem(getCommonArrays(possibleMoves, corners));
        } else if (difficulty == 1 && turns == 0){
            move = getRandomStringItem(getCommonArrays(possibleMoves, center_segment));
        } else {
            move = getRandomStringItem(possibleMoves);
        }
        if (difficulty == 0){
            System.out.println("You played " + move + " as " + symbol);
        } else {
            System.out.println("Computer played " + move + " as " + symbol);
        }

        possibleMoves.remove(move);
        int[] num = letterToNum(move);
        board[num[0]][num[1]] = symbol;
        turns++;
    }

    private int[] letterToNum(String letter){
        /*
         * PURPOSE: Convert a letter coordinate (ex: a1, b2, c3, etc.) to the board index coordinate.
         * @param String letter: The letter coordinate to be converted.
         * @return new int[]{row, column}: The index coordinate to be returned.
         */
        int row = letter.charAt(1) - 49;
        int column = 0;
        for (int i = 0; i < letters.length; i++){
            if (letter.charAt(0) == letters[i]){
                column = i;
            }
        }
        return new int[]{row, column};
    }

    private String numToLetter(int[] num) {
        /*
         * PURPOSE: Convert a board index coordinate to a letter coordinate (ex: a1, b2, c3, etc.).
         * @param String: The board index coordinate to be converted.
         * @return new int[]{row, column}: The letter coordinate to be returned.
         */
        return letters[num[1]] + String.valueOf(num[0] + 1);
    }

    private ArrayList<String> getCommonArrays(ArrayList<String> arr1, ArrayList<String> arr2){
        /*
         * PURPOSE: Get the array of Strings with common items between two arrays of Strings. No duplicates.
         * @param ArrayList<String> arr1: The first array of Strings to be examined.
         * @param ArrayList<String> arr2: The second array of Strings to be examined.
         * @return ArrayList<String> arr3: The array of Strings contains common items among two arrays of Strings.
         */
        ArrayList<String> arr3 = new ArrayList<>();
        for (String item: arr1){
            if (arr2.contains(item) && !arr3.contains(item)){
                arr3.add(item);
            }
        }
        return arr3;
    }

    private String getRandomStringItem(ArrayList<String> arr){
        // PURPOSE: Randomly take an item from an array of strings.
        return arr.get((int)(Math.random() * arr.size()));
    }

    private int[] getRandomIntItem(ArrayList<int[]> arr){
        // PURPOSE: Randomly take an item from an array of integers.
        return arr.get((int)(Math.random() * arr.size()));
    }

    private ArrayList<int[]> doubleSymbols(String symbol, String[][] board){
        /*
         * PURPOSE: Check for symbols that are threatening to get three marks in a row, which are currently at two.
         * @param String symbol: The symbol to be checked for three marks in a row.
         * @param String[][] board: The board to be checked for symbol with three marks in a row.
         * @return ArrayList<int[]> coordinates: This is the coordinates of the given board, which has a symbol with three marks in a row.
         */
        ArrayList<int[]> coordinates = new ArrayList<>();

        for (int i = 0; i < board.length; i++) {
            int symbolCountRow = 0, emptyCountRow = 0, symbolCountColumn = 0, emptyCountColumn = 0;
            int[] emptySpotRow = new int[]{-1, -1}, emptySpotColumn = new int[]{-1, -1};

            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j].equals(symbol)) {
                    symbolCountRow++;
                }
                if (board[i][j].equals(" ")){
                    emptyCountRow++;
                    emptySpotRow = new int[]{i, j};
                }

                // 3 x 3 matrix anyway so?
                if (board[j][i].equals(symbol)) {
                    symbolCountColumn++;
                }
                if (board[j][i].equals(" ")){
                    emptyCountColumn++;
                    emptySpotColumn = new int[]{j, i};
                }
            }

            if (symbolCountRow == 2 && emptyCountRow == 1){
                coordinates.add(emptySpotRow);
            }
            if (symbolCountColumn == 2 && emptyCountColumn == 1){
                coordinates.add(emptySpotColumn);
            }
        }

        // 3x3 matrix anyway so?
        int emptyCountLeftDiagonal = 0, symbolCountLeftDiagonal = 0, emptyCountRightDiagonal = 0, symbolCountRightDiagonal = 0;
        int[] emptySpotLeftDiagonal = new int[]{-1, -1}, emptySpotRightDiagonal = new int[]{-1, -1};
        for (int i = 0; i < board.length; i++){
            if (board[i][i].equals(symbol)){
                symbolCountLeftDiagonal++;
            }
            if (board[i][i].equals(" ")){
                emptyCountLeftDiagonal++;
                emptySpotLeftDiagonal = new int[]{i, i};
            }

            if (board[2-i][i].equals(symbol)){
                symbolCountRightDiagonal++;
            }
            if (board[2-i][i].equals(" ")){
                emptyCountRightDiagonal++;
                emptySpotRightDiagonal = new int[]{2-i, i};
            }
        }

        if (symbolCountLeftDiagonal == 2 && emptyCountLeftDiagonal == 1){
            coordinates.add(emptySpotLeftDiagonal);
        }
        if (symbolCountRightDiagonal == 2 && emptyCountRightDiagonal == 1){
            coordinates.add(emptySpotRightDiagonal);
        }

        return coordinates;
    }

    private ArrayList<String> getSpots(String[][] board, String symbol){
        /*
         * PURPOSE: Get the coordinates of a symbol in terms of letter coordinates (ex: a1, b2, c3, etc.).
         * @param String[][] board: The board to be checked for symbol coordinates.
         * @param String symbol: The symbol to be found in board.
         * @return ArrayList<String> spots: The coordinates of a symbol in terms of letter coordinate.
         */
        ArrayList<String> spots = new ArrayList<>();
        for (String coordinate: Main.ALL_MOVES){
            if (board[(letterToNum(coordinate)[0])][letterToNum(coordinate)[1]].equals(symbol)){
                spots.add(coordinate);
            }
        }
        return spots;
    }

    private ArrayList<String> aligned(ArrayList<String> coordinates, ArrayList<String> symbols, boolean alignedCoordinates){
        /*
         * PURPOSE: Check if the symbol is aligned vertically or horizontally.
         */
        ArrayList<String> theIntersections = new ArrayList<>(), nonIntersections = new ArrayList<>();
        for (String coordinate: coordinates){
            boolean isIntersection = false;
            int index = 0;
            while (!isIntersection && index < symbols.size()){
                String symbol = symbols.get(index);
                if (coordinate.charAt(0) == symbol.charAt(0) || coordinate.charAt(1) == symbol.charAt(1)) {
                    isIntersection = true;
                }
                index++;
            }

            if (isIntersection){
                theIntersections.add(coordinate);
            } else {
                nonIntersections.add(coordinate);
            }
        }

        return alignedCoordinates ? theIntersections: nonIntersections;
    }

    public String getSymbol(){
        return symbol;
    }

    public int getDifficulty(){
        return difficulty;
    }
}
