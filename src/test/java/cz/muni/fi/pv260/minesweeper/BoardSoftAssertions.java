package cz.muni.fi.pv260.minesweeper;

import org.assertj.core.api.SoftAssertions;

import java.util.ArrayList;
import java.util.List;

final class BoardSoftAssertions implements AutoCloseable {

    private final Board board;
    private final SoftAssertions softly = new SoftAssertions();

    /**
     * Initializes the board from a condensed string representation.
     *
     * @param boardAsString condensed string representation of the board
     *
     * @see #loadFromString(String)
     */
    BoardSoftAssertions(String boardAsString) {
        this(loadFromString(boardAsString));
    }

    BoardSoftAssertions(Board board) {
        this.board = board;
    }

    /**
     * Loads and initializes the board from a condensed string representation.
     *
     * <p>Each line in the provided condensed string represents a row,
     * each character in each row represents a cell.
     * All the lines in the string must be of the same length!
     *
     * <p>The number of mines in the board is determined automatically
     * and the values of all the empty cells are calculated based on
     * the number of mines in adjacent cells.
     *
     * <p>Possible characters for cell representation:
     * <ul>
     *   <li>{@code 'M'} - represents a mine (naturally unrevealed)
     *   <li>{@code 'X'} - represents an empty cell (not revealed yet)
     *   <li>{@code '.'} - represents an empty revealed cell
     *   <li>{@code 'O'} - represents a flagged cell without mine (open position)
     *   <li>{@code 'C'} - represents a flagged cell with a mine (closed position)
     * <ul>
     * @param boardAsString condensed string representation of the board
     *
     * @return loaded and initialized board
     *
     * @throws IllegalArgumentException if the string is not formatted properly
     */
    static Board loadFromString(String boardAsString) {
        String[] lines = boardAsString.split("\n");
        int rows = lines.length;
        int cols = lines[0].trim().length();

        List<BoardCell> cells = new ArrayList<>(rows * cols);

        for (String line : lines) {
            for (char ch : line.toUpperCase().toCharArray()) {
                BoardCell cell = new StandardCell(' ');

                switch (ch) {
                    case 'M' -> cell = new MineCell();
                    case '.' -> cell.setRevealed(true);
                    case 'X' -> cell.setRevealed(false);
                    case 'O' -> cell.toggleFlag();
                    case 'C' -> { cell = new MineCell(); cell.toggleFlag(); }
                    default -> throw new IllegalArgumentException("Unsupported character: " + ch);
                }

                cells.add(cell);
            }
        }

        return new Board(rows, cols, cells);
    }

    void reveal(int row, int col) {
        board.reveal(row, col);
    }

    int flag(int row, int col) {
        return board.flag(row, col);
    }

    void assertRows(int expectedRows) {
        softly.assertThat(board.rows)
                .as("Board rows")
                .isEqualTo(expectedRows);
    }

    void assertColumns(int expectedColumns) {
        softly.assertThat(board.cols)
                .as("Board columns")
                .isEqualTo(expectedColumns);
    }

    void assertMines(int expectedMines) {
        softly.assertThat(board.mines)
                .as("Mines in the board")
                .isEqualTo(expectedMines);
    }

    void assertCleared(boolean expectedCleared) {
        softly.assertThat(board.isCleared())
                .as("Board is cleared")
                .isEqualTo(expectedCleared);
    }

    void assertBoardRevealed(String expectedBoard) {
        String[] lines = expectedBoard.split("\n");
        String actualBoard = "";
        for (int row = 0; row < lines.length; row++) {
            for (int column = 0; column < lines[row].length(); column++) {
                if (board.getCell(row, column).isFlagged()){
                    actualBoard += 'F';
                } else {
                    actualBoard += board.getCell(row, column).isRevealed() ? '.' : 'X';
                }
            }
            actualBoard += "\n";
        }
        softly.assertThat(actualBoard)
                .as("Board revealed")
                .isEqualTo(expectedBoard);
    }

    void assertBoardValues(String expectedBoard) {
        String[] lines = expectedBoard.split("\n");
        String actualBoard = "";
        for (int row = 0; row < lines.length; row++) {
            for (int column = 0; column < lines[row].length(); column++) {
                actualBoard += board.getCell(row, column).getValue();
            }
            actualBoard += "\n";
        }
        softly.assertThat(actualBoard)
                .as("Board values")
                .isEqualTo(expectedBoard);
    }

    void assertRow(int row, char... expectedValues) {
        for (int column = 0; column < expectedValues.length; column++) {
            assertCell(row, column, expectedValues[column]);
        }
    }

    void assertCell(int row, int column, char expectedValue) {
        softly.assertThat(board.getCell(row, column).getValue())
                .as("Cell value at [%d, %d]", row, column)
                .isEqualTo(expectedValue);
        softly.assertThat(board.getCell(row, column).isRevealed())
                .as("Cell at [%d, %d] should not be revealed", row, column)
                .isFalse();
    }

    void assertRevealedCell(int row, int column, char expectedValue) {
        softly.assertThat(board.getCell(row, column).getValue())
                .as("Cell value at [%d, %d]", row, column)
                .isEqualTo(expectedValue);
        softly.assertThat(board.getCell(row, column).isRevealed())
                .as("Cell at [%d, %d] should be revealed", row, column)
                .isTrue();
    }

    @Override
    public void close() {
        softly.assertAll();
    }
}
