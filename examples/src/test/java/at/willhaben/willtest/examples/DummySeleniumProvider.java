package at.willhaben.willtest.examples;

import at.willhaben.willtest.rule.AbstractSeleniumProvider;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.regex.Pattern;

import static org.mockito.Mockito.*;

public class DummySeleniumProvider extends AbstractSeleniumProvider<DummySeleniumProvider,WebDriver> {
    private Pattern patternField;

    /**
     * This will be injected by {@link at.willhaben.willtest.misc.rule.SeleniumProviderFactory} based on the setter name
     * @param patternField just a demo dependency
     */
    public void setPattern(Pattern patternField) {
        this.patternField = patternField;
    }

    @Override
    public DummySeleniumProvider getThis() {
        return this;
    }

    @Override
    protected WebDriver constructWebDriver(DesiredCapabilities desiredCapabilities) {
        WebDriver webDriver = mock(WebDriver.class, RETURNS_DEEP_STUBS);
        doReturn(patternField.pattern()).when(webDriver).getCurrentUrl();
        return webDriver;
    }
}
