package at.willhaben.willtest.test;

import at.willhaben.willtest.junit5.BrowserUtil;
import at.willhaben.willtest.junit5.OptionCombiner;
import at.willhaben.willtest.junit5.OptionModifier;
import at.willhaben.willtest.util.AnnotationHelper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BrowserOptionsTest {

    ExtensionContext context;

    @BeforeEach
    void setup() {
        context = mock(ExtensionContext.class);

        when(context.getRequiredTestClass())
                .thenReturn(((Class) TestClass.class));
    }

    @Test
    void mergeOptionsTest() {
        List<OptionModifier> optionAnnotationList
                = AnnotationHelper.getBrowserUtilExtensionList(context, OptionModifier.class, false);
        ChromeOptions modifiedFirefoxOptions = new OptionCombiner(optionAnnotationList)
                .getBrowserOptions(ChromeOptions.class);
        Map<Object, Object> chromeOptionsMap =
                (Map<Object, Object>) modifiedFirefoxOptions.asMap().get(ChromeOptions.CAPABILITY);
        List<String> chromeArguments = (List<String>) chromeOptionsMap.get("args");
        assertThat(chromeArguments, Matchers.containsInAnyOrder("--headless", "--disable-gpu"));
    }

    public static class TestOptions implements OptionModifier {

        @Override
        public ChromeOptions modifyChromeOptions(ChromeOptions options) {
            return options.setHeadless(true);
        }
    }

    @BrowserUtil(TestOptions.class)
    public class TestClass {

    }
}
