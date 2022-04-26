package cz.muni.fi.pv260.minesweeper;

import org.assertj.core.api.AutoCloseableSoftAssertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class BoardTest {

    @Test
    void loadSimpleBoard() {
        var board = TestUtils.loadFromString("""
                MXM
                XXX
                XXM
                """);

        assertBoard(board, 3, 3, 3);

        try (var softly = new BoardSoftAssertions(board)) {
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

    private void assertBoard(Board board, int expectedColumns, int expectedRows, int expectedMines) {
        assertThat(board.rows)
                .isEqualTo(expectedRows);

        assertThat(board.cols)
                .isEqualTo(expectedColumns);

        assertThat(board.mines)
                .isEqualTo(expectedMines);
    }

    private static class BoardSoftAssertions extends AutoCloseableSoftAssertions {

        private final Board board;

        private BoardSoftAssertions(Board board) {
            this.board = board;
        }

        private void assertCell(int row, int column, char value) {
            assertThat(board.getCell(row, column).value)
                    .as("Cell value at [%d, %d]", row, column)
                    .isEqualTo(value);
            assertThat(board.getCell(row, column).isRevealed)
                    .as("Cell at [%d, %d] should not be revealed", row, column)
                    .isFalse();
        }
    }
}
