package cz.muni.fi.pv260.minesweeper;

import cz.muni.fi.pv260.minesweeper.exceptions.InvalidCommandException;

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
            d[ebug] - prints debug output
            export - exports current board
            import <base64-encoded board> - imports new board
            exit | quit - exits the game
            """;

    Board board;
    SystemWrapper systemWrapper;
    Scanner scanner;
    boolean isGameFinished = false;

    public static void main(String[] args) {
        Minesweeper minesweeper = new Minesweeper(new SystemWrapper());
        minesweeper.runGame();
    }

    public Minesweeper(SystemWrapper systemWrapper) {
        this.systemWrapper = systemWrapper;
        System.out.println(LOGO);
        board = new Board(5, 10, 10);
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

            try {
                doOneStep(parts);
            } catch (InvalidCommandException e) {
                handleInvalidCommand(e, inputLine);
            }
        }
    }

    private void doOneStep(String[] parts) throws InvalidCommandException {
        if (parts.length == 1) {
            if ("exit".equalsIgnoreCase(parts[0])) {
                doExit();
            } else if ("quit".equalsIgnoreCase(parts[0])) {
                doExit();
            } else if ("debug".equalsIgnoreCase(parts[0]) || "d".equalsIgnoreCase(parts[0])) {
                doDebug(board);
            } else if ("export".equalsIgnoreCase(parts[0])) {
                doExport();
            } else {
                throw new InvalidCommandException("Unknown command");
            }
        } else if (parts.length == 2) {
            if ("import".equalsIgnoreCase(parts[0])) {
                doImport(parts[1]);
            } else {
                throw new InvalidCommandException("Unknown command");
            }
        } else if (parts.length == 3 && "reveal".equalsIgnoreCase(parts[0]) || "r".equalsIgnoreCase(parts[0])) {
            try {
                int row = Integer.parseInt(parts[1]);
                int column = Integer.parseInt(parts[2]);
                if (!doReveal(row, column)) {
                    handleMine(row, column);
                }
            } catch (NumberFormatException e) {
                throw new InvalidCommandException("Expected numbers for row and column");
            } catch (IndexOutOfBoundsException e) {
                throw new InvalidCommandException("Row or column out of bounds");
            }
            if (board.isCleared()) {
                doWon();
            }
        } else {
            throw new InvalidCommandException("Unknown command");
        }
    }

    private void doExport() {
        System.out.println(board.exportBoard());
    }

    private void doImport(String content) {
        System.out.println("Are you sure you want to import a replace current board? (Y/n)");
        if (scanner.nextLine().equals("Y")) {
            board = Board.importBoard(content);
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

    private boolean doReveal(int row, int column) {
        boolean result = board.reveal(row, column);
        doPrintBoard();
        return result;
    }

    private void doDebug(Board board) {
        System.out.println("Debug output: \n");
        System.out.println(board.toString());
    }

    private void doExit() {
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

    private void handleInvalidCommand(InvalidCommandException exception, String command) {
        System.out.println("Invalid command: " + command + " (" + exception.getMessage() + ")");
        doPrintUsage();
    }

    private void doPrintUsage() {
        System.out.println(USAGE);
    }
}
