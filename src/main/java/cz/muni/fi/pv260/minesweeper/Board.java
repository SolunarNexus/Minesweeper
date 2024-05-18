package cz.muni.fi.pv260.minesweeper;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.random.RandomGenerator;

public class Board {

    private static final int[][] DIRECTIONS = new int[][]{
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
    Long seed;

    Board(int rows, int cols, int mines, Long seed, int selectedRow, int selectedCol) {
        this.seed = seed;
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
        generateRandomBoard(selectedRow, selectedCol);
    }

    Board(int rows, int cols, Collection<BoardCell> cells) {

        this.rows = rows;
        this.cols = cols;
        this.mines = (int) cells.stream().filter(c -> c.value == 'M').count();
        this.cells = new ArrayList<>(cells);

        // calculate numbers
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                char adjMinesChar = this.getAdjMinesCount(r, c);
                if (adjMinesChar != 'M') {
                    this.getCell(r, c).value = adjMinesChar;
                }
            }
        }
    }

    public static Optional<Board> importBoard(String base64content) {
        var content = new String(Base64.getDecoder().decode(base64content), StandardCharsets.UTF_8)
                .split("\n");
        var rowColsStr = content[0].split(",");
        int rows = Integer.parseInt(rowColsStr[0]), cols = Integer.parseInt(rowColsStr[1]);

        List<BoardCell> cells = new ArrayList<>(rows * cols);

        for (int i = 0; i < rows * cols; i++) {
            cells.add(new BoardCell());
        }

        for (int i = 1; i < content.length; i++) {
            if (!content[i].startsWith("R")) {
                var rowColStr = content[i].split(",");
                Optional<Integer> row = parseCoordinate(rowColStr[0], rows);
                Optional<Integer> col = parseCoordinate(rowColStr[1], cols);
                if (row.isPresent() && col.isPresent()) {
                    cells.get(row.get() * cols + col.get()).value = 'M';
                } else {
                    return Optional.empty();
                }
            } else {
                var rowColStr = content[i].substring(1).split(",");
                Optional<Integer> row = parseCoordinate(rowColStr[0], rows);
                Optional<Integer> col = parseCoordinate(rowColStr[1], cols);
                if (row.isPresent() && col.isPresent()) {
                    cells.get(row.get() * cols + col.get()).isRevealed = true;
                } else {
                    return Optional.empty();
                }
            }
        }

        return Optional.of(new Board(rows, cols, cells));
    }

    private static Optional<Integer> parseCoordinate(String text, int max) {
        try {
            int value = Integer.parseInt(text);
            if (value < 0 || value >= max) {
                return Optional.empty();
            }
            return Optional.of(value);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public BoardCell getCell(int r, int c) {
        return cells.get(r * this.cols + c);
    }

    public boolean reveal(int row, int col) {
        if (cells == null) {
            generateRandomBoard(row, col);
        }

        BoardCell cell = getCell(row, col);

        if (cell.isFlagged){
            return true;
        }

        cell.isRevealed = true;

        if (cell.value == '0') {
            floodFill(row, col);
        }

        return cell.value != 'M';
    }

    public boolean flag(int row, int col){
        if (cells == null){
            generateRandomBoard(row, col);
        }

        BoardCell cell = getCell(row, col);

        if (!cell.isRevealed){
            cell.isFlagged = !cell.isFlagged;
            return true;
        }

        return false;
    }

    private void floodFill(int row, int col) {
        for (int[] direction : DIRECTIONS) {
            int neighbourRow = row + direction[0];
            int neighbourCol = col + direction[1];
            if (isInBounds(neighbourRow, neighbourCol)) {
                BoardCell neighbourCell = getCell(neighbourRow, neighbourCol);
                if (!neighbourCell.isRevealed) {
                    reveal(neighbourRow, neighbourCol);
                }
            }
        }
    }

    public boolean isCleared() {
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

    public void print(PrintStream out) {
        out.print("   ");
        for (int c = 0; c < cols; c++) {
            out.printf("%02d ", c);
        }
        out.println("");
        for (int r = 0; r < rows; r++) {
            out.printf("%02d ", r);
            for (int c = 0; c < cols; c++) {
                if (cells != null) {
                    var cell = getCell(r, c);
                    if (cell.isRevealed) {
                        out.printf(" %c ", cell.value);
                        continue;
                    }
                    if (cell.isFlagged){
                        out.print(" F ");
                        continue;
                    }
                }
                out.print(" X ");
            }
            out.println("");
        }
    }

    public String exportBoard() {
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("%d,%d\n", rows, cols));
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (getCell(r, c).value == 'M')
                    sb.append(String.format("%d,%d\n", r, c));

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (getCell(r, c).isRevealed)
                    sb.append(String.format("R%d,%d\n", r, c));

        return Base64
                .getEncoder()
                .withoutPadding()
                .encodeToString(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    private void generateRandomBoard(int selectedRow, int selectedCol) {
        cells = new ArrayList<BoardCell>(this.rows * this.cols);
        for (int i = 0; i < rows * cols; i++) {
            cells.add(new BoardCell());
        }
        RandomGenerator random = seed != null ? new Random(seed) : new Random();
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
            for (var dir : DIRECTIONS) {
                var row = dir[0] + r;
                var col = dir[1] + c;

                if (!isInBounds(row, col)) {
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

    boolean isInBounds(int row, int col) {
        if (row < 0 || row >= this.rows) {
            return false;
        }

        if (col < 0 || col >= this.cols) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (cells == null) {
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
