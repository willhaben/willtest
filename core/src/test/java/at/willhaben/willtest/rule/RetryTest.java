package at.willhaben.willtest.rule;

import at.willhaben.willtest.matcher.ExceptionMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.hamcrest.core.AnyOf.anyOf;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RetryTest {
    private static final int TRIALS = 4;
    @Mock
    private Statement statement;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @SuppressWarnings("unchecked")
    @Test
    public void tooManyTrialsThrowTheLastException() throws Throwable {
        IOException thrownAtTheEnd = new IOException("I am thrown at the end");
        doThrow(new IllegalArgumentException())
                .doThrow(new IllegalArgumentException())
                .doThrow(new IllegalArgumentException())
                .doThrow(thrownAtTheEnd)
                .when(statement).evaluate();
        try {
            new Retry(anyOf(
                    new ExceptionMatcher(IllegalArgumentException.class),
                    new ExceptionMatcher(IOException.class)), TRIALS)
                    .apply(statement, Description.TEST_MECHANISM).evaluate();
            fail("Expected " + thrownAtTheEnd.getClass().getName());
        }
        catch( IOException e ) {
            //expected
        }
        verify(statement, times(TRIALS)).evaluate();
    }

    @Test
    public void secondTrialSuccess() throws Throwable {
        doThrow(new IllegalArgumentException())
                .doNothing()
                .when(statement).evaluate();
        new Retry(new ExceptionMatcher<>(IllegalArgumentException.class), TRIALS)
                .apply(statement, Description.TEST_MECHANISM).evaluate();
        verify(statement, times(2)).evaluate();
    }

    @Test
    public void noError() throws Throwable {
        new Retry(new ExceptionMatcher<>(IllegalArgumentException.class), TRIALS)
                .apply(statement, Description.TEST_MECHANISM).evaluate();
        verify(statement, times(1)).evaluate();
    }
}