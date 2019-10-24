package at.willhaben.willtest.junit5;

import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.IOException;

public interface TestStartListener extends BrowserUtilExtension {

    void testStarted(ExtensionContext context, String testName) throws IOException;
}
