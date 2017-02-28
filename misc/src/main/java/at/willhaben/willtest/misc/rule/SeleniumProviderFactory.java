package at.willhaben.willtest.misc.rule;

import at.willhaben.willtest.config.SeleniumProvider;
import at.willhaben.willtest.rule.LocalFirefoxProvider;
import at.willhaben.willtest.util.Environment;
import org.junit.rules.TestRule;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Creates {@link SeleniumProvider} according to system property, and it injects
 * its basic dependencies based on class+object pairs (see {@link ParameterObject}).
 */
public class SeleniumProviderFactory {
    public static final String SELENIUM_PROVIDER_CLASS_NAME = "seleniumProvider";
    private static final String DEFAULT_SELENIUM_PROVIDER_CLASS_NAME = LocalFirefoxProvider.class.getName();

    /**
     * Creates {@link SeleniumRule} implementation instance with class name defined in system property
     * {@value #SELENIUM_PROVIDER_CLASS_NAME}. If the system property is not present,
     * {@link LocalFirefoxProvider} will be used.<br/>
     * It is expected, that {@link SeleniumProvider} implementations used together with this class do have public
     * default constructor.<br>
     * An array of {@link ParameterObject} can be passed in as parameter. After creating the {@link SeleniumProvider}
     * instance, setters will be searched for each {@link ParameterObject#getClazz()} based on class name. If there is
     * such method, it will be called with the result of {@link ParameterObject#getObject}.
     * @return provider according to system property
     */
    public static <P extends SeleniumProvider<P, D> & TestRule, D extends WebDriver> P createSeleniumProviderRule(
            ParameterObject... parameterObjects) {
        String className = Environment.getValue(
                SELENIUM_PROVIDER_CLASS_NAME,
                DEFAULT_SELENIUM_PROVIDER_CLASS_NAME);
        try {
            @SuppressWarnings("unchecked") Class<P> clazz = (Class<P>) Class.forName(className);
            if (SeleniumProvider.class.isAssignableFrom(clazz)) {
                if (TestRule.class.isAssignableFrom(clazz)) {
                    Constructor<P> providerDefaultConstructor = clazz.getConstructor();
                    P provider = providerDefaultConstructor.newInstance();
                    Arrays.stream(parameterObjects)
                            .forEach(parameterObject ->
                                    setObject(provider, parameterObject));
                    return provider;
                } else {
                    throw new IllegalStateException(className + " is not an implementation of " +
                            TestRule.class.getName() + "!");
                }
            } else {
                throw new IllegalStateException(className + " is not an implementation of " +
                        SeleniumProvider.class.getName() + "!");
            }
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(className + " has no default constructor, which is expected here!", e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(className + " class cannot been found!", e);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new IllegalStateException("Could not create a new instance of " + className + "!", e);
        }
    }

    private static void setObject(SeleniumProvider<?, ?> seleniumProvider, ParameterObject parameterObject) {
        String className = parameterObject.getClazz().getSimpleName();
        Object object = parameterObject.getObject();
        String expectedSetterName = "set" + className;
        Method[] methods = seleniumProvider.getClass().getMethods();
        Arrays
                .stream(methods)
                .filter(method -> method.getName().equals(expectedSetterName))
                .filter(method -> method.getParameterCount() == 1)
                .filter(method -> method.getParameterTypes()[0].equals(object.getClass()))
                .forEach(method -> {
                    try {
                        method.invoke(seleniumProvider, object);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException("Cannot set " + object + " using method " + method + "!", e);
                    }
                });
    }

    /**
     * Parameter object, which can be injected into a setter of the instantiated {@link SeleniumProvider} implementations.
     */
    public static class ParameterObject {
        private final Class<?> clazz;
        private final Object object;

        /**
         * @param clazz class of the object. Setter will be searched as "set"+{@link Class#getSimpleName()}
         * @param object the object, which will be the parameter of the method
         */
        public ParameterObject(Class<?> clazz, Object object) {
            this.clazz = clazz;
            this.object = object;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public Object getObject() {
            return object;
        }
    }
}
