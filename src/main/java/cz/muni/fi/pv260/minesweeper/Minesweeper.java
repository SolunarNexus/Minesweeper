package cz.muni.fi.pv260.minesweeper;

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
        var minesweeper = new Minesweeper(new SystemWrapper(), args);
        minesweeper.runGame();
    }

    public Minesweeper(SystemWrapper systemWrapper, String[] args) {
        this.systemWrapper = systemWrapper;
        GameConfiguration configuration = ArgumentParser.parseGameConfiguration(args);
        this.board = new Board(configuration.getRows(), configuration.getCols(), configuration.getMines(), configuration.getSeed());
        System.out.println(LOGO);
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
                doExit();
                break;
            case "debug", "d":
                doDebug(board);
                break;
            case "export":
                doExport();
                break;
            case "import":
                if (parts.length == 2) {
                    doImport(parts[1]);
                } else {
                    handleInvalidCommand("Expected base64-encoded board");
                }
                break;
            case "reveal", "r":
                if (parts.length == 3) {
                    try {
                        int row = Integer.parseInt(parts[1]);
                        int column = Integer.parseInt(parts[2]);
                        doReveal(row, column);
                    } catch (NumberFormatException e) {
                        handleInvalidCommand("Expected numbers for row and column");
                    }
                } else {
                    handleInvalidCommand("Unknown command");
                }
                break;
            default:
                handleInvalidCommand("Unknown command");
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

    private void doReveal(int row, int column) {
        if (!board.isInBounds(row, column)) {
            handleInvalidCommand("Row or column out of bounds");
            return;
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

    private void handleInvalidCommand(String message) {
        System.out.println("Invalid command: " + message);
        doPrintUsage();
    }

    private void doPrintUsage() {
        System.out.println(USAGE);
    }
}
