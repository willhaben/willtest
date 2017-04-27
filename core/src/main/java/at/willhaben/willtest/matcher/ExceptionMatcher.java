package at.willhaben.willtest.matcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Optional;

/**
 * Matches a given exception type with an optional additional message matcher.
 * @param <T> expected exception type
 */
public class ExceptionMatcher<T extends Throwable> extends TypeSafeMatcher<T>{
    private final Matcher<String> messageMatcher;
    private final Class<T> expectedType;

    /**
     * Will match {@link Throwable} instances of the given type. See also {@link TypeSafeMatcher} for details.
     * @param expectedType expected child class of {@link Throwable}
     */
    public ExceptionMatcher(Class<T> expectedType) {
        this(expectedType,null);
    }

    /**
     * Will match {@link Throwable} instances of the given type and if its message matches the given String {@link Matcher}.
     * See also {@link TypeSafeMatcher} for details.
     * @param expectedType expected child class of {@link Throwable}
     * @param messageMatcher matcher for exception message
     */
    public ExceptionMatcher(Class<T> expectedType, Matcher<String> messageMatcher) {
        super(expectedType);
        this.expectedType = expectedType;
        this.messageMatcher = messageMatcher;
    }

    @Override
    protected boolean matchesSafely(T item) {
        return Optional
                .ofNullable(messageMatcher)
                .map(matcher -> matcher.matches(item.getMessage()))
                .orElse(true);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText( "Expected a " + expectedType.getClass().getName() + " instance" );
        if ( messageMatcher != null ) {
            description.appendText( " having a message, which matches: " );
            messageMatcher.describeTo(description);
        }
    }
}
