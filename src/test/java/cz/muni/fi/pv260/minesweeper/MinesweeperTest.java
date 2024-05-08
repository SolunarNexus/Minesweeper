package cz.muni.fi.pv260.minesweeper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.PrintStream;

import static org.mockito.Mockito.*;

class MinesweeperTest {

    private PrintStream out;
    private PrintStream err;
    private SystemWrapper wrapper;
    private Board board;
    private Minesweeper minesweeper;

    @BeforeEach
    void setUp() {
        out = mock(PrintStream.class);
        err = mock(PrintStream.class);
        wrapper = mock(SystemWrapper.class);
        board = mock(Board.class);
        System.setOut(out);
        System.setErr(err);
        minesweeper = new Minesweeper(wrapper);
        minesweeper.board = board;
    }

    @Test
    void doReveal_notMine() {
        String input = """
                reveal 1 2
                exit
                """;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        when(board.reveal(1, 2)).thenReturn(true);

        minesweeper.runGame();

        verify(board).reveal(1, 2);
        verify(board, times(2)).print(out);
        verify(board).isCleared();
        verifyNoMoreInteractions(board);

        verify(wrapper).exit(10);


        verify(out).println(Minesweeper.LOGO);
        verify(out, times(2)).print(">>> ");
        verify(out).println("You have called exit - defeat");
        verifyNoMoreInteractions(out, err);
    }

}
