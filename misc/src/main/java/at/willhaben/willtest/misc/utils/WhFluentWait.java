package at.willhaben.willtest.misc.utils;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.Clock;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Sleeper;

import java.util.Objects;
import java.util.function.Function;

public class WhFluentWait<T> extends FluentWait<T> {

    public WhFluentWait(T input) {
        super(input);
    }

    public WhFluentWait(T input, Clock clock, Sleeper sleeper) {
        super(input, clock, sleeper);
    }

    /**
     * Waits until the given condition is met. Throws an {@link AssertionError} instead of a {@link TimeoutException}
     * when the condition fails because the error is known.
     * @param errorMessage concrete error message describing the failure
     * @param isTrue the parameter to pass to the {@link ExpectedCondition}
     * @param <V> The function's expected return type.
     * @return The function's return value if the function returned something different
     *         from null or false before the timeout expired.
     * @throws AssertionError if the timeout expires
     */
    public <V> V until(String errorMessage, Function<? super T, V> isTrue) {
        try {
            return super.until(isTrue);
        } catch (TimeoutException e) {
            if (Objects.nonNull(errorMessage) && !"".equals(errorMessage)) {
                throw new AssertionError(errorMessage, e);
            } else {
                throw e;
            }
        }
    }

    @Override
    public <V> V until(Function<? super T, V> isTrue) {
        return super.until(isTrue);
    }
}
