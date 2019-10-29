package at.willhaben.willtest.junit5.extensions;

import at.willhaben.willtest.browserstack.BrowserstackEnvironment;
import at.willhaben.willtest.browserstack.exception.BrowserstackEnvironmentException;
import at.willhaben.willtest.junit5.*;
import at.willhaben.willtest.proxy.BrowserProxyBuilder;
import at.willhaben.willtest.proxy.ProxyOptionModifier;
import at.willhaben.willtest.proxy.ProxyWrapper;
import at.willhaben.willtest.proxy.impl.ProxyWrapperImpl;
import at.willhaben.willtest.util.AndroidOptions;
import at.willhaben.willtest.util.BrowserSelectionUtils;
import at.willhaben.willtest.util.IOsOptions;
import at.willhaben.willtest.util.PlatformUtils;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import net.lightbody.bmp.BrowserMobProxy;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static at.willhaben.willtest.util.AnnotationHelper.getBrowserUtilExtensionList;
import static at.willhaben.willtest.util.AssumptionUtil.isAssumptionViolation;
import static at.willhaben.willtest.util.RemoteSelectionUtils.RemotePlatform.BROWSERSTACK;
import static at.willhaben.willtest.util.RemoteSelectionUtils.getRemotePlatform;
import static at.willhaben.willtest.util.RemoteSelectionUtils.isRemote;


