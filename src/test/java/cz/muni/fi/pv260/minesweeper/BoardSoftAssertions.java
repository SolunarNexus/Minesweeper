package cz.muni.fi.pv260.minesweeper;

import org.assertj.core.api.AutoCloseableSoftAssertions;

final class BoardSoftAssertions extends AutoCloseableSoftAssertions {

    private final Board board;

    BoardSoftAssertions(String boardAsString) {
        this(TestUtils.loadFromString(boardAsString));
    }

    BoardSoftAssertions(Board board) {
        this.board = board;
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
