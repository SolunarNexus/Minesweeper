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

    Board board;
    SystemWrapper systemWrapper;
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
        Scanner scanner = new Scanner(System.in);
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

            if (parts.length == 1) {
                if ("exit".equalsIgnoreCase(parts[0])) {
                    doExit();
                } else if ("quit".equalsIgnoreCase(parts[0])) {
                    doExit();
                } else if ("debug".equalsIgnoreCase(parts[0]) || "d".equalsIgnoreCase(parts[0])) {
                    doDebug(board);
                } else if ("export".equalsIgnoreCase(parts[0])) {
                    doExport();
                }
            } else if (parts.length == 2) {
                if ("import".equalsIgnoreCase(parts[0])) {
                    doImport(parts[1]);
                }
            } else if (parts.length == 3 && "reveal".equalsIgnoreCase(parts[0]) || "r".equalsIgnoreCase(parts[0])) {
                if (!doReveal(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]))) {
                    handleMine(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                }
                if (board.isCleared()) {
                    doWon();
                }
            } else {
                handleError("Invalid command: " + inputLine);
            }
        }
    }

    private void doExport() {
        System.out.println(board.exportBoard());
    }

    private void doImport(String content) {
        board = Board.importBoard(content);
        System.out.println("Board imported!");
        doPrintBoard();
    }

    private void doWon() {
        System.out.println("You won!");
        isGameFinished = true;
        systemWrapper.exit(0);
    }

    private void doPrintBoard() {
        board.print(System.out);
    }

    private boolean doReveal(int fst, int snd) {
        boolean result = board.reveal(fst, snd);
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

    private void handleMine(int fst, int snd) {
        System.out.printf("Found mine @ coordinates [%d, %d]\n", fst, snd);
        System.out.println("\n*** You lost!***\n");
        isGameFinished = true;
        systemWrapper.exit(1);
    }

    private void handleError(String err) {
        System.err.println("Error - " + err);
        isGameFinished = true;
        systemWrapper.exit(100);
    }
}
