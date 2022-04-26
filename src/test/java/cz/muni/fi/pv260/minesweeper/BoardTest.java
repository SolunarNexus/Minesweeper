package cz.muni.fi.pv260.minesweeper;

import org.assertj.core.api.AutoCloseableSoftAssertions;
import org.junit.jupiter.api.Test;

final class BoardTest {

    @Test
    void loadSimpleBoard() {
        var board = TestUtils.loadFromString("""
                MXM
                XXX
                XXM
                """);

        try (var softly = new BoardSoftAssertions(board)) {
            softly.assertRows(3);
            softly.assertColumns(3);
            softly.assertMines(3);

            softly.assertCell(0, 0, 'M');
            softly.assertCell(0, 1, '2');
            softly.assertCell(0, 2, 'M');

            softly.assertCell(1, 0, '1');
            softly.assertCell(1, 1, '3');
            softly.assertCell(1, 2, '2');

            softly.assertCell(2, 0, '0');
            softly.assertCell(2, 1, '1');
            softly.assertCell(2, 2, 'M');
        }
    }

    private static class BoardSoftAssertions extends AutoCloseableSoftAssertions {

        private final Board board;

        private BoardSoftAssertions(Board board) {
            this.board = board;
        }

        private void assertRows(int expectedRows) {
            assertThat(board.rows)
                    .as("Board rows")
                    .isEqualTo(expectedRows);
        }

        private void assertColumns(int expectedColumns) {
            assertThat(board.cols)
                    .as("Board columns")
                    .isEqualTo(expectedColumns);
        }

        private void assertMines(int expectedMines) {
            assertThat(board.mines)
                    .as("Mines in the board")
                    .isEqualTo(expectedMines);
        }

        private void assertCell(int row, int column, char expectedValue) {
            assertThat(board.getCell(row, column).value)
                    .as("Cell value at [%d, %d]", row, column)
                    .isEqualTo(expectedValue);
            assertThat(board.getCell(row, column).isRevealed)
                    .as("Cell at [%d, %d] should not be revealed", row, column)
                    .isFalse();
        }
    }
}
