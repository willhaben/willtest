package at.willhaben.willtest.misc.pages.find;

import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;
import org.openqa.selenium.support.ui.Select;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;

public abstract class CustomUiComponentFactory<A extends Annotation, C> {

    public abstract Class<A> customAnnotation();

    public A getAnnotation(Field field) {
        return field.getAnnotation(customAnnotation());
    }

    public abstract By buildBy(A annotation);


    public C generateProxy() {
        return null;
    }

    public InvocationHandler createInvocationHandler() {
        return null;
    }
}
