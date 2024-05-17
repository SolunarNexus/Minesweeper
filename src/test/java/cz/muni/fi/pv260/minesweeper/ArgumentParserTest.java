package cz.muni.fi.pv260.minesweeper;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArgumentParserTest {

    @Test
    public void testEmptyArguments() {
        GameConfiguration configuration = ArgumentParser.parseGameConfiguration(new String[]{});
        assertThat(configuration.getRows()).isEqualTo(5);
        assertThat(configuration.getCols()).isEqualTo(10);
        assertThat(configuration.getMines()).isEqualTo(10);
        assertThat(configuration.getSeed()).isNull();
    }

    @Test
    public void testValidArguments() {
        GameConfiguration configuration = ArgumentParser.parseGameConfiguration(new String[]{"--rows", "1", "--cols", "2", "--mines", "3", "--seed", "4"});
        assertThat(configuration.getRows()).isEqualTo(1);
        assertThat(configuration.getCols()).isEqualTo(2);
        assertThat(configuration.getMines()).isEqualTo(3);
        assertThat(configuration.getSeed()).isEqualTo(4);
    }

    @Test
    public void testInvalidArgumentsNotNumber() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ArgumentParser.parseGameConfiguration(new String[]{"--rows", "ff"}));
        assertThat(exception.getMessage()).isEqualTo("Expected number attributed for '--rows' but got 'ff' instead");
    }

    @Test
    public void testInvalidArgumentsWrongNumber() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ArgumentParser.parseGameConfiguration(new String[]{"--rows"}));
        assertThat(exception.getMessage()).isEqualTo("Invalid arguments");
    }

    @Test
    public void testUnknownArguments() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> ArgumentParser.parseGameConfiguration(new String[]{"--unknown", "ff"}));
        assertThat(exception.getMessage()).isEqualTo("Unknown argument --unknown");
    }
}
