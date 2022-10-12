import java.util.InputMismatchException;
import java.util.Scanner;

public class ConsoleWindow {

    public static void main(String[] args) {

        Checkerboard board = new Checkerboard();
        int player = 1; // BLACK starts
        int turnsSinceLastCapture = 0;
        boolean gameOver = false;
        boolean pieceCaptured;

        board.displayBoard();
        while (!gameOver) {
            do {
                int[][] positions = getInput(player, board);
                pieceCaptured = board.update(positions[0], positions[1]);

                if (pieceCaptured)
                    turnsSinceLastCapture = 0;
                else
                    turnsSinceLastCapture++;

                if (pieceCaptured)
                    board.displayBoard();

            } while (pieceCaptured);

            int winner = determineWinner(turnsSinceLastCapture, board);
            if (winner != 0)
                gameOver = true;

            board.displayBoard();

            // Change to other player
            player = -player;
        }

        if (board.redCount == 0) {
            System.out.println("Black wins!");
        } else if (board.blackCount == 0) {
            System.out.println("Red Wins!");
        } else {
            System.out.println("Its a draw!");
        }

    }

    public static int[][] getInput(int player, Checkerboard board) {
        Scanner input = new Scanner(System.in);
        String oldLoc, newLoc;
        int[] oldPosition;
        int[] newPosition;

        if (player == 1)
            System.out.print("Black ");
        else
            System.out.print("Red ");

        do {
            oldLoc = getLoc(input, "Chip to Move: ");
            newLoc = getLoc(input, "Where to Place: ");
            oldPosition = parseLoc(oldLoc);
            newPosition = parseLoc(newLoc);
        } while (!board.validMove(oldPosition, newPosition));

        return new int[][] {oldPosition, newPosition};
    }


    public static String getLoc(Scanner input, String prompt)
    {
        String text;
        do
        {
            try {
                System.out.print(prompt);
                text = input.next("[A-H][1-8]"); // throws InputMismatchException
            } catch (InputMismatchException e) {
                input.next();
                text = null;
                System.out.println("Input a valid location");
            }
        }
        while (text == null);
        System.out.println(text);
        return text;
    }

    public static int[] parseLoc(String loc)
    {
        return new int[] {loc.charAt(1) - 49, loc.charAt(0) - 65};
    }

    public static int determineWinner(int turnsSinceLastCapture, Checkerboard board)
    {
        // 50 turns passed with no capture (draw)
        if (turnsSinceLastCapture > 50)
            return -1;
        // No red pieces remain   (black wins)
        if (board.redCount == 0)
            return 1;
        // No black pieces remain     (red wins)
        if (board.blackCount == 0)
            return 2;

        return 0;
    }

}