public class DriverParameterResolverExtension implements ParameterResolver, BeforeEachCallback,
        AfterEachCallback, AfterAllCallback, TestExecutionExceptionHandler {

    public static final String DRIVER_KEY = "wh-webDriver";
    private static final String BEFOREALL_DRIVER_KEY = "wh-beforeall-webDriver";
    private static final String PROXY_KEY = "wh-proxy";
    private static final Logger LOGGER = LoggerFactory.getLogger(DriverParameterResolverExtension.class);

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Class<?> parameterType = parameterContext.getParameter().getType();
        return parameterType.isAssignableFrom(WebDriver.class) ||
                parameterType.isAssignableFrom(ProxyWrapper.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Class<?> parameterType = parameterContext.getParameter().getType();
        Optional<WebDriver> driverCreatedInBeforeEach = getDriverFromStore(extensionContext, DRIVER_KEY);
        Optional<ProxyWrapper> proxyCreatedInBeforeEach = getProxyFromStore(extensionContext);

        if (parameterType.isAssignableFrom(WebDriver.class) && driverCreatedInBeforeEach.isPresent()) {
            return driverCreatedInBeforeEach.get();
        } else if (parameterType.isAssignableFrom(ProxyWrapper.class) && proxyCreatedInBeforeEach.isPresent()) {
            return proxyCreatedInBeforeEach.get();
        } else if (parameterType.isAssignableFrom(WebDriver.class)) {
            List<OptionModifier> modifiers = new ArrayList<>();
            if (shouldStartProxy(extensionContext)) {
                BrowserMobProxy proxy = BrowserProxyBuilder.builder()
                        .startProxy();
                modifiers.add(new ProxyOptionModifier(proxy));
                getStore(extensionContext).put(PROXY_KEY, new ProxyWrapperImpl(proxy));
            }
            List<WebDriverPostInterceptor> driverPostInterceptorList = getBrowserPostProcess(extensionContext);
            modifiers.addAll(getBrowserOptionModifiers(extensionContext));
            WebDriver driver = createDriver(modifiers, driverPostInterceptorList, getTestName(extensionContext));
            if (extensionContext.getTestMethod().isPresent()) {
                getStore(extensionContext).put(DRIVER_KEY, driver);
            } else {
                getStore(extensionContext).put(BEFOREALL_DRIVER_KEY, driver);
            }
            return driver;
        } else if (parameterType.isAssignableFrom(ProxyWrapper.class) && getProxyFromStore(extensionContext).isPresent()) {
            return getProxyFromStore(extensionContext).get();
        } else {
            return null;
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Optional<WebDriver> driver = getDriverFromStore(context, DRIVER_KEY);
        for (TestStartListener testStartListener : getBrowserUtils(context, TestStartListener.class)) {
            try {
                testStartListener.testStarted(context, getTestName(context));
            } catch (Exception e) {
                LOGGER.error("Test start listener couldn't be started", e);
                throw e;
            }
        }
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        closeDriver(extensionContext, DRIVER_KEY);
        getProxyFromStore(extensionContext).ifPresent(proxyUtil -> proxyUtil.getProxy().abort());
    }

    private void closeDriver(ExtensionContext extensionContext, String driverKey) {
        getDriverFromStore(extensionContext, driverKey).ifPresent(WebDriver::quit);
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        closeDriver(context, BEFOREALL_DRIVER_KEY);
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        if (isAssumptionViolation(throwable)) {
            throw throwable;
        }
        Optional<WebDriver> driver = getDriverFromStore(context, DRIVER_KEY);
        if (driver.isPresent()) {
            for (TestFailureListener testFailureListener : getFailureListeners(context)) {
                try {
                    testFailureListener.onFailure(context, driver.get(), throwable);
                } catch (Exception e) {
                    throwable.addSuppressed(e);
                }
            }
        }
        throw throwable;
    }

    public boolean shouldStartProxy(ExtensionContext context) {
        Optional<Method> testMethod = context.getTestMethod();
        if (testMethod.isPresent()) {
            return Arrays.asList(testMethod.get().getParameterTypes()).contains(ProxyWrapper.class);
        } else {
            LOGGER.debug("The test method is not present. No proxy can be started.");
            return false;
        }
    }

    public static Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(DriverParameterResolverExtension.class));
    }

    public static Optional<WebDriver> getDriverFromStore(ExtensionContext context, String driverKey) {
        return Optional.ofNullable(getStore(context).get(driverKey, WebDriver.class));
    }

    public static Optional<ProxyWrapper> getProxyFromStore(ExtensionContext context) {
        return Optional.ofNullable(getStore(context).get(PROXY_KEY, ProxyWrapper.class));
    }

    private String getTestName(ExtensionContext context) {
        return context.getRequiredTestClass().getSimpleName() + "_" + context.getRequiredTestMethod().getName();
    }

    private WebDriver createDriver(List<OptionModifier> modifiers, List<WebDriverPostInterceptor> driverPostInterceptorList, String testName) {
        String seleniumHub = System.getProperty("seleniumHub");
        FirefoxOptions firefoxOptions;
        ChromeOptions chromeOptions;
        EdgeOptions edgeOptions;
        InternetExplorerOptions internetExplorerOptions;
        AndroidOptions androidOptions;
        IOsOptions iOsOptions;

        // use new option modifiers if the list is not empty
        if (!modifiers.isEmpty()) {
            OptionCombiner optionCombiner = new OptionCombiner(modifiers);
            firefoxOptions = optionCombiner.getBrowserOptions(FirefoxOptions.class);
            chromeOptions = optionCombiner.getBrowserOptions(ChromeOptions.class);
            edgeOptions = optionCombiner.getBrowserOptions(EdgeOptions.class);
            internetExplorerOptions = optionCombiner.getBrowserOptions(InternetExplorerOptions.class);
            androidOptions = optionCombiner.getBrowserOptions(AndroidOptions.class);
            iOsOptions = optionCombiner.getBrowserOptions(IOsOptions.class);
        } else {
            firefoxOptions = new FirefoxOptions();
            chromeOptions = new ChromeOptions();
            edgeOptions = new EdgeOptions();
            internetExplorerOptions = new InternetExplorerOptions();
            androidOptions = new AndroidOptions();
            iOsOptions = new IOsOptions();
        }

        if (isRemote()) {
            if (getRemotePlatform() == BROWSERSTACK) {
                List<BrowserstackEnvironment> browserstackEnvironments = BrowserstackEnvironment.parseFromSystemProperties();
                if (browserstackEnvironments.size() != 1) {
                    throw new BrowserstackEnvironmentException("Exactly one browserstack environment must be specified. " +
                            "See BrowserstackSystemProperties class for more information.");
                }
                BrowserstackEnvironment browserstackEnv = browserstackEnvironments.get(0);
                firefoxOptions = browserstackEnv.addToCapabilities(firefoxOptions, testName);
                chromeOptions = browserstackEnv.addToCapabilities(chromeOptions, testName);
                edgeOptions = browserstackEnv.addToCapabilities(edgeOptions, testName);
                internetExplorerOptions = browserstackEnv.addToCapabilities(internetExplorerOptions, testName);
                androidOptions = browserstackEnv.addToCapabilities(androidOptions, testName);
                iOsOptions = browserstackEnv.addToCapabilities(iOsOptions, testName);
            }
        }

        WebDriver driver;
        if (PlatformUtils.isAndroid()) {
            if (isRemote()) {
                URL seleniumHubUrl = convertSeleniumHubToURL(seleniumHub);
                driver = new AndroidDriver<>(seleniumHubUrl, androidOptions);
            } else {
                driver = new AndroidDriver<>(androidOptions);
            }
        } else if (PlatformUtils.isIOS()) {
            if (isRemote()) {
                URL seleniumHubUrl = convertSeleniumHubToURL(seleniumHub);
                driver = new IOSDriver<>(seleniumHubUrl, iOsOptions);
            } else {
                driver = new IOSDriver<>(iOsOptions);
            }
            ((AppiumDriver) driver).context("NATIVE_APP");
        } else if (PlatformUtils.isLinux() || PlatformUtils.isWindows()) {
            if (isRemote()) {
                URL seleniumHubUrl = convertSeleniumHubToURL(seleniumHub);
                RemoteWebDriver remoteDriver;
                if (BrowserSelectionUtils.isFirefox()) {
                    remoteDriver = new RemoteWebDriver(seleniumHubUrl, firefoxOptions);
                } else if (BrowserSelectionUtils.isChrome()) {
                    remoteDriver = new RemoteWebDriver(seleniumHubUrl, chromeOptions);
                } else if (BrowserSelectionUtils.isIE()) {
                    remoteDriver = new RemoteWebDriver(seleniumHubUrl, internetExplorerOptions);
                } else if (BrowserSelectionUtils.isEdge()) {
                    remoteDriver = new RemoteWebDriver(seleniumHubUrl, edgeOptions);
                } else {
                    throw new RuntimeException("The specified browser '" + BrowserSelectionUtils.getBrowser() + "' is not supported");
                }
                remoteDriver.setFileDetector(new LocalFileDetector());
                driver = remoteDriver;
            } else {
                if (BrowserSelectionUtils.isFirefox()) {
                    driver = new FirefoxDriver(firefoxOptions);
                } else if (BrowserSelectionUtils.isChrome()) {
                    driver = new ChromeDriver(chromeOptions);
                } else if (BrowserSelectionUtils.isIE()) {
                    driver = new InternetExplorerDriver(internetExplorerOptions);
                } else if (BrowserSelectionUtils.isEdge()) {
                    driver = new EdgeDriver(edgeOptions);
                } else {
                    throw new RuntimeException("The specified browser '" + BrowserSelectionUtils.getBrowser() + "' is not supported");
                }
            }
        } else {
            throw new RuntimeException("The specified platform '" + PlatformUtils.getPlatform() + "' is not supported.");
        }
        driverPostInterceptorList.forEach(postProcessor -> postProcessor.postProcessWebDriver(driver));
        return driver;
    }

    private URL convertSeleniumHubToURL(String seleniumHub) {
        try {
            return new URL(seleniumHub);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed url created", e);
        }
    }

    private List<OptionModifier> getBrowserOptionModifiers(ExtensionContext context) {
        return getBrowserUtilExtensionList(context, OptionModifier.class, false);
    }

    private List<WebDriverPostInterceptor> getBrowserPostProcess(ExtensionContext context) {
        return getBrowserUtilExtensionList(context, WebDriverPostInterceptor.class, true);
    }

    private List<TestFailureListener> getFailureListeners(ExtensionContext context) {
        return getBrowserUtilExtensionList(context, TestFailureListener.class, true);
    }

    private <T extends BrowserUtilExtension> List<T> getBrowserUtils(ExtensionContext context, Class<T> type) {
        return getBrowserUtilExtensionList(context, type, true);
    }
}
