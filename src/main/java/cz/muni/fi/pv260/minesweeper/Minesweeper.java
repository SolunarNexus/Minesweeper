package cz.muni.fi.pv260.minesweeper;

import java.util.Optional;
import java.util.Scanner;

public final class Minesweeper {

    public static final String LOGO = """
            ███╗░░░███╗██╗███╗░░██╗███████╗░██████╗░██╗░░░░░░░██╗███████╗███████╗██████╗░███████╗██████╗░
            ████╗░████║██║████╗░██║██╔════╝██╔════╝░██║░░██╗░░██║██╔════╝██╔════╝██╔══██╗██╔════╝██╔══██╗
            ██╔████╔██║██║██╔██╗██║█████╗░░╚█████╗░░╚██╗████╗██╔╝█████╗░░█████╗░░██████╔╝█████╗░░██████╔╝
            ██║╚██╔╝██║██║██║╚████║██╔══╝░░░╚═══██╗░░████╔═████║░██╔══╝░░██╔══╝░░██╔═══╝░██╔══╝░░██╔══██╗
            ██║░╚═╝░██║██║██║░╚███║███████╗██████╔╝░░╚██╔╝░╚██╔╝░███████╗███████╗██║░░░░░███████╗██║░░██║
            ╚═╝░░░░░╚═╝╚═╝╚═╝░░╚══╝╚══════╝╚═════╝░░░░╚═╝░░░╚═╝░░╚══════╝╚══════╝╚═╝░░░░░╚══════╝╚═╝░░╚═╝
            """;

    public static final String USAGE = """
            Supported commands:
            r[eveal] <row> <column> - reveals cell
            f[lag] <row> <column> - flags cell
            d[ebug] - prints debug output
            export - exports current board
            import <base64-encoded board> - imports new board
            exit | quit - exits the game
            """;

    Board board;
    SystemWrapper systemWrapper;
    Scanner scanner;
    boolean isGameFinished = false;
    boolean isBoardInitialized = false;
    GameConfiguration configuration;

    public static void main(String[] args) {
        var minesweeper = new Minesweeper(new SystemWrapper(), args);
        minesweeper.runGame();
    }

    public Minesweeper(SystemWrapper systemWrapper, String[] args) {
        this.systemWrapper = systemWrapper;
        configuration = ArgumentParser.parseGameConfiguration(args);
        String errorMessage = validateConfiguration();
        if (errorMessage != null) {
            System.err.println(errorMessage);
            systemWrapper.exit(100);
            return;
        }
        this.board = new Board(configuration.getRows(), configuration.getCols(), configuration.getMines(), configuration.getSeed(), 0, 0);
        System.out.println(LOGO);
    }

    private String validateConfiguration() {
        var rows = configuration.getRows();
        var cols = configuration.getCols();
        var mines = configuration.getMines();
        if (rows * cols <= mines) {
            return "Invalid configuration: number of mines is greater or equal to number of cells";
        }

        if (rows < 3 || cols < 3 || rows > 99 || cols > 99 || mines < 1) {
            return "Invalid configuration: rows and cols must be between 3 and 99 and mines must be greater than 0";
        }
        return null;
    }

    private int[] parseCoordinates(String rowPart, String colPart) {
        try {
            int row = Integer.parseInt(rowPart);
            int column = Integer.parseInt(colPart);

            if (!board.isInBounds(row, column)) {
                handleInvalidCommand("Row or column out of bounds");
                return null;
            }

            return new int[]{row, column};
        } catch (NumberFormatException e) {
            handleInvalidCommand("Expected numbers for row and column");
            return null;
        }
    }

    void runGame() {
        scanner = new Scanner(System.in);
        doPrintBoard();
        while (!isGameFinished) {
            System.out.print(">>> ");
            var inputLine = scanner.nextLine();
            if (inputLine.isBlank())
                continue;

            var parts = inputLine.strip().split("\s");
            if (parts.length == 0) {
                continue;
            }

            handleCommand(parts);
        }
    }

    private void handleCommand(String[] parts) {
        switch (parts[0].toLowerCase()) {
            case "exit", "quit":
                handleExitCommand();
                break;
            case "debug", "d":
                handleDebugCommand();
                break;
            case "export":
                handleExportCommand();
                break;
            case "import":
                handleImportCommand(parts);
                break;
            case "reveal", "r":
                handleRevealCommand(parts);
                break;
            case "flag", "f":
                handleFlagCommand(parts);
                break;
            default:
                handleInvalidCommand("Unknown command");
        }
    }

    private void handleFlagCommand(String[] parts) {
        if (parts.length != 3) {
            handleInvalidCommand("Expected row and column coordinates");
            return;
        }

        var coordinates = parseCoordinates(parts[1], parts[2]);

        if (coordinates != null) {
            if (!isBoardInitialized) {
                board = new Board(configuration.getRows(), configuration.getCols(), configuration.getMines(), configuration.getSeed(), coordinates[0], coordinates[1]);
                isBoardInitialized = true;
            }

            board.flag(coordinates[0], coordinates[1]);
            doPrintBoard();
        }
    }

    private void handleExportCommand() {
        if (!isBoardInitialized) {
            System.out.println("Board is not initialized.");
            return;
        }
        System.out.println(board.exportBoard());
    }

    private void handleImportCommand(String[] parts) {
        if (parts.length != 2) {
            handleInvalidCommand("Expected base64-encoded board");
            return;
        }
        System.out.println("Are you sure you want to import a replace current board? (Y/n)");
        if (scanner.nextLine().equals("Y")) {
            Optional<Board> importedBoard = Board.importBoard(parts[1]);
            if (importedBoard.isEmpty()) {
                System.out.println("Invalid board import");
                return;
            }
            board = importedBoard.get();
            System.out.println("Board imported!");
            doPrintBoard();
        } else {
            System.out.println("Board import discarded");
        }
    }

    private void doWon() {
        System.out.println("You won!");
        isGameFinished = true;
        systemWrapper.exit(0);
    }

    private void doPrintBoard() {
        board.print(System.out);
    }

    private void handleRevealCommand(String[] parts) {
        if (parts.length != 3) {
            handleInvalidCommand("Expected row and column coordinates");
            return;
        }

        var coordinates = parseCoordinates(parts[1], parts[2]);

        if (coordinates != null) {
            var row = coordinates[0];
            var column = coordinates[1];

            if (!isBoardInitialized) {
                board = new Board(configuration.getRows(), configuration.getCols(), configuration.getMines(), configuration.getSeed(), row, column);
                isBoardInitialized = true;
            }

            boolean result = board.reveal(row, column);
            doPrintBoard();

            if (!result) {
                handleMine(row, column);
            }

            if (board.isCleared()) {
                doWon();
            }
        }
    }

    private void handleDebugCommand() {
        System.out.println("Debug output: \n");
        System.out.println(board.toString());
    }

    private void handleExitCommand() {
        System.out.println("You have called exit - defeat");
        isGameFinished = true;
        systemWrapper.exit(10);
    }

    private void handleMine(int row, int column) {
        System.out.printf("Found mine @ coordinates [%d, %d]\n", row, column);
        System.out.println("\n*** You lost!***\n");
        isGameFinished = true;
        systemWrapper.exit(1);
    }

    private void handleInvalidCommand(String message) {
        System.out.println("Invalid command: " + message);
        doPrintUsage();
    }

    private void doPrintUsage() {
        System.out.println(USAGE);
    }
}
