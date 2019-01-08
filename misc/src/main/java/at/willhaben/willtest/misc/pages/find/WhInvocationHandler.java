package at.willhaben.willtest.misc.pages.find;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.ui.Select;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class WhInvocationHandler implements InvocationHandler {

    private ElementLocator locator;
    private WebElementTransformer transformer;

    public WhInvocationHandler(ElementLocator locator, WebElementTransformer transformer) {
        this.locator = locator;
        this.transformer = transformer;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        WebElement element = locator.findElement();

        if ("getWrappedElement".equals(method.getName())) {
            return element;
        }

        Object customUiComponent = transformer.generateElement(element);
        return method.invoke(customUiComponent, objects);
    }
}
