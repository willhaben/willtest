package at.willhaben.willtest.proxy.assertions;

import org.hamcrest.Description;

import java.util.Collection;

public class RequestAvailableAssertion extends AbstractRequestAssertion<Collection<String>> {

    private String regexMatch;

    public RequestAvailableAssertion(String regexMatch) {
        this.regexMatch = regexMatch;
    }

    @Override
    protected boolean matchesSafely(Collection<String> urls) {
        return oneOrMoreMatch(urls, url -> url.matches(regexMatch));
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("url which matches the given regex ").appendValue(regexMatch);
    }
}
