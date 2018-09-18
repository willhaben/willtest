package at.willhaben.willtest.misc.pages.find;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.support.pagefactory.DefaultElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

import java.lang.reflect.Field;
import java.util.Objects;

public class WhElementLocatorFactory implements ElementLocatorFactory {

    private SearchContext searchContext;

    public WhElementLocatorFactory(SearchContext searchContext) {
        this.searchContext = searchContext;
    }

    @Override
    public ElementLocator createLocator(Field field) {
        System.out.println("Field name: " + field.getName());
        FindWh annotation = field.getAnnotation(FindWh.class);
        if (Objects.isNull(annotation)) {
            System.out.println("FindWh annotation is null.");
            new DefaultElementLocator(searchContext, field);
        } else {
            System.out.println("FindWh annotation is not null.");
            new CustomElementLocator(searchContext, field);
        }
        return null;
    }
}
