package at.willhaben.misc.test.pages;

import at.willhaben.willtest.misc.pages.find.CustomUiComponentFactory;
import at.willhaben.willtest.misc.pages.find.FindTestId;
import org.openqa.selenium.By;

public class FindTestIdComponent extends CustomUiComponentFactory<FindTestId, Void> {

    @Override
    public Class<FindTestId> customAnnotation() {
        return FindTestId.class;
    }

    @Override
    public By buildBy(FindTestId annotation) {
        String dataTestId = annotation.value();
        if(annotation.tagName().isEmpty()) {
            return By.cssSelector("[data-testid='" + dataTestId + "']");
        } else {
            return By.cssSelector(annotation.tagName() + "[data-testid='" + dataTestId + "']");
        }
    }


}
