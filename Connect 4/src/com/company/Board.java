package com.company;

import java.util.ArrayList;

public class Board {
    private final String[][] board; // This does not mean that the items in the board can be changed.
    private final String EMPTY_SPOT = "_";

    public Board(int rows, int columns){ // Constructor used for creating a new board.
        board = new String[rows][columns];
        for (int rowIndex = 0; rowIndex < rows; rowIndex++){
            for (int columnIndex = 0; columnIndex < columns; columnIndex++){
                board[rowIndex][columnIndex] = EMPTY_SPOT;
            }
        }
    }

    public Board(String[][] board){ // Constructor used for cloning.
        this.board = board;
    }

    public void displayBoard(){
        /*
         * PURPOSE: Displays the current board.
         */
        for (String[] row : board) {
            System.out.print("\n|");
            for (String spot : row) {
                System.out.print("_" + spot + "_|");
            }
        }

        System.out.println("\n  1   2   3   4   5   6   7\n");
    }

    public String getWinner(){
        /*
         * PURPOSE: Determine whether there is a winner from a Connect 4 board.
         *
         * @return String winner: The symbol winner of the game.
         */
        String winner = "?";

        for (String[] row : board) {
            for (int columnIndex = 0; columnIndex < row.length - 3; columnIndex++) { // Horizontal.
                if (!row[columnIndex].equals(EMPTY_SPOT) && row[columnIndex].equals(row[columnIndex+1]) &&
                        row[columnIndex].equals(row[columnIndex+2]) && row[columnIndex].equals(row[columnIndex+3])){
                    winner = row[columnIndex];
                }
            }
        }

        for (int rowIndex = 0; rowIndex < board.length - 3 && winner.equals("?"); rowIndex++) { // Vertical.
            for (int columnIndex = 0; columnIndex < board[rowIndex].length; columnIndex++) {
                if (!board[rowIndex][columnIndex].equals(EMPTY_SPOT) && board[rowIndex][columnIndex].equals(board[rowIndex+1][columnIndex]) &&
                        board[rowIndex][columnIndex].equals(board[rowIndex+2][columnIndex]) && board[rowIndex][columnIndex].equals(board[rowIndex+3][columnIndex])){
                    winner = board[rowIndex][columnIndex];
                }
            }
        }

        for (int rowIndex = 0; rowIndex < board.length - 3 && winner.equals("?"); rowIndex++) {
            for (int columnIndex = 0; columnIndex < board[rowIndex].length - 3; columnIndex++) {
                if (!board[rowIndex][columnIndex].equals(EMPTY_SPOT) && board[rowIndex][columnIndex].equals(board[rowIndex+1][columnIndex+1]) &&
                        board[rowIndex][columnIndex].equals(board[rowIndex+2][columnIndex+2]) && board[rowIndex][columnIndex].equals(board[rowIndex+3][columnIndex+3])){
                    winner = board[rowIndex][columnIndex];
                } // Left Diagonal.

                if (!board[board.length - rowIndex - 1][columnIndex].equals(EMPTY_SPOT) &&
                        board[board.length - rowIndex - 1][columnIndex].equals(board[board.length - rowIndex - 2][columnIndex+1]) &&
                        board[board.length - rowIndex - 1][columnIndex].equals(board[board.length - rowIndex - 3][columnIndex+2]) &&
                        board[board.length - rowIndex - 1][columnIndex].equals(board[board.length - rowIndex - 4][columnIndex+3])){
                    winner = board[board.length - rowIndex - 1][columnIndex];
                } // Right Diagonal.
            }
        }

        return winner;
    }

    public ArrayList<Integer> getPossibleMoves(){
        /*
         * PURPOSE: Determine the valid moves from a Connect 4 board.
         *
         * @return ArrayList<Integer> possibleMoves: The valid moves from a Connect 4 board.
         */
        ArrayList<Integer> possibleMoves = new ArrayList<>();
        for (int columnIndex = 0; columnIndex < board[0].length; columnIndex++){
            if (board[0][columnIndex].equals(EMPTY_SPOT)){
                possibleMoves.add(columnIndex);
            }
        }

        return possibleMoves;
    }

    public void updateBoard(int move, String symbol){
        /*
         * PURPOSE: Update a Connect 4 board based on a move and a token.
         *
         * @param int move: The integer that determines the initiated move on a Connect 4 board.
         * @param String symbol: The token that will be added to the board as a move.
         */
        for (int rowIndex = 0; rowIndex < board.length; rowIndex++){
            if (rowIndex == board.length - 1 || !board[rowIndex+1][move].equals(EMPTY_SPOT)){
                board[rowIndex][move] = symbol;
                break; // We ONLY update one spot.
            }
        }
    }

    public Board cloneBoard(){
        /*
         * PURPOSE: Clone an existing board.
         *
         * @return new Board(newBoard): The new board cloned from a board with a cloned array.
         */
        String[][] newBoard = new String[board.length][board[0].length];
        for (int rowIndex = 0; rowIndex < board.length; rowIndex++){
            System.arraycopy(board[rowIndex], 0, newBoard[rowIndex], 0, board[0].length);
        }

        return new Board(newBoard);
    }

    public String[][] getBoard(){return board;}
}
