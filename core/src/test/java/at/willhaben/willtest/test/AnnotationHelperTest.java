package at.willhaben.willtest.test;

import at.willhaben.willtest.junit5.BrowserOptionInterceptor;
import at.willhaben.willtest.junit5.BrowserUtil;
import at.willhaben.willtest.util.AnnotationHelper;
import at.willhaben.willtest.util.BrowserOptionProvider;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static at.willhaben.willtest.util.AnnotationHelper.getBrowserUtilExtensionList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AnnotationHelperTest {

    private static final String TEST_CAP_NAME = "test";

    ExtensionContext extensionContext;

    @BeforeEach
    void before() throws NoSuchMethodException {
        extensionContext = mock(ExtensionContext.class);

        when(extensionContext.getRequiredTestClass())
                .thenReturn((Class) OrderTesting.class);

        when(extensionContext.getRequiredTestMethod())
                .thenReturn(OrderTesting.class.getMethod("testing"));
    }

    @Test
    void testBrowserUtilOnSuperClass() {
        BrowserUtil browserUtil = AnnotationHelper.getFirstSuperClassAnnotation(Testing.class, BrowserUtil.class);
        assertThat(browserUtil, Matchers.notNullValue());
    }

    @Test
    void testNoBrowserUtil() {
        BrowserUtil browserUtil = AnnotationHelper.getFirstSuperClassAnnotation(NoAnnotation.class, BrowserUtil.class);
        assertThat(browserUtil, Matchers.nullValue());
        browserUtil = AnnotationHelper.getFirstSuperClassAnnotation(NoAnnotationSuperClass.class, BrowserUtil.class);
        assertThat(browserUtil, Matchers.nullValue());
    }

    @Test
    void testOrderOfExtensions() {
        List<BrowserOptionInterceptor> optionInterceptors =
                getBrowserUtilExtensionList(extensionContext, BrowserOptionInterceptor.class, false);
        assertTrue(optionInterceptors.get(0).getClass().isAssignableFrom(Browserconfig3.class));

        List<String> testCaps = optionInterceptors.stream()
                .map(BrowserOptionInterceptor::getFirefoxOptions)
                .map(cap -> (String) cap.getCapability(TEST_CAP_NAME))
                .collect(Collectors.toList());

        assertEquals(testCaps, Arrays.asList("3", "2", "1"));

        Capabilities caps = new BrowserOptionProvider(optionInterceptors).getFirefoxOptions();
        assertThat(caps.getCapability(TEST_CAP_NAME), Matchers.is("1"));
    }

    private class Testing extends ValidSuperClass {}

    @BrowserUtil(Browserconfig3.class)
    private class ValidSuperClass {}

    private class NoAnnotation {}

    private class NoAnnotationSuperClass extends NoAnnotation {}



    @BrowserUtil(Browserconfig2.class)
    private class OrderTesting extends ValidSuperClass {

        @BrowserUtil(Browserconfig1.class)
        public void testing() {

        }
    }

    public static class Browserconfig1 extends BrowserOptionInterceptor {
        @Override
        public FirefoxOptions getFirefoxOptions() {
            FirefoxOptions options = new FirefoxOptions();
            options.setCapability(TEST_CAP_NAME, "1");
            return options;
        }
    }

    public static class Browserconfig2 extends BrowserOptionInterceptor {
        @Override
        public FirefoxOptions getFirefoxOptions() {
            FirefoxOptions options = new FirefoxOptions();
            options.setCapability(TEST_CAP_NAME, "2");
            return options;
        }
    }

    public static class Browserconfig3 extends BrowserOptionInterceptor {
        @Override
        public FirefoxOptions getFirefoxOptions() {
            FirefoxOptions options = new FirefoxOptions();
            options.setCapability(TEST_CAP_NAME, "3");
            return options;
        }
    }
}
