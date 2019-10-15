package at.willhaben.willtest.proxy.assertions;

import org.hamcrest.TypeSafeMatcher;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class AbstractRequestAssertion<T> extends TypeSafeMatcher<T> {

    public <T> boolean oneOrMoreMatch(Collection<T> list, Predicate<T> filterMethod) {
        List<T> filteredEntries = list.stream()
                .filter(filterMethod)
                .collect(Collectors.toList());
        return filteredEntries.size() > 0;
    }
}
