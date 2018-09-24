package at.willhaben.willtest.misc.pages.find;

import org.openqa.selenium.By;
import org.openqa.selenium.support.AbstractFindByBuilder;
import org.openqa.selenium.support.PageFactoryFinder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
@PageFactoryFinder(FindTestId.FindTestIdBuilder.class)
public @interface FindTestId {
    String value();
    String tagName() default "";

    class FindTestIdBuilder extends AbstractFindByBuilder {
        @Override
        public By buildIt(Object annotation, Field field) {
            FindTestId findBy = (FindTestId) annotation;
            String dataTestId = findBy.value();
            if(findBy.tagName().isEmpty()) {
                return By.cssSelector("[data-testid='" + dataTestId + "']");
            } else {
                return By.cssSelector(findBy.tagName() + "[data-testid='" + dataTestId + "']");
            }
        }
    }
}
