package at.willhaben.willtest.misc.pages.find;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.Annotations;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

public class CustomElementLocator implements ElementLocator {

    private SearchContext searchContext;
    private Annotations annotations;
    private WebElement cachedElement;
    private List<WebElement> cachedElementList;

    public CustomElementLocator(SearchContext searchContext, Field field) {
        this(searchContext, new CustomAnnotation(field));
    }

    public CustomElementLocator(SearchContext searchContext, Annotations annotations) {
        this.searchContext = searchContext;
        this.annotations = annotations;
    }

    @Override
    public WebElement findElement() {
        System.out.println("Find element");
        if(Objects.nonNull(cachedElement) && annotations.isLookupCached()) {
            return cachedElement;
        }
        WebElement element = searchContext.findElement(annotations.buildBy());
        if(annotations.isLookupCached()) {
            cachedElement = element;
        }
        return element;
    }

    @Override
    public List<WebElement> findElements() {
        return searchContext.findElements(annotations.buildBy());
    }
}
