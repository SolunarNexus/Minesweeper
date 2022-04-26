package cz.muni.fi.pv260.minesweeper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

final class TestUtils {
    private TestUtils() {
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
    static Board loadFromString(String raw) throws ParseException {
        var lines = raw.split("\n");
        var rows = lines.length;
        var cols = lines[0].trim().length();

        List<BoardCell> cells = new ArrayList<>(rows * cols);
        int i = 0;

        for (var line : lines) {
            for (char ch : line.toUpperCase().toCharArray()) {
                var cell = new BoardCell();
                cells.add(cell);

                switch (ch) {
                    case 'M' -> cell.value = 'M';
                    case '.' -> cell.isRevealed = true;
                    case 'X' -> cell.isRevealed = false;
                    case 'W', 'F' -> throw new UnsupportedOperationException("Not implemented yet");
                    default -> throw new ParseException("Unsupported character: " + ch, i);
                }

                i++;
            }
        }

        return new Board(rows, cols, cells);
    }
}
