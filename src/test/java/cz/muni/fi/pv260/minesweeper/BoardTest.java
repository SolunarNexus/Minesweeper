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

    @Test
    void loadLargerBoard() {
        var board = TestUtils.loadFromString("""
                MMMMMMM
                MXMXMXM
                MMMXMXX
                MXXMXXX
                """);

        try (var softly = new BoardSoftAssertions(board)) {
            softly.assertRows(4);
            softly.assertColumns(7);
            softly.assertMines(17);

            softly.assertRow(0, 'M', 'M', 'M', 'M', 'M', 'M', 'M');
            softly.assertRow(1, 'M', '8', 'M', '7', 'M', '6', 'M');
            softly.assertRow(2, 'M', 'M', 'M', '5', 'M', '3', '1');
            softly.assertRow(3, 'M', '4', '3', 'M', '2', '1', '0');
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

        private void assertRow(int row, char... expectedValues) {
            for (int column = 0; column < expectedValues.length; column++) {
                assertCell(row, column, expectedValues[column]);
            }
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
