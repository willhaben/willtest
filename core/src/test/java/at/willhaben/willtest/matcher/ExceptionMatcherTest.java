package at.willhaben.willtest.matcher;

import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

public class ExceptionMatcherTest {
    @Test
    public void matchesType() {
        assertThat( new IllegalArgumentException(), new ExceptionMatcher<>(IllegalArgumentException.class));
    }

    @Test
    public void matchesSubclasses() {
        assertThat( new IllegalArgumentException(), new ExceptionMatcher<>(RuntimeException.class));
    }

    @Test
    public void typeMatchesButWrongText() {
        assertThat(
                new IllegalArgumentException("fooo"),
                not(new ExceptionMatcher<>(IllegalArgumentException.class,is("bar"))));
    }

    @Test
    public void typeMatchesAndGoodText() {
        assertThat(
                new IllegalArgumentException("fooo"),
                new ExceptionMatcher<>(IllegalArgumentException.class,containsString("oo")));
    }

    @Test
    public void wrongTypeGoodText() {
        //noinspection unchecked
        assertThat(
                new IllegalArgumentException("fooo"),
                not((ExceptionMatcher)  new ExceptionMatcher<>(IOException.class,containsString("oo"))));
    }
}