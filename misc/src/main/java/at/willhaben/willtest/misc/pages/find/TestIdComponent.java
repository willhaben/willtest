package at.willhaben.willtest.misc.pages.find;

import org.openqa.selenium.By;

public class TestIdComponent extends CustomUiComponentFactory<FindTestId, Void> {

    @Override
    public Class<FindTestId> customAnnotation() {
        return FindTestId.class;
    }

    @Override
    public By buildBy(FindTestId annotation) {
        String testId = annotation.value();
        if(annotation.tagName().isEmpty()) {
            return By.cssSelector("[data-testid='" + testId + "']");
        } else {
            return By.cssSelector(annotation.tagName() + "[data-testid='" + testId + "']");
        }
    }
}
