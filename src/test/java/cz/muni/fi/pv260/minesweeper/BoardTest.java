package cz.muni.fi.pv260.minesweeper;

import org.junit.jupiter.api.Test;

final class BoardTest {

    @Test
    void loadSimpleBoard() {
        String board = """
                MXM
                XXX
                XXM
                """;

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
    void loadSimpleClearedBoard() {
        String board = """
                MM.
                ..M
                ..M
                """;

        try (var softly = new BoardSoftAssertions(board)) {
            softly.assertRows(3);
            softly.assertColumns(3);
            softly.assertMines(4);
            softly.assertCleared(true);

            softly.assertCell(0, 0, 'M');
            softly.assertCell(0, 1, 'M');
            softly.assertRevealedCell(0, 2, '2');

            softly.assertRevealedCell(1, 0, '2');
            softly.assertRevealedCell(1, 1, '4');
            softly.assertCell(1, 2, 'M');

            softly.assertRevealedCell(2, 0, '0');
            softly.assertRevealedCell(2, 1, '2');
            softly.assertCell(2, 2, 'M');
        }
    }

    @Test
    void loadBoardWithAllPossibleCellValues() {
        String board = """
                MMMMMMM
                MXMXMXM
                MMMXMXX
                MXXMXXX
                """;

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

    @Test
    void revealWithFloodFillBasic() {
        String board = """
                XXXXXXXXX
                XXXXXXXXX
                XXXXMXXXX
                XXXXXXXXX
                """;

        try (var softly = new BoardSoftAssertions(board)) {
            softly.reveal(0, 0);

            String expectedBoard = """
                    .........
                    .........
                    ....X....
                    ....X....
                    """;
            softly.assertBoardRevealed(expectedBoard);
        }
    }


    @Test
    void revealWithFloodFillBasicOneRevealed() {
        String board = """
                XXXXX
                XXMXX
                """;

        try (var softly = new BoardSoftAssertions(board)) {
            softly.reveal(0, 2);

            String expectedBoard = """
                    XX.XX
                    XXXXX
                        """;
            softly.assertBoardRevealed(expectedBoard);
        }
    }

    @Test
    void revealWithFloodFillDoNotRevealBorders() {
        String board = """
                XXXXXXXXX
                XXXXXXXXX
                XMXXXXXXX
                XXXXXXXXX
                """;

        try (var softly = new BoardSoftAssertions(board)) {
            softly.reveal(2, 4);

            String expectedBoard = """
                    .........
                    .........
                    XX.......
                    XX.......
                    """;
            softly.assertBoardRevealed(expectedBoard);
        }
    }

    @Test
    void revealWithFloodFillCorner() {
        String board = """
                XXXXXXXXX
                XXXXXXXXX
                XMXXXXXXX
                XXXXXXXXX
                """;

        try (var softly = new BoardSoftAssertions(board)) {
            softly.reveal(0, 0);

            String expectedBoard = """
                    .........
                    .........
                    XX.......
                    XX.......
                    """;
            softly.assertBoardRevealed(expectedBoard);
        }
    }

    @Test
    void revealWithFloodFillMoreMines() {
        String board = """
                XXXXMXXXX
                XXXXXMXXX
                MXXXXXXXX
                XXMXXXMXX
                """;

        try (var softly = new BoardSoftAssertions(board)) {
            softly.reveal(0, 0);

            String expectedBoard = """
                    ....XXXXX
                    ....XXXXX
                    X...XXXXX
                    XXXXXXXXX
                    """;
            softly.assertBoardRevealed(expectedBoard);
        }
    }

    @Test
    void createBoardWithSeed_shouldGenerateTheSameGame() {
        Board board = new Board(5, 5, 5, 1234L, 0, 0);

        try (var softly = new BoardSoftAssertions(board)) {
            board.reveal(0, 0);
            softly.assertBoardValues(
                    """
                            001M1
                            01221
                            12M21
                            M22M2
                            1112M
                            """
            );
            softly.assertBoardRevealed(
                    """
                            ...XX
                            ...XX
                            ..XXX
                            XXXXX
                            XXXXX
                            """
            );
        }
    }
}
