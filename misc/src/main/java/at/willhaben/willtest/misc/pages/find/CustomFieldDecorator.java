package at.willhaben.willtest.misc.pages.find;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.internal.Locatable;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.ui.ISelect;
import org.openqa.selenium.support.ui.Select;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Objects;

public class CustomFieldDecorator extends DefaultFieldDecorator {

//    private Map<Class, >

    public CustomFieldDecorator(ElementLocatorFactory factory) {
        super(factory);
    }

    @Override
    public Object decorate(ClassLoader loader, Field field) {
        Object element = null;
        if(Objects.isNull(element)) {
            System.out.println("Default decorate has no success.");
            if(Select.class.isAssignableFrom(field.getType())) {
                System.out.println("Is assignable to Select.");
                SelectInvocationHandler handler = new SelectInvocationHandler(factory.createLocator(field));
                Select select = (Select) Proxy.newProxyInstance(loader, new Class[]{ISelect.class}, handler);
                return select;
            } else {
                return super.decorate(loader, field);
            }
        } else {
            return super.decorate(loader, field);
        }
    }
}
