package at.willhaben.willtest.misc.pages.find;

import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.lang.reflect.InvocationHandler;

public abstract class CustomElementInvocationHandler implements InvocationHandler {

    protected ElementLocator locator;

    public CustomElementInvocationHandler(ElementLocator locator) {
        this.locator = locator;
    }
}
