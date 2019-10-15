package at.willhaben.willtest.test;

import at.willhaben.willtest.junit5.BrowserUtil;
import at.willhaben.willtest.junit5.OptionCombiner;
import at.willhaben.willtest.junit5.OptionModifier;
import at.willhaben.willtest.util.AnnotationHelper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

        when(extensionContext.getTestMethod())
                .thenReturn(Optional.of(OrderTesting.class.getMethod("testing")));
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
        List<OptionModifier> optionModifierList =
                getBrowserUtilExtensionList(extensionContext, OptionModifier.class, false);
        assertTrue(optionModifierList.get(0).getClass().isAssignableFrom(Browserconfig3.class));

        List<String> testCaps = optionModifierList.stream()
                .map(modifier -> new OptionCombiner(Collections.singletonList(modifier)))
                .map(combiner -> combiner.getBrowserOptions(FirefoxOptions.class))
                .map(cap -> (String) cap.getCapability(TEST_CAP_NAME))
                .collect(Collectors.toList());

        assertEquals(testCaps, Arrays.asList("3", "2", "1"));

        Capabilities caps = new OptionCombiner(optionModifierList).getBrowserOptions(FirefoxOptions.class);
        assertThat(caps.getCapability(TEST_CAP_NAME), Matchers.is("1"));
    }

    public static class Browserconfig1 implements OptionModifier {
        @Override
        public FirefoxOptions modifyFirefoxOptions(FirefoxOptions options) {
            options.setCapability(TEST_CAP_NAME, "1");
            return options;
        }
    }

    public static class Browserconfig2 implements OptionModifier {
        @Override
        public FirefoxOptions modifyFirefoxOptions(FirefoxOptions options) {
            options.setCapability(TEST_CAP_NAME, "2");
            return options;
        }
    }

    public static class Browserconfig3 implements OptionModifier {
        @Override
        public FirefoxOptions modifyFirefoxOptions(FirefoxOptions options) {
            options.setCapability(TEST_CAP_NAME, "3");
            return options;
        }
    }

    private class Testing extends ValidSuperClass {
    }

    @BrowserUtil(Browserconfig3.class)
    private class ValidSuperClass {
    }

    private class NoAnnotation {
    }

    private class NoAnnotationSuperClass extends NoAnnotation {
    }

    @BrowserUtil(Browserconfig2.class)
    private class OrderTesting extends ValidSuperClass {

        @BrowserUtil(Browserconfig1.class)
        public void testing() {

        }
    }

}
