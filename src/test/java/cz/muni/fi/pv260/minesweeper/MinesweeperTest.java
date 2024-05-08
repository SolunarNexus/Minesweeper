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
    void doReveal() {
        doRevealCommand("""
                reveal 1 2
                exit
                """);
    }

    @Test
    void doRevealShort() {
        doRevealCommand("""
                r 1 2
                exit
                """);
    }

    private void doRevealCommand(String input) {
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

    @Test
    void doReveal_mine() {
        String input = """
                reveal 1 2
                """;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        when(board.reveal(1, 2)).thenReturn(false);

        minesweeper.runGame();

        verify(board).reveal(1, 2);
        verify(board, times(2)).print(out);
        verify(board).isCleared();
        verifyNoMoreInteractions(board);

        verify(wrapper).exit(1);


        verify(out).println(Minesweeper.LOGO);
        verify(out).print(">>> ");
        verify(out).printf("Found mine @ coordinates [%d, %d]\n", 1, 2);
        verify(out).println("\n*** You lost!***\n");
        verifyNoMoreInteractions(out, err);
    }

    @Test
    void doReveal_win() {
        String input = """
                reveal 1 2
                """;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        when(board.reveal(1, 2)).thenReturn(true);
        when(board.isCleared()).thenReturn(true);

        minesweeper.runGame();

        verify(board).reveal(1, 2);
        verify(board, times(2)).print(out);
        verify(board).isCleared();
        verifyNoMoreInteractions(board);

        verify(wrapper).exit(0);


        verify(out).println(Minesweeper.LOGO);
        verify(out).print(">>> ");
        verify(out).println("You won!");
        verifyNoMoreInteractions(out, err);
    }

    @Test
    void doExit() {
        doQuitCommand("exit");
    }

    @Test
    void doQuit() {
        doQuitCommand("quit");
    }

    private void doQuitCommand(String command) {
        System.setIn(new ByteArrayInputStream(command.getBytes()));

        minesweeper.runGame();

        verify(board).print(out);
        verify(out).println(Minesweeper.LOGO);
        verify(out).print(">>> ");
        verify(out).println("You have called exit - defeat");
        verifyNoMoreInteractions(board, out, err);

        verify(wrapper).exit(10);
    }

    @Test
    void doImport() {
        String input = """
                import NSwxMAowLDYKMCw3CjEsMgoxLDUKMSw2CjIsMAoyLDMKMiw3CjMsMAozLDIK
                exit
                """;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        minesweeper.runGame();

        verify(board).print(out);
        verify(out).println(Minesweeper.LOGO);
        verify(out, times(2)).print(">>> ");
        verify(out).println("Board imported!");
        verify(out).println("You have called exit - defeat");
        verifyNoMoreInteractions(board, err);

        verify(wrapper).exit(10);
    }

    @Test
    void doExport() {
        String input = """
                export
                exit
                """;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        when(board.exportBoard()).thenReturn("EXPORT");

        minesweeper.runGame();

        verify(board).print(out);
        verify(board).exportBoard();
        verify(out).println(Minesweeper.LOGO);
        verify(out, times(2)).print(">>> ");
        verify(out).println("EXPORT");
        verify(out).println("You have called exit - defeat");
        verifyNoMoreInteractions(board, out, err);

        verify(wrapper).exit(10);
    }

    @Test
    void doDebug() {
        doDebugCommand("""
                debug
                exit
                """);
    }

    @Test
    void doDebugShort() {
        doDebugCommand("""
                d
                exit
                """);
    }

    void doDebugCommand(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        minesweeper.runGame();

        verify(board).print(out);
        verify(out).println(Minesweeper.LOGO);
        verify(out, times(2)).print(">>> ");
        verify(out).println("Debug output: \n");
        verify(out).println(board.toString());
        verify(out).println("You have called exit - defeat");
        verifyNoMoreInteractions(board, out, err);

        verify(wrapper).exit(10);
    }

    @Test
    void errorCommand() {
        String input = """
                invalid valid command test
                """;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        minesweeper.runGame();

        verify(board).print(out);
        verify(out).println(Minesweeper.LOGO);
        verify(out).print(">>> ");
        verify(err).println("Error - Invalid command: invalid valid command test");
        verifyNoMoreInteractions(board, out, err);

        verify(wrapper).exit(100);
    }
}
