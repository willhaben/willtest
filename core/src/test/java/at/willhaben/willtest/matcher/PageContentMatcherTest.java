package at.willhaben.willtest.matcher;

import at.willhaben.willtest.rule.PageContentException;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

public class PageContentMatcherTest {
    @Test
    public void exceptionHavingNoPageContentExceptionDoesNotMatch() {
        IllegalArgumentException exception = new IllegalArgumentException("foooo");
        exception.addSuppressed( new IOException("bar"));
        assertThat(exception, not(new PageContentMatcher(containsString("some text from pagecontent"))));
    }

    @Test
    public void exceptionHavingNoSuppressedExceptionsIsNotAProblemAndDoesNotMatch() {
        IllegalArgumentException exception = new IllegalArgumentException("foooo");
        assertThat(exception, not(new PageContentMatcher(containsString("some text from pagecontent"))));
    }

    @Test
    public void matchingPageContent() {
        IllegalArgumentException exception = new IllegalArgumentException("foooo");
        exception.addSuppressed(new PageContentException("page source"));
        assertThat(exception, new PageContentMatcher(containsString("page")));
    }

    @Test
    public void nonMatchingPageContent() {
        IllegalArgumentException exception = new IllegalArgumentException("foooo");
        exception.addSuppressed(new PageContentException("no matching text"));
        assertThat(exception, not(new PageContentMatcher(containsString("some text from pagecontent"))));
    }

    @Test
    public void multiplePageContentExceptionsOneMatches() {
        IllegalArgumentException exception = new IllegalArgumentException("foooo");
        exception.addSuppressed(new PageContentException("no matching text"));
        exception.addSuppressed(new PageContentException("some text"));
        assertThat(exception, new PageContentMatcher(containsString("some")));
    }

    @Test
    public void multiplePageContentExceptionsNoneMatches() {
        IllegalArgumentException exception = new IllegalArgumentException("foooo");
        exception.addSuppressed(new PageContentException("no matching text"));
        exception.addSuppressed(new PageContentException("another non matching text"));
        assertThat(exception, not(new PageContentMatcher(containsString("some text from pagecontent"))));
    }
}