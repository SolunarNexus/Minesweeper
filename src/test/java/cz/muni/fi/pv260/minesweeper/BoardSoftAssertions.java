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
     * </ul>
     *
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
                var cell = new BoardCell();
                cells.add(cell);

                switch (ch) {
                    case 'M' -> cell.value = 'M';
                    case '.' -> cell.isRevealed = true;
                    case 'X' -> cell.isRevealed = false;
                    default -> throw new IllegalArgumentException("Unsupported character: " + ch);
                }
            }
        }

        return new Board(rows, cols, cells);
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

    void assertRow(int row, char... expectedValues) {
        for (int column = 0; column < expectedValues.length; column++) {
            assertCell(row, column, expectedValues[column]);
        }
    }

    void assertCell(int row, int column, char expectedValue) {
        softly.assertThat(board.getCell(row, column).value)
                .as("Cell value at [%d, %d]", row, column)
                .isEqualTo(expectedValue);
        softly.assertThat(board.getCell(row, column).isRevealed)
                .as("Cell at [%d, %d] should not be revealed", row, column)
                .isFalse();
    }

    void assertRevealedCell(int row, int column, char expectedValue) {
        softly.assertThat(board.getCell(row, column).value)
                .as("Cell value at [%d, %d]", row, column)
                .isEqualTo(expectedValue);
        softly.assertThat(board.getCell(row, column).isRevealed)
                .as("Cell at [%d, %d] should be revealed", row, column)
                .isTrue();
    }

    @Override
    public void close() {
        softly.assertAll();
    }
}
