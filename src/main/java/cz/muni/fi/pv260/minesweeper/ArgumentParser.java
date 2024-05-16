package cz.muni.fi.pv260.minesweeper;

public class ArgumentParser {

    public static GameConfiguration parseGameConfiguration(String args[]) {
        GameConfiguration configuration = new GameConfiguration();
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("Invalid arguments");
        }
        for (int i = 0; i < args.length; i += 2) {
            try {
                switch (args[i]) {
                    case "--rows":
                        configuration.setRows(Integer.parseInt(args[i + 1]));
                        break;
                    case "--cols":
                        configuration.setCols(Integer.parseInt(args[i + 1]));
                        break;
                    case "--mines":
                        configuration.setMines(Integer.parseInt(args[i + 1]));
                        break;
                    case "--seed":
                        configuration.setSeed(Long.parseLong(args[i + 1]));
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown argument " + args[i]);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Expected number attributed for '" + args[i] + "' but got '" + args[i + 1] + "' instead");
            }
        }
        return configuration;
    }
}
