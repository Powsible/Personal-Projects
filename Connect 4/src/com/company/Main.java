/**
 * Tic-tac-toe
 * @author       Christian Joel M. Ventura
 * @version      7/7/2023
 * PURPOSE: Implement a nearly perfect Connect 4 algorithm while creating an interactive Connect 4 game with a user.
 */
package com.company;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String symbolX = "X", symbolO = "O";
    private static final ArrayList<String>
            gameFormats = new ArrayList<>(List.of("Player vs Player", "Computer vs Player", "Computer vs Computer")),
            difficulties = new ArrayList<>(List.of("Random Moves (SUPER Easy)", "Easy", "Medium", "Hard", "Insane (Idea from internet, not properly working)")),
            symbols = new ArrayList<>(List.of(symbolX, symbolO)), end = new ArrayList<>(List.of("Yes", "No", "Select different game format."));
    private static final int BOARD_ROWS = 6, BOARD_COLUMNS = 7;

    public static void main(String[] args) {
        System.out.println("Christian's CONNECT 4 GAME");

        // menu();
        playComputersForAmount(4, 3, 10000);
        // playComputersUntilNoWin(4, 3, true);

        System.out.println("*******   Program terminated normally.   *******");
    }

    public static void menu(){
        /*
         * PURPOSE: Displays the main menu to prompt the user to select a game format and prints the winners of each game.
         */
        boolean repeatMenu = true;
        while (repeatMenu){
            int difficulty = 0, difficulty2 = 0; // The difficulty of the second computer is difficulty2. Otherwise, it's the player.
            String gameFormat = validationChoice(gameFormats, "Game format.");
            boolean userStartsFirst = true, repeatGame = true;

            if (gameFormats.indexOf(gameFormat) != 0){ // When it is not player vs. player.
                difficulty = difficulties.indexOf(validationChoice(difficulties, "Computer Difficulty.")) + 1;
                if (gameFormats.indexOf(gameFormat) == 2){ // When it is computer vs. computer.
                    difficulty2 = difficulties.indexOf(validationChoice(difficulties, "Second Computer Difficulty.")) + 1;
                } else if (validationChoice(symbols, "User symbol.").equals(symbols.get(1))){
                    userStartsFirst = false;
                }
            }

            int winCountX = 0, winCountO = 0, drawCount = 0;
            while (repeatGame){
                String winner = startGame(gameFormat, difficulty, difficulty2, userStartsFirst);
                if (winner.equals(symbolX)){
                    winCountX++;
                } else if (winner.equals(symbolO)) {
                    winCountO++;
                } else {
                    drawCount++;
                }
                System.out.println("\n" + symbolX + ": " + winCountX + "\n" + symbolO + ": " + winCountO + "\nDraw: " + drawCount + "\n");

                int indexInput = end.indexOf(validationChoice(end, "Play again?"));
                if (indexInput > 0){ // End the loop of playing the same game format.
                    repeatGame = false;
                    if (indexInput == 1){ // End the menu or the program.
                        repeatMenu = false;
                    }
                }
            }
        }
    }

    public static String validationChoice(ArrayList<String> choices, String extraMessage){
        /*
         * PURPOSE: Prompt the user to select a set of options with a validation loop.
         *
         * @param ArrayList<String> choices: The set of choices the user can make.
         * @param String extraMessage: The title or extra information about the prompt.
         *
         * @return String userChoice: A proper choice that the user made.
         */
        Scanner input = new Scanner(System.in);
        boolean validInput = false;
        String userChoice = "?";

        while (!validInput){
            int choiceCount = 0;
            System.out.println("____________________\n" + extraMessage);
            for (String choice : choices) {
                choiceCount++;
                System.out.println(choiceCount + ": " + choice);
            }

            System.out.println("Enter your choice.");
            String userInput = input.next();
            if (isNumeric(userInput) && Integer.parseInt(userInput) <= choiceCount && Integer.parseInt(userInput) > 0){
                validInput = true;
                userChoice = choices.get(Integer.parseInt(userInput) - 1);
            } else {
                System.out.println("Invalid input. Try again.");
            }
        }

        return userChoice;
    }

    public static boolean isNumeric(String num){
        /*
         * PURPOSE: Determine whether a number is an integer.
         *
         * @param String num: The integer value to be examined.
         *
         * @return boolean numeric: The boolean is true if the integer is numeric and is false otherwise.
         */
        boolean numeric = true;

        try {
            Integer.parseInt(num);
        } catch(NumberFormatException error) {
            numeric = false;
        }

        return numeric;
    }

    private static String startGame(String gameFormat, int difficulty, int difficulty2, boolean userStartsFirst){
        /*
         * PURPOSE: Start a Connect 4 game based on a game format.
         *
         * @param String gameFormat: The game format that determines whether the mode is player vs. player,
         *                           player vs. computer, or computer vs. computer.
         * @param int difficulty, difficulty 2: The integer value for each difficulty:
         *                                      0 - Player
         *                                      1 - Computer: Random Moves (SUPER Easy)
         *                                      2 - Computer: Easy
         *                                      3 - Computer: Normal
         *                                      4 - Computer: Hard
         *                                      5 - Computer: Insane
         *           FUN EXPERIMENTAL PROBABILITY - If a computer plays against another that is 1 difficulty below it, it will have a 90–95% chance of winning!
         * @param boolean userStartsFirst: The boolean is true if the user starts first or is "X" and false otherwise.
         *
         * @return String winner: The symbol winner of the game.
         */
        Board board = new Board(BOARD_ROWS, BOARD_COLUMNS);
        Player playerX, playerO;
        ArrayList<Integer> possibleMoves = board.getPossibleMoves();
        String winner = "?";
        boolean draw = false, playerXTurn = true;

        if (gameFormat.equals(gameFormats.get(1)) && userStartsFirst){
            playerX = new Player(difficulty2, symbolX); // difficulty2 is the player for this game format.
            playerO = new Player(difficulty, symbolO);
        } else {
            playerX = new Player(difficulty, symbolX);
            playerO = new Player(difficulty2, symbolO);
        }

        while (winner.equals("?") && !draw){
            board.displayBoard();
            if (playerXTurn){
                board.updateBoard(playerX.initiateMove(board, possibleMoves), symbolX);
            } else {
                board.updateBoard(playerO.initiateMove(board, possibleMoves), symbolO);
            }
            winner = board.getWinner();
            possibleMoves = board.getPossibleMoves();
            if (possibleMoves.size() == 0){
                draw = true;
            }
            playerXTurn = !playerXTurn;
        }

        board.displayBoard();

        if (!winner.equals("?")){
            if (gameFormat.equals(gameFormats.get(1))){
                if ((userStartsFirst && winner.equals(playerX.getSymbol())) || (!userStartsFirst && winner.equals(playerO.getSymbol()))){
                    System.out.println("You win!");
                } else {
                    System.out.println("You lose!");
                }
            } else {
                System.out.println(winner + " wins!");
            }
        } else {
            System.out.println("Draw!");
        }

        return winner;
    }

    public static void playComputersUntilNoWin(int difficulty1, int difficulty2, boolean difficulty1WaitToLose){
        /*
         * PURPOSE: Simulate games between two computers until one of them stops winning.
         *
         * @param int difficulty, difficulty 2: The integer value for each difficulty:
         *                                      1 - Computer: Random Moves (SUPER Easy)
         *                                      2 - Computer: Easy
         *                                      3 - Computer: Normal
         *                                      4 - Computer: Hard
         *                                      5 - Computer: Insane
         * @param boolean difficulty1WaitToLose: The boolean is true if difficulty1 is what is
         *                                       waited to lose and false if difficulty2 is so.
         */
        String winner = startGame(gameFormats.get(2), difficulty1, difficulty2, true),
                symbol = difficulty1WaitToLose? symbolX: symbolO;

        int count = 0;
        while (winner.equals(symbol)){
            count++;
            System.out.println("Computer " + symbol + " has won " + count + " times!");
            winner = startGame(gameFormats.get(2), difficulty1, difficulty2, true);
        }

        System.out.println("Computer " + symbol + " has won " + count + " times before finally stopped winning!");
    }

    public static void playComputersForAmount(int difficulty1, int difficulty2, int count){
        /*
         * PURPOSE: Simulate games between two computers for a given amount of time.
         *
         * @param int difficulty, difficulty 2: The integer value for each difficulty:
         *                                      1 - Computer: Random Moves (SUPER Easy)
         *                                      2 - Computer: Easy
         *                                      3 - Computer: Normal
         *                                      4 - Computer: Hard
         *                                      5 - Computer: Insane
         * @param int count: The number of times the game will be played.
         */
        int countX = 0, countO = 0;
        for (int i = 0; i < count; i++){
            if (startGame(gameFormats.get(2), difficulty1, difficulty2, true).equals("X")){
                countX++;
            } else {
                countO++;
            }
            System.out.println("X: " + countX);
            System.out.println("O: " + countO);
        }
    }
}

