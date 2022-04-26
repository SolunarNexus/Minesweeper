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

        assertThat(board.getCell(0, 0).value).isEqualTo('M');
        assertThat(board.getCell(0, 1).value).isEqualTo('2');
        assertThat(board.getCell(0, 2).value).isEqualTo('M');

        assertThat(board.getCell(1, 0).value).isEqualTo('1');
        assertThat(board.getCell(1, 1).value).isEqualTo('3');
        assertThat(board.getCell(1, 2).value).isEqualTo('2');

        assertThat(board.getCell(2, 0).value).isEqualTo('0');
        assertThat(board.getCell(2, 1).value).isEqualTo('1');
        assertThat(board.getCell(2, 2).value).isEqualTo('M');
    }

    private void assertBoard(Board board, int expectedColumns, int expectedRows, int expectedMines) {
        assertThat(board.rows)
                .isEqualTo(expectedRows);

        assertThat(board.cols)
                .isEqualTo(expectedColumns);

        assertThat(board.mines)
                .isEqualTo(expectedMines);
    }
}
