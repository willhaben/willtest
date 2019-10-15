package at.willhaben.willtest.proxy.assertions;

import org.hamcrest.Matcher;

import java.util.Collection;

public class ProxyMatchers {

    public static Matcher<Collection<String>> requestAvailable(String regexMatcher) {
        return new RequestAvailableAssertion(regexMatcher);
    }
}
