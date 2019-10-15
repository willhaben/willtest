package at.willhaben.rerun;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;

public class ParameterTest {


    private static Collection<String> data() {
        return Arrays.asList("One", "Two", "Three");
    }

    @MethodSource("data")
    @ParameterizedTest
    void testing(String value) {
        System.out.println(value);
        Assertions.assertEquals("Two", value);
    }
}
