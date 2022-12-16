package tests.homework3;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

/*
* These tests check length variable (String).
* */
public class TestShortPhrase {

    @ParameterizedTest
    @ValueSource(strings = {"", "123", "15 symbols: abc", "More than fifteen symbols"})
    public void testShortPhrase(String var) {
        assertTrue(var.length() > 15, "Length of variable '" + var + "' has less or equal 15 symbols");
    }
}
