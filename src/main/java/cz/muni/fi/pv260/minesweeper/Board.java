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
        this.mines = (int) cells.stream().filter(BoardCell::isMine).count();
        this.cells = new ArrayList<>(cells);

        // calculate numbers
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                char adjMinesChar = this.getAdjMinesCount(r, c);
                if (adjMinesChar != 'M') {
                    this.getCell(r, c).setValue(adjMinesChar);
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
            cells.add(new StandardCell(' '));
        }

        for (int i = 1; i < content.length; i++) {
            if (content[i].startsWith("R")) {
                var rowColStr = content[i].substring(1).split(",");
                Optional<Integer> row = parseCoordinate(rowColStr[0], rows);
                Optional<Integer> col = parseCoordinate(rowColStr[1], cols);
                if (row.isPresent() && col.isPresent()) {
                    cells.get(row.get() * cols + col.get()).setRevealed(true);
                } else {
                    return Optional.empty();
                }
            } else if (content[i].startsWith("F")) {
                var rowColStr = content[i].substring(1).split(",");
                Optional<Integer> row = parseCoordinate(rowColStr[0], rows);
                Optional<Integer> col = parseCoordinate(rowColStr[1], cols);
                if (row.isPresent() && col.isPresent()) {
                    cells.get(row.get() * cols + col.get()).toggleFlag();
                } else {
                    return Optional.empty();
                }
            } else {
                var rowColStr = content[i].split(",");
                Optional<Integer> row = parseCoordinate(rowColStr[0], rows);
                Optional<Integer> col = parseCoordinate(rowColStr[1], cols);
                if (row.isPresent() && col.isPresent()) {
                    cells.set(row.get() * cols + col.get(), new MineCell());
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

        if (cell.isFlagged()){
            return true;
        }

        cell.setRevealed(true);

        if (cell.getValue() == '0') {
            floodFill(row, col);
        }

        return !cell.isMine();
    }

    public boolean flag(int row, int col){
        if (cells == null){
            generateRandomBoard(row, col);
        }

        BoardCell cell = getCell(row, col);

        if (!cell.isRevealed()){
            cell.toggleFlag();
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

                if (!neighbourCell.isRevealed()) {
                    reveal(neighbourRow, neighbourCol);
                }
            }
        }
    }

    public boolean isCleared() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                var cell = getCell(r, c);
                if (!cell.isRevealed() && !cell.isMine()) {
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
                    if (cell.isRevealed()) {
                        out.printf(" %c ", cell.getValue());
                        continue;
                    }
                    if (cell.isFlagged()){
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
                if (getCell(r, c).isMine())
                    sb.append(String.format("%d,%d\n", r, c));

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (getCell(r, c).isRevealed())
                    sb.append(String.format("R%d,%d\n", r, c));

        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (getCell(r, c).isFlagged())
                    sb.append(String.format("F%d,%d\n", r, c));

        return Base64
                .getEncoder()
                .withoutPadding()
                .encodeToString(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    private void generateRandomBoard(int selectedRow, int selectedCol) {
        cells = new ArrayList<>(this.rows * this.cols);

        for (int i = 0; i < rows * cols; i++) {
            cells.add(new StandardCell(' '));
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
            if (!cell.isMine()) {
                cells.set(tmp1 * cols + tmp2, new MineCell());
                mc--;
            }
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                char adjMinesChar = getAdjMinesCount(r, c);
                if (adjMinesChar != 'M') {
                    getCell(r, c).setValue(adjMinesChar);
                }
            }
        }
    }

    private char getAdjMinesCount(int r, int c) {
        int counter = 0;

        if (!getCell(r, c).isMine()) {
            for (var dir : DIRECTIONS) {
                var row = dir[0] + r;
                var col = dir[1] + c;

                if (!isInBounds(row, col)) {
                    continue;
                }

                var adj = getCell(row, col);
                if (adj.isMine()) {
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
                sb.append(String.format("%c%c ", cell.isRevealed() ? '*' : ' ', cell.getValue()));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
