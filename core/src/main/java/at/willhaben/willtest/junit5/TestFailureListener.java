package at.willhaben.willtest.junit5;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;

public interface TestFailureListener extends BrowserUtilExtension {

    void onFailure(ExtensionContext context, WebDriver driver, Throwable throwable) throws Throwable;
}
