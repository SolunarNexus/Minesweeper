package cz.muni.fi.pv168.minesweeper;


import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;

final class BoardTest {
    @Test
    void loadSimpleBoard() throws ParseException {
        var board = TestUtils.loadFromString("""
                XXX
                XMX
                XXX
                """);

        assertThat(board.rows)
                .isEqualTo(3);

        assertThat(board.cols)
                .isEqualTo(3);

        assertThat(board.mines)
                .isEqualTo(1);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                assertThat(board.getCell(0, 1).isRevealed)
                        .as("Cell at [%d, %d] should not be revealed", row, col)
                        .isFalse();
            }
        }

        assertThat(board.getCell(0, 0).value).isEqualTo('1');
        assertThat(board.getCell(0, 1).value).isEqualTo('1');
        assertThat(board.getCell(0, 2).value).isEqualTo('1');

        assertThat(board.getCell(1, 0).value).isEqualTo('1');
        assertThat(board.getCell(1, 1).value).isEqualTo('M');
        assertThat(board.getCell(1, 2).value).isEqualTo('1');

        assertThat(board.getCell(2, 0).value).isEqualTo('1');
        assertThat(board.getCell(2, 1).value).isEqualTo('1');
        assertThat(board.getCell(2, 2).value).isEqualTo('1');
    }
}