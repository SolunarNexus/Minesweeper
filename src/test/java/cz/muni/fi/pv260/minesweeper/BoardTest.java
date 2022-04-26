package cz.muni.fi.pv260.minesweeper;

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

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                assertThat(board.getCell(0, 1).isRevealed)
                        .as("Cell at [%d, %d] should not be revealed", row, col)
                        .isFalse();
            }
        }

        assertCell(board, 0, 0, 'M');
        assertCell(board, 0, 1, '2');
        assertCell(board, 0, 2, 'M');

        assertCell(board, 1, 0, '1');
        assertCell(board, 1, 1, '3');
        assertCell(board, 1, 2, '2');

        assertCell(board, 2, 0, '0');
        assertCell(board, 2, 1, '1');
        assertCell(board, 2, 2, 'M');
    }

    private void assertBoard(Board board, int expectedColumns, int expectedRows, int expectedMines) {
        assertThat(board.rows)
                .isEqualTo(expectedRows);

        assertThat(board.cols)
                .isEqualTo(expectedColumns);

        assertThat(board.mines)
                .isEqualTo(expectedMines);
    }

    private void assertCell(Board board, int row, int column, char value) {
        assertThat(board.getCell(row, column).value).isEqualTo(value);
    }
}
