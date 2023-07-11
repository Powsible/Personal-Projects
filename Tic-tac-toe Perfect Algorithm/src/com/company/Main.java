/**
 * Tic-tac-toe
 * @author       Christian Joel M. Ventura
 * @version      4/28/2023
 * PURPOSE: Implement an undefeatable Tic-tac-toe algorithm while creating a Tic-tac-toe interactive game with a user.
 */
package com.company;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static Scanner userInput;
    public static final int BOARD_ROWS = 3, BOARD_COLUMNS = 3;
    public static final String[] ALL_MOVES = {"a1", "a2", "a3", "b1", "b2", "b3", "c1", "c2", "c3"},
            options1 = {"Player VS Player", "Player VS Computer"}, options2 = {"easy", "medium", "impossible"}, options3 = {"X", "O"};
    public static final String NO_WINNER = "?";

    public static void main(String[] args) {
        System.out.println("CHRISTIAN'S TIC-TAC-TOE GAME");
        userInput = new Scanner(System.in);
        newGame();

        userInput.close();
        System.out.println("\n*******   Program terminated normally.   *******\n");
    }

    public static void newGame(){
        /*
         * PURPOSE: Ask the user to choose an opponent, a difficulty, and a symbol turn if the opponent is a computer for Tic-tac-toe.
         */
        int difficulty = 0;
        boolean startingFirst = true;

        if (validationLoop(options1) == 2){
            difficulty = validationLoop(options2);
            if (validationLoop(options3) == 2){
                startingFirst = false;
            }
        }

        startGame(difficulty, startingFirst);
    }

    public static int validationLoop(String[] options){
        /*
         * PURPOSE: Prompt the user to select a set of options with a validation loop.
         * @param String[] options: The set of choices the user can make.
         * @return Integer.parseInt(input) - 1: The index value of the choice within a set of choices.
         */
        boolean validInput = false;
        String input = "?";

        while (!validInput){
            System.out.println("---------------------");
            int numOptions = 0;
            for (String option: options){
                numOptions++;
                System.out.println(numOptions + ": " + option);
            }
            System.out.println("Choose your option:");
            input = userInput.next();
            if (isNumeric(input) && Integer.parseInt(input) <= numOptions && Integer.parseInt(input) > 0){
                validInput = true;
                System.out.println("You chose: " + options[Integer.parseInt(input) - 1]);
            } else {
                System.out.println("Invalid option. Try again.");
            }
        }

        return Integer.parseInt(input);
    }

    public static boolean isNumeric(String num) {
        /*
         * PURPOSE: Determine whether a number is an integer.
         * @param String num: The integer value to be examined.
         * @return boolean numeric: The boolean is true if the integer is numeric and is false otherwise.
         */
        boolean numeric = true;

        if (num == null) {
            numeric = false;
        } else {
            try {
                int check = Integer.parseInt(num);
            } catch (NumberFormatException nfe) {
                numeric = false;
            }
        }

        return numeric;
    }

    public static void startGame(int difficulty, boolean startingFirst){
        /*
         * PURPOSE: Start the Tic-tac-toe game based on difficulty.
         * @param int difficulty: An integer indicating the computer's difficulty from
         *                        1 to 3, where 1 is the easiest and 3 is the hardest.
         *                        A difficulty of 0 is player vs. player mode.
         * @param boolean startingFirst: The boolean is true if the player starts the move
         *                               first and false otherwise.
         */
        String[][] board = generateBoard();
        boolean playerXTurn = true;
        String winner = NO_WINNER;
        ArrayList<String> possibleMoves = new ArrayList<>(Arrays.asList(ALL_MOVES));
        boolean draw = false;

        Player playerX, playerO;

        if (!startingFirst){
            playerX = new Player(difficulty, "X");
            playerO = new Player(0, "O");
        } else {
            playerX = new Player(0, "X");
            playerO = new Player(difficulty,"O");
        }

        while (winner.equals(NO_WINNER) && !draw){
            displayBoard(board);

            if (playerXTurn){
                playerX.initiateMove(board, possibleMoves);
            } else {
                playerO.initiateMove(board, possibleMoves);
            }

            winner = checkWinner(board);
            draw = isDraw(board);
            playerXTurn = !playerXTurn;
        }

        displayBoard(board);

        if (draw && winner.equals(NO_WINNER)){
            System.out.println("Draw!");
        } else if (difficulty == 0){
            System.out.println(winner + " wins!");
        } else if (winner.equals(playerX.getSymbol()) && playerX.getDifficulty() == 0){
            System.out.println("You won!");
        } else {
            System.out.println("You lose!");
        }
    }

    public static String[][] generateBoard(){
        /*
         * PURPOSE: Generate a 2-dimensional array acting as a Tic-tac-toe board.
         * @return String[][] array: The 2-dimensional array to be returned.
         */
        String[][] array = new String[BOARD_ROWS][BOARD_COLUMNS];
        for (int i = 0; i < BOARD_ROWS; i++){
            for (int j = 0; j < BOARD_COLUMNS; j++){
                array[i][j] = " ";
            }
        }
        return array;
    }

    public static void displayBoard(String[][] board){
        /*
         * PURPOSE: Print a 3-by-3 2-dimensional array acting as a Tic-tac-toe board.
         * @param String[][] board: The 2-dimensional array to be printed.
         */
        System.out.println("\n" +
                "  |  a  |  b  |  c  |\n" +
                "---------------------\n" +
                "  |     |     |     |\n" +
                "1 |  " + board[0][0] + "  |  " + board[0][1] + "  |  " + board[0][2] + "  |\n" +
                "  |     |     |     |\n" +
                "---------------------\n" +
                "  |     |     |     |\n" +
                "2 |  " + board[1][0] + "  |  " + board[1][1] + "  |  " + board[1][2] + "  |\n" +
                "  |     |     |     |\n" +
                "---------------------\n" +
                "  |     |     |     |\n" +
                "3 |  " + board[2][0] + "  |  " + board[2][1] + "  |  " + board[2][2] + "  |\n" +
                "  |     |     |     |\n---------------------\n");
    }

    public static String checkWinner(String[][] board){
        /*
         * PURPOSE: Determine whether there is a winner in a Tic-tac-toe game or
         *          if there are three marks in a row (up, down, across, diagonally).
         * @param String[][] board: The 2-dimensional array to be checked for three marks in a row.
         * @return String winner: The winner to be returned, or if there is no winner, then returns NO_WINNER.
         */
        String winner = NO_WINNER;

        for (String[] strings : board) {
            if (strings[0].equals(strings[1]) && strings[0].equals(strings[2]) && !strings[0].equals(" ")) {
                winner = strings[0];
            }

            for (int j = 0; j < strings.length; j++) {
                if (board[0][j].equals(board[1][j]) && board[0][j].equals(board[2][j]) && !board[0][j].equals(" ")) {
                    winner = board[0][j];
                }
            }
        }

        if (board[0][0].equals(board[1][1]) && board[0][0].equals(board[2][2]) && !board[0][0].equals(" ")){
            winner = board[0][0];
        }

        if (board[2][0].equals(board[1][1]) && board[2][0].equals(board[0][2]) && !board[2][0].equals(" ")){
            winner = board[2][0];
        }

        return winner;
    }

    public static boolean isDraw(String[][] board){
        /*
         * PURPOSE: Determine whether the game is a draw or if there are no available moves.
         * @param String[][] board: The 2-dimensional array to be checked for no available moves.
         * @return boolean draw: The boolean is true if the game is drawn or false otherwise.
         */
        boolean draw = true;
        for (String[] strings : board) {
            for (String string : strings) {
                if (string.equals(" ")) {
                    draw = false;
                    break;
                }
            }
        }
        return draw;
    }
}

