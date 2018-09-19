package at.willhaben.willtest.misc.pages.find;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.Annotations;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.lang.reflect.Field;
import java.util.List;

public class CustomElementLocator implements ElementLocator {

    private SearchContext searchContext;
    private Annotations annotations;
    private WebElement cachedElement;
    private List<WebElement> cachedElementList;
    private CustomUiComponentFactory factory;
    private Field field;

    public CustomElementLocator(SearchContext searchContext, Field field, CustomUiComponentFactory factory) {
        this(searchContext, new CustomAnnotation(field));
        this.factory = factory;
        this.field = field;
    }

    public CustomElementLocator(SearchContext searchContext, Annotations annotations) {
        this.searchContext = searchContext;
        this.annotations = annotations;
    }

    @Override
    public WebElement findElement() {
        return searchContext.findElement(factory.buildBy(factory.getAnnotation(field)));
    }

    @Override
    public List<WebElement> findElements() {
        return searchContext.findElements(annotations.buildBy());
    }
}
