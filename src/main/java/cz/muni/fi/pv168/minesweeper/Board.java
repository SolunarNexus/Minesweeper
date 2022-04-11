package cz.muni.fi.pv168.minesweeper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;

public final class Board {
    static final int[][] DIRECTIONS = new int[][]{
            {0, 1},
            {1, 0},
            {1, 1},
            {0, -1},
            {-1, 0},
            {-1, -1},
            {1, -1},
            {-1, 1}
    };

    final int rows, cols, mines;
    List<BoardCell> cells = null;

    Board(int rows, int cols, int mines) {
        if (rows * cols <= mines)
            throw new IllegalArgumentException("Oops something went wrong");


        if (rows < 3 || cols < 3 || rows > 99 || cols > 99 || mines < 1)
            throw new IllegalArgumentException("Error");


        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
    }

    public BoardCell getCell(int r, int c) {
        checkTooSoon();
        return cells.get(r * this.cols + c);
    }

    public boolean reveal(int row, int col) {
        checkBounds(row, col);

        if (cells == null) {
            generateRandomBoard(row, col);
        }

        BoardCell cell = getCell(row, col);
        cell.isRevealed = true;

        return cell.value != 'M';
    }

    public boolean isCleared() {
        checkTooSoon();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                var cell = getCell(r, c);
                if (!cell.isRevealed && cell.value != 'M') {
                    return false;
                }
            }
        }

        return true;
    }


    public void print() {
        System.out.printf("   ");
        for (int c = 0; c < cols; c++) {
            System.out.printf("%02d ", c);
        }
        System.out.println("");
        for (int r = 0; r < rows; r++) {
            System.out.printf("%02d ", r);
            for (int c = 0; c < cols; c++) {
                if (cells != null) {
                    var cell = getCell(r, c);
                    if (cell.isRevealed) {
                        System.out.printf(" %c ", cell.value);
                        continue;
                    }
                }
                System.out.print(" X ");
            }
            System.out.println("");
        }
    }

    private void generateRandomBoard(int selectedRow, int selectedCol) {
        cells = new ArrayList<BoardCell>(this.rows * this.cols);
        for(int i = 0; i < rows * cols; i++) {
            cells.add(new BoardCell());
        }
        RandomGenerator random = new Random();
        int mc = mines;
        while (mc > 0) {
            var tmp1 = random.nextInt(this.rows);
            var tmp2 = random.nextInt(this.cols);
            if (tmp1 == selectedRow && tmp2 == selectedCol) {
                continue; // skip selected cell
            }

            BoardCell cell = cells.get(tmp1 * cols + tmp2);
            if (cell.value != 'M') {
                cell.value = 'M';
                mc--;
            }
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                char adjMinesChar = getAdjMinesCount(r, c);
                if (adjMinesChar != 'M') {
                    getCell(r, c).value = adjMinesChar;
                }
            }
        }
    }

    private char getAdjMinesCount(int r, int c) {
        int counter = 0;
        if (getCell(r, c).value != 'M') {
            for (var direction : DIRECTIONS) {
                var row = direction[0] + r;
                var col = direction[1] + c;

                try {
                    checkBounds(row, col);
                } catch (IndexOutOfBoundsException ex) {
                    continue;
                }

                var adj = getCell(row, col);
                if (adj.value == 'M') {
                    counter++;
                }
            }
            return Character.forDigit(counter, 10);
        }

        return 'M';
    }

    private void checkBounds(int row, int col) {
        if (row < 0 || row >= this.rows) {
            throw new IndexOutOfBoundsException(row);
        }

        if (col < 0 || col >= this.cols) {
            throw new IndexOutOfBoundsException(col);
        }
    }

    private void checkTooSoon() {
        if (cells == null) {
            throw new IllegalStateException("You called this too soon");
        }
    }

    @Override
    public String toString() {
        if(cells == null) {
            return "Not initialized!";
        }

        var sb = new StringBuffer();
        sb.append("   ");
        for (int c = 0; c < cols; c++) {
            sb.append(String.format("%02d ", c));
        }
        sb.append("\n");
        for (int r = 0; r < rows; r++) {
            sb.append(String.format("%02d ", r));
            for (int c = 0; c < cols; c++) {
                var cell = getCell(r, c);
                sb.append(String.format("%c%c ", cell.isRevealed ? '*' : ' ', cell.value));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
