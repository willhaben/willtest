package at.willhaben.willtest.misc.pages.find;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.support.pagefactory.DefaultElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class WhElementLocatorFactory implements ElementLocatorFactory {

    private SearchContext searchContext;
    private List<CustomUiComponentFactory> componentFactories = new ArrayList<>();

    public WhElementLocatorFactory(SearchContext searchContext) {
        this.searchContext = searchContext;
    }

    public WhElementLocatorFactory addComponent(CustomUiComponentFactory factory) {
        componentFactories.add(factory);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ElementLocator createLocator(Field field) {
        System.out.println("Create locator for field: '" + field.getName() + "'.");
        Optional<CustomUiComponentFactory> customUiComponentFactory = componentFactories.stream()
                .filter(factory -> Objects.nonNull(field.getAnnotation(factory.customAnnotation())))
                .findFirst();
        if (customUiComponentFactory.isPresent()) {
            System.out.println("Use custom element locator for field: '" + field.getName() + "'.");
            return new CustomElementLocator(searchContext, field, customUiComponentFactory.get());
        } else {
            return new DefaultElementLocator(searchContext, field);
        }
    }
}
