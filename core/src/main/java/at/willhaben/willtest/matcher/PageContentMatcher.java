package at.willhaben.willtest.matcher;

import at.willhaben.willtest.rule.PageContentException;
import com.google.common.collect.ImmutableList;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * Matches a {@link Throwable} if it has any suppressed {@link PageContentException}, which contains a page content
 * (see {@link PageContentException#getPageContent()}, which matches the {@link Matcher} passed in as constructor
 * argument. It can be used together with {@link at.willhaben.willtest.rule.Retry} and
 * {@link at.willhaben.willtest.rule.PageSource} to retry on a specific page content.
 * Example: a "Gateway Timeout" page sent by Apache could mean that we want to retry a test.
 */
@Deprecated
public class PageContentMatcher extends TypeSafeMatcher<Throwable> {
    private final Matcher<String> pageContentMatcher;

    /**
     * @param pageContentMatcher this matcher will be used to match the page content as string
     */
    public PageContentMatcher(Matcher<String> pageContentMatcher) {
        this.pageContentMatcher = pageContentMatcher;
    }

    @Override
    protected boolean matchesSafely(Throwable testFailure) {
        return getPageContentExceptions(testFailure)
                .stream()
                .map(PageContentException::getPageContent)
                .anyMatch(pageContentMatcher::matches);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText( String.format( "Matches any error having an %s suppressed exception having a " +
                "pageContent matching the following matcher:", PageContentException.class.getName() ) );
        pageContentMatcher.describeTo(description);
    }

    private List<PageContentException> getPageContentExceptions(Throwable testFailure) {
        return Optional
                .ofNullable(testFailure.getSuppressed())
                .map(suppressedThrowables -> Arrays
                        .stream(suppressedThrowables)
                        .filter(suppressed ->
                                PageContentException.class.isAssignableFrom( suppressed.getClass() ) )
                        .map(suppressed -> (PageContentException) suppressed)
                        .collect(toList()))
                .orElse(ImmutableList.of());
    }
}
