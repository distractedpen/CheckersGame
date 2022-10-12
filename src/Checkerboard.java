public class Checkerboard {

    private final Chip[][] board;       // variable board cannot be rebound, but can still (Ref type)
     public int redCount;
    public int blackCount;

    public Checkerboard() {
        board = new Chip[8][8];

        for (int row = 0; row < board.length; row++)
        {
            for (int col = 0; col < board[row].length; col++)
            {
                if ( (row < 3) && isValidSquare(row, col)) {
                    board[row][col] = new Chip(Chip.Color.RED);
                } else if ( ( row > 4 ) && isValidSquare(row, col)) {
                    board[row][col] = new Chip(Chip.Color.BLACK);
                } else {
                    board[row][col] = null;
                }
            }
        }

        redCount = 12;
        blackCount = 12;
    }

    public Chip getVauleAt(int row, int col) {
        return board[row][col];
    }

    /** Validate that the new position is on the board, valid, not occupied, and in valid direction */
    public boolean validMove(int[] oldPosition, int[] newPosition) {

        int oldRow = oldPosition[0];
        int oldCol = oldPosition[1];
        int newRow = newPosition[0];
        int newCol = newPosition[1];

        Chip chip = board[oldRow][oldCol];

        // no chip to move
        if (chip == null) {
            System.out.println("No chip to move");
            return false;
        }

        // check if old location is off board
        if (isOffBoard(oldRow, oldCol)) {
            System.out.println("Old Position not on board");
            return false;
        }

        // check if new location is off board
        if (isOffBoard(newRow, newCol)) {
            System.out.println("New Position not on board");
            return false;
        }

        // check if new loc is not a light square
        if (!isValidSquare(newRow, newCol)) {
            System.out.println("Invalid board position");
            return false;
        }

        // Check if new loc is occupied
        if (board[newRow][newCol] != null) {
            System.out.println("New Position is occupied");
            return false;
        }

        // Regular chips cannot go backward
        if ( !chip.isKing() && (( chip.getColor() == Chip.Color.BLACK && (oldRow - newRow) < 0) ||
             ( chip.getColor() == Chip.Color.RED  && (oldRow - newRow) > 0))) {
            System.out.println("Pawn chips cannot go backwards");
            return false;
        }

        // Position too far away! (Can only jump one piece at a time)
        if ( Math.abs(oldRow - newRow ) > 2) {
            System.out.println("Position too far away. Can only jump once piece at a time.");
            return false;
        }

        // if capturing, is it a valid capture?
        if (Math.abs(oldRow - newRow) == 2 && !canCapture(oldPosition, newPosition)) {
            System.out.println("Invalid Capture");
            return false;
        }

        return true;
    }

    /** Move chip to new location, clear old location.
     * @return True if player can move again, false otherwise
     * */
    public boolean update(int[] oldPosition, int[] newPosition) {

        int oldRow = oldPosition[0];
        int oldCol = oldPosition[1];
        int newRow = newPosition[0];
        int newCol = newPosition[1];

        Chip chip = board[oldRow][oldCol];

        // move piece to new location
        board[newRow][newCol] = chip;
        board[oldRow][oldCol] = null;

        // check for Coronations
        checkCoronation(newPosition);

        // check if capturing piece
        if ( Math.abs(oldRow - newRow) == 2 && Math.abs(oldCol - newCol) == 2) {
            capture(oldPosition, newPosition);
            return true;  // player can move again
        }

        return false; // cannot move again
    }

    /** Determine location of captured piece, remove it from board, decrement the counter of its color */
    private void capture(int[] oldPosition, int[] newPosition) {
        int oldRow = oldPosition[0];
        int oldCol = oldPosition[1];
        int newRow = newPosition[0];
        int newCol = newPosition[1];

        int otherChipRow = Math.floorDiv(oldRow+newRow, 2);
        int otherChipCol = Math.floorDiv(oldCol+newCol, 2);

        Chip otherChip = board[otherChipRow][otherChipCol];

        board[otherChipRow][otherChipCol] = null;
        if (otherChip.getColor() == Chip.Color.BLACK)
            blackCount--;
        else
            redCount--;
    }

    private boolean isOffBoard(int row, int col) {
        return row < 0 || row >= board.length || col < 0 || col >= board.length;
    }

    public boolean isValidSquare(int row, int col) {
        return (row % 2 == 0 && col % 2 == 1) || (row % 2 == 1 && col % 2 == 0);
    }

    public boolean canCapture(int[] oldPosition, int[] newPosition) {
        int oldRow = oldPosition[0];
        int oldCol = oldPosition[1];
        int newRow = newPosition[0];
        int newCol = newPosition[1];

        int otherChipRow = Math.floorDiv(oldRow+newRow, 2);
        int otherChipCol = Math.floorDiv(oldCol+newCol, 2);

        Chip chip = board[oldRow][oldCol];
        Chip otherChip = board[otherChipRow][otherChipCol];

        return !chip.equals(otherChip) && board[newRow][newCol] == null;
    }

    private void checkCoronation(int[] position)
    {
        int row = position[0];
        int col = position[1];

        Chip chip = board[row][col];

        if ( (chip.getColor() == Chip.Color.BLACK && row == 0) ||
            (chip.getColor() == Chip.Color.RED && row == 7)) {
            chip.makeKing();
        }
    }

    public void displayBoard()
    {
        System.out.println("   A B C D E F G H");
        System.out.println("-------------------");
        for (int row = 0; row < board.length; row++)
        {
            System.out.print(row+1 + " ");
            for (int col = 0; col < board[row].length; col++)
            {
                String marker;
                Chip chip = board[row][col];
                if (chip == null)
                    marker = " ";
                else if (chip.getColor() == Chip.Color.RED) {
                    marker = "o";
                } else {
                    marker = "x";
                }
                if (chip != null && chip.isKing()) marker = marker.toUpperCase();
                System.out.print("|" + marker);
            }

            System.out.print("|\n");
        }
        System.out.println("-------------------");
    }
}
