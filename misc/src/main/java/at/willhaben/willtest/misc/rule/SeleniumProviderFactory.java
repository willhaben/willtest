package at.willhaben.willtest.misc.rule;

import at.willhaben.willtest.config.SeleniumProvider;
import at.willhaben.willtest.rule.LocalFirefoxProvider;
import at.willhaben.willtest.util.Environment;
import org.junit.rules.TestRule;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Creates {@link at.willhaben.willtest.config.SeleniumProvider} according to system property.
 */
public class SeleniumProviderFactory {
    private static final String SELENIUM_PROVIDER_CLASS_NAME = "seleniumProvider";
    private static final String DEFAULT_SELENIUM_PROVIDER_CLASS_NAME = LocalFirefoxProvider.class.getName();

    /**
     * Creates {@link SeleniumRule} implementation instance with class name defined in system property
     * {@value #SELENIUM_PROVIDER_CLASS_NAME}. Falls the system property is not present,
     * {@link at.willhaben.willtest.rule.LocalFirefoxProvider} will be used.
     *
     * @return provider according to system property
     */
    public static <P extends SeleniumProvider<P,D> & TestRule,D extends WebDriver> P create() {
        String className = Environment.getValue(
                SELENIUM_PROVIDER_CLASS_NAME,
                DEFAULT_SELENIUM_PROVIDER_CLASS_NAME);
        try {
            Class<P> clazz = (Class<P>) Class.forName(className);
            if (SeleniumProvider.class.isAssignableFrom(clazz)) {
                if (TestRule.class.isAssignableFrom(clazz)) {
                    Constructor<P> providerDefaultConstructor = clazz.getConstructor();
                    return providerDefaultConstructor.newInstance();
                } else {
                    throw new IllegalStateException( className + " is not an implementation of " +
                            TestRule.class.getName() + "!");
                }
            } else {
                throw new IllegalStateException( className + " is not an implementation of " +
                        SeleniumProvider.class.getName() + "!");
            }
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException( className + " has no default constructor, which is expected here!", e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException( className + " class cannot been found!", e);
        } catch (IllegalAccessException|InstantiationException|InvocationTargetException e) {
            throw new IllegalStateException( "Could not create a new instance of " + className + "!", e);
        }
    }
}
