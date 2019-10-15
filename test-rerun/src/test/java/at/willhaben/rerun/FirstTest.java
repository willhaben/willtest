package at.willhaben.rerun;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FirstTest {

    @Test
    void testOne() {
        System.out.println("FirstTest#testOne");
    }

    @Test
    void testTwo() {
        System.out.println("FirstTest#testTwo");
        throw new NullPointerException();
    }

    @Test
    void testThree() {
        System.out.println("FirstTest#testThree");
        Assertions.assertTrue(false);
    }
}
