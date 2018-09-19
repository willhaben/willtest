package at.willhaben.willtest.misc.pages.find;

import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.Annotations;

import java.lang.reflect.Field;
import java.util.Objects;

public class CustomAnnotation extends Annotations {

    private Field field;

    /**
     * @param field expected to be an element in a Page Object
     */
    public CustomAnnotation(Field field) {
        super(field);
        this.field = field;
    }

    @Override
    public By buildBy() {
        FindTestId annotation = field.getAnnotation(FindTestId.class);
        if(Objects.isNull(annotation)) {
            return super.buildByFromDefault();
        }
        String dataTestId = annotation.value();
        if(annotation.tagName().isEmpty()) {
            return By.cssSelector("[data-testid='" + dataTestId + "']");
        } else {
            return By.cssSelector(annotation.tagName() + "[data-testid='" + dataTestId + "']");
        }
    }
}
