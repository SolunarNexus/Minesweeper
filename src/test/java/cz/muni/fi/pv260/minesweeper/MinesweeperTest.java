package cz.muni.fi.pv260.minesweeper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
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
        minesweeper = new Minesweeper(wrapper, new String[]{});
        minesweeper.board = board;
    }

    @ParameterizedTest
    @ValueSource(strings = {"reveal 1 2", "r 1 2"})
    void testRevealCommand(String command) {
        String input = command + "\nexit";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        when(board.reveal(1, 2)).thenReturn(true);
        when(board.isInBounds(1, 2)).thenReturn(true);
        minesweeper.isBoardInitialized = true;

        minesweeper.runGame();

        verify(board).isInBounds(1, 2);
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
    void testRevealCommandNotInitialized() {
        String input = """
                reveal 1 2
                exit
                """;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        when(board.reveal(1, 2)).thenReturn(true);
        when(board.isInBounds(1, 2)).thenReturn(true);

        minesweeper.runGame();

        assertThat(minesweeper.isBoardInitialized).isTrue();
    }

    @Test
    void doReveal_mine() {
        String input = """
                reveal 1 2
                """;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        when(board.reveal(1, 2)).thenReturn(false);
        when(board.isInBounds(1, 2)).thenReturn(true);
        minesweeper.isBoardInitialized = true;

        minesweeper.runGame();

        verify(board).isInBounds(1, 2);
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
        when(board.isInBounds(1, 2)).thenReturn(true);
        minesweeper.isBoardInitialized = true;

        minesweeper.runGame();

        verify(board).isInBounds(1, 2);
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

    @ParameterizedTest
    @ValueSource(strings = {"quit", "exit"})
    void testQuit(String command) {
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
    void doImportConfirm() {
        String input = """
                import NSwxMAowLDYKMCw3CjEsMgoxLDUKMSw2CjIsMAoyLDMKMiw3CjMsMAozLDIK
                Y
                exit
                """;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        minesweeper.runGame();

        verify(board).print(out);
        verify(out).println(Minesweeper.LOGO);
        verify(out, times(2)).print(">>> ");
        verify(out).println("Are you sure you want to import a replace current board? (Y/n)");
        verify(out).println("Board imported!");
        verify(out).println("You have called exit - defeat");
        verifyNoMoreInteractions(board, err);

        verify(wrapper).exit(10);
    }

    @Test
    void doImportDiscard() {
        String input = """
                import NSwxMAowLDYKMCw3CjEsMgoxLDUKMSw2CjIsMAoyLDMKMiw3CjMsMAozLDIK
                n
                exit
                """;
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        minesweeper.runGame();

        verify(board).print(out);
        verify(out).println(Minesweeper.LOGO);
        verify(out, times(2)).print(">>> ");
        verify(out).println("Are you sure you want to import a replace current board? (Y/n)");
        verify(out).println("Board import discarded");
        verify(out).println("You have called exit - defeat");
        verifyNoMoreInteractions(board, err);

        verify(wrapper).exit(10);
    }

    @Test
    void doExportInitialized() {
        String input = """
                export
                exit
                """;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        when(board.exportBoard()).thenReturn("EXPORT");
        minesweeper.isBoardInitialized = true;

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
    void doExportNotInitialized() {
        String input = """
                export
                exit
                """;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        when(board.exportBoard()).thenReturn("EXPORT");
        minesweeper.isBoardInitialized = false;

        minesweeper.runGame();

        verify(board).print(out);
        verify(out).println(Minesweeper.LOGO);
        verify(out, times(2)).print(">>> ");
        verify(out).println("Board is not initialized.");
        verify(out).println("You have called exit - defeat");
        verifyNoMoreInteractions(board, out, err);

        verify(wrapper).exit(10);
    }

    @ParameterizedTest
    @ValueSource(strings = {"debug", "d"})
    void testDebug(String command) {
        String input = command + "\nexit";
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
    void errorCommandReveal_outOfBounds() {
        String input = """
                reveal 1 100
                exit
                """;
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        when(board.isInBounds(1, 2)).thenReturn(false);

        minesweeper.runGame();

        verify(board).print(out);
        verify(board).isInBounds(1, 100);
        verify(out).println(Minesweeper.LOGO);
        verify(out, times(2)).print(">>> ");
        verify(out).println("Invalid command: Row or column out of bounds");
        verify(out).println(Minesweeper.USAGE);
        verify(out).println("You have called exit - defeat");
        verifyNoMoreInteractions(board, out, err);

        verify(wrapper).exit(10);
    }

    @ParameterizedTest
    @CsvSource({
            "invalid,Unknown command",
            "invalid command,Unknown command",
            "long invalid command,Unknown command",
            "super long invalid command,Unknown command",
            "r x 1,Expected numbers for row and column",
            "reveal 1 1 command,Expected row and column coordinates",
            "import xx command,Expected base64-encoded board",
    })
    void errorCommand(String command, String messageExpected) {
        System.setIn(new ByteArrayInputStream((command + "\nexit").getBytes()));

        minesweeper.runGame();

        verify(board).print(out);
        verify(out).println(Minesweeper.LOGO);
        verify(out, times(2)).print(">>> ");
        verify(out).println("Invalid command: " + messageExpected);
        verify(out).println(Minesweeper.USAGE);
        verify(out).println("You have called exit - defeat");
        verifyNoMoreInteractions(board, out, err);

        verify(wrapper).exit(10);
    }
}
