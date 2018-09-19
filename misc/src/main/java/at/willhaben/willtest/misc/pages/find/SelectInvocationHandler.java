package at.willhaben.willtest.misc.pages.find;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.ui.Select;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class SelectInvocationHandler implements InvocationHandler {

    private ElementLocator locator;

    public SelectInvocationHandler(ElementLocator locator) {
        this.locator = locator;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        WebElement element = locator.findElement();

        if ("getWrappedElement".equals(method.getName())) {
            return element;
        }
        Select select = new Select(element);
        System.out.println(method.getName());
        return method.invoke(select, objects);
    }
}
