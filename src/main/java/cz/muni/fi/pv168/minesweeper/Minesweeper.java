package cz.muni.fi.pv168.minesweeper;

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

    static Board board;

    public static void main(String[] args) {
        System.out.println(LOGO);
        board = new Board(5, 10, 10);
        Scanner scanner = new Scanner(System.in);

        doPrintBoard();
        while (true) {
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
                }
            } else if (parts.length == 3 && "reveal".equalsIgnoreCase(parts[0]) || "r".equalsIgnoreCase(parts[0])) {
                if (!doReveal(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]))) {
                    handle_MINE(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                }
            } else {
                handle_ERROR("Invalid command: " + inputLine);
            }
            if (board.isCleared()) {
                doWon();
            }
        }
    }

    private static void doWon() {
        System.out.println("You won!");
        System.exit(0);
    }

    private static void doPrintBoard() {
        board.print();
    }

    private static boolean doReveal(int fst, int snd) {
        boolean result = board.reveal(fst, snd);
        doPrintBoard();
        return result;
    }

    private static void doDebug(Board board) {
        System.out.println("Debug output: \n");
        System.out.println(board.toString());
        System.out.println("");
    }

    private static void doExit() {
        System.out.println("You have called exit - defeat");
        System.exit(10);
    }

    private static void handle_MINE(int fst, int snd) {
        System.out.printf("Found mine @ coordinates [%d, %d]\n", fst, snd);
        System.exit(1);
    }

    private static void handle_ERROR(String err) {
        System.err.println("Error - " + err);
        System.exit(100);
    }
}
