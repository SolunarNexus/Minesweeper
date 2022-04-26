package cz.muni.fi.pv260.minesweeper;

import org.assertj.core.api.AutoCloseableSoftAssertions;

import java.util.ArrayList;
import java.util.List;

final class BoardSoftAssertions extends AutoCloseableSoftAssertions {

    private final Board board;

    BoardSoftAssertions(String boardAsString) {
        this(loadFromString(boardAsString));
    }

    BoardSoftAssertions(Board board) {
        this.board = board;
    }

    /**
     * Loads a board from provided string
     * each line represents a row, each char in row represents a column
     * <p>
     * Possible chars for cols:
     * - 'M' - represents a mine
     * - '.' - represents a cell that is revealed (and it is not a mine)
     * - 'X' - represents an unrevealed mine
     * FUTURE:
     * - 'F' - represents a correct flag on the cell, cell is flagged and it is mine
     * - 'W` - wrong flag - the flagged cell is not a mine!
     *
     * @param raw board representation
     * @return initialized board
     */
    static Board loadFromString(String raw) {
        var lines = raw.split("\n");
        var rows = lines.length;
        var cols = lines[0].trim().length();

        List<BoardCell> cells = new ArrayList<>(rows * cols);

        for (var line : lines) {
            for (char ch : line.toUpperCase().toCharArray()) {
                var cell = new BoardCell();
                cells.add(cell);

                switch (ch) {
                    case 'M' -> cell.value = 'M';
                    case '.' -> cell.isRevealed = true;
                    case 'X' -> cell.isRevealed = false;
                    case 'W', 'F' -> throw new UnsupportedOperationException("Not implemented yet");
                    default -> throw new IllegalArgumentException("Unsupported character: " + ch);
                }
            }
        }

        return new Board(rows, cols, cells);
    }

    void assertRows(int expectedRows) {
        assertThat(board.rows)
                .as("Board rows")
                .isEqualTo(expectedRows);
    }

    void assertColumns(int expectedColumns) {
        assertThat(board.cols)
                .as("Board columns")
                .isEqualTo(expectedColumns);
    }

    void assertMines(int expectedMines) {
        assertThat(board.mines)
                .as("Mines in the board")
                .isEqualTo(expectedMines);
    }

    void assertRow(int row, char... expectedValues) {
        for (int column = 0; column < expectedValues.length; column++) {
            assertCell(row, column, expectedValues[column]);
        }
    }

    void assertCell(int row, int column, char expectedValue) {
        assertThat(board.getCell(row, column).value)
                .as("Cell value at [%d, %d]", row, column)
                .isEqualTo(expectedValue);
        assertThat(board.getCell(row, column).isRevealed)
                .as("Cell at [%d, %d] should not be revealed", row, column)
                .isFalse();
    }
}
