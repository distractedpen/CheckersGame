import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GameWindow extends JFrame implements ActionListener {

    // Swing Component and Game Model fields
    JFrame boardWindow;
    Checkerboard board;
    ArrayList<JButton> boardSquares;
    JLabel status;

    // Displaying Icon fields
    Icon emptyDarkIcon = new ImageIcon("resources/EmptyDarkSquare.png");
    Icon emptyLightIcon = new ImageIcon("resources/EmptyLightSquare.png");
    Icon redIcon = new ImageIcon("resources/Red.png");
    Icon blackIcon = new ImageIcon("resources/Black.png");
    Icon redKingIcon = new ImageIcon("resources/RedKing.png");
    Icon blackKingIcon = new ImageIcon("resources/BlackKing.png");

    // Game Logic fields
    int player, winner;
    String playerName;
    Chip.Color playerColor;
    boolean pieceCaptured;
    boolean receivedPieceToMove, receivedNewLocation;
    int turnsSinceLastCapture = 0;
    int[] oldPosition = new int[2];
    int[] newPosition = new int[2];

    public GameWindow() {
        boardWindow = new JFrame();
        boardWindow.setLayout(new GridBagLayout());
        boardWindow.setSize(800,800);
        boardWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });


        status = new JLabel();
        status.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 26));
        status.setSize(500, 100);

        resetGameBoard();
        boardWindow.setVisible(true);
    }

    private void resetGameBoard() {
        player = 1; // 1 is BLACK, -1 is RED
        playerName = "Black";
        playerColor = Chip.Color.BLACK;
        board = new Checkerboard();
        boardSquares = new ArrayList<>();

        GridBagConstraints c = new GridBagConstraints();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JButton square = new JButton();
                square.setName("square_" + row + "_" + col);
                square.setPreferredSize(new Dimension(75,75));
                square.setSize(emptyDarkIcon.getIconWidth(), emptyDarkIcon.getIconHeight());

                if (board.isValidSquare(row, col)) {
                    Chip chip = board.getVauleAt(row, col);
                    if (chip == null)
                        square.setIcon(emptyDarkIcon);
                    else if (chip.getColor() == Chip.Color.BLACK) {
                        if (chip.isKing()) square.setIcon(blackKingIcon);
                        else square.setIcon(blackIcon);
                    } else {
                        if (chip.isKing()) square.setIcon(redKingIcon);
                        else square.setIcon(redIcon);
                    }
                } else {
                    square.setIcon(emptyLightIcon);
                }

                c.gridx = col; c.gridy = row+1;


                boardSquares.add(square);
                boardWindow.add(square, c);
                square.addActionListener(this);
            }
        }

        c.gridx = 0; c.gridy = 9;
        c.gridwidth = 8;
        c.fill = GridBagConstraints.CENTER;
        status.setText("Black's Move! Choose a Piece.");
        boardWindow.add(status, c);
    }

    private void updateGameWindow() {
        for (JButton square : boardSquares) {
            Icon currIcon = square.getIcon();

            // Skip over Light Squares since they will never change
            if (currIcon.equals(emptyLightIcon))
                continue;

            String name = square.getName();
            String[] tokens = name.split("_");
            int row = Integer.parseInt(tokens[1]);
            int col = Integer.parseInt(tokens[2]);

            Chip boardValue = board.getVauleAt(row, col);

            if (boardValue == null)
                square.setIcon(emptyDarkIcon);
            else if (boardValue.getColor() == Chip.Color.BLACK) {
                if (boardValue.isKing()) square.setIcon(blackKingIcon);
                else square.setIcon(blackIcon);
            } else {
                if (boardValue.isKing()) square.setIcon(redKingIcon);
                else square.setIcon(redIcon);
            }
        }
    }

    private void changePlayer() {
        player = -player;
        if (player == 1) {
            playerName = "Black";
            playerColor = Chip.Color.BLACK;
        }
        else {
            playerName = "Red";
            playerColor = Chip.Color.RED;
        }
        status.setText(playerName + "'s Move.");
    }

    private int determineWinner()
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

    private void runGameOver() {
        for (JButton square : boardSquares) {
            square.setEnabled(false);
        }

        if (winner == 1) status.setText("Black Wins!");
        else if (winner == 2) status.setText("Red Wins!");
        else status.setText("It's a draw!");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        for (JButton square : boardSquares) {
            if (source == square) {
                String name = square.getName();
                String[] tokens = name.split("_");

                int row = Integer.parseInt(tokens[1]);
                int col = Integer.parseInt(tokens[2]);


                if (player == 1) playerName = "Black";
                else playerName = "Red";

                if (!receivedPieceToMove) {
                    oldPosition[0] = row;
                    oldPosition[1] = col;

                    Chip boardValue = board.getVauleAt(row, col);

                    if (boardValue == null) {
                        status.setText("No chip there. " + playerName + "'s move");
                    } else if (boardValue.getColor() != playerColor) {
                        status.setText("Can't move opponents chips. " + playerName + "'s move");
                    } else {
                        receivedPieceToMove = true;
                        status.setText(playerName + "'s Move. Choose New Location.");
                    }

                } else if (!receivedNewLocation) {

                    newPosition[0] = row;
                    newPosition[1] = col;
                    receivedNewLocation = true;

                    if (board.validMove(oldPosition, newPosition)) {

                        // do update
                        pieceCaptured = board.update(oldPosition, newPosition);
                        updateGameWindow();

                        if (pieceCaptured) {
                            status.setText(playerName + "'s Move again!");
                            turnsSinceLastCapture = 0;
                        }
                        else {
                            changePlayer();
                            turnsSinceLastCapture++;
                        }
                    } else {
                        status.setText("Invalid Move! " + playerName + "'s Move");
                    }

                    // clear trigger flags
                    receivedPieceToMove = false;
                    receivedNewLocation = false;

                    int winner = determineWinner();
                    if (winner != 0)
                        runGameOver();
                }

            }
        }
    }

    public static void main(String[] args) {
        new GameWindow();
    }
}
