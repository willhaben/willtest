package at.willhaben.willtest.misc.pages.find;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

public class WhListInvocationHandler implements InvocationHandler {

    private ElementLocator locator;
    private WebElementTransformer transformer;

    public WhListInvocationHandler(ElementLocator locator, WebElementTransformer transformer) {
        this.locator = locator;
        this.transformer = transformer;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        List<WebElement> elements = locator.findElements();
        List<Object> transformedElements = elements.stream()
                .map(element -> transformer.generateElement(element))
                .collect(Collectors.toList());
        try {
            return method.invoke(transformedElements, objects);
        } catch (InvocationTargetException e) {
            // Unwrap the underlying exception
            throw e.getCause();
        }
    }
}
