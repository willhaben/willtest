package at.willhaben.willtest.junit5.extensions;

import at.willhaben.willtest.junit5.*;
import at.willhaben.willtest.util.BrowserSelectionUtils;
import at.willhaben.willtest.util.PlatformUtils;
import at.willhaben.willtest.util.RemoteSelectionUtils;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;


public class DriverParameterResolver implements ParameterResolver, AfterEachCallback {

    public static final String DRIVER_KEY = "wh-webDriver";
    private static final String PROPERTY_STORE_PATH = "C:\\development\\willhaben-test-utils\\misc\\src\\main\\resources\\willhaben.properties";
    private static Logger logger = LogManager.getLogger();
    private WebDriver driver;

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Class<?> type = parameterContext.getParameter().getType();
        if (AppiumDriver.class.isAssignableFrom(type)) {
            return true;
        } else if (WebDriver.class.isAssignableFrom(type)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        ExtensionContext.Store store = extensionContext.getStore(ExtensionContext.Namespace.GLOBAL);
        BrowserOptionInterceptor browserOptionInterceptor = getBrowserOptionInterceptor(extensionContext);
        String seleniumHub = System.getProperty("seleniumHub");
        if (PlatformUtils.isAndroid()) {
            if (RemoteSelectionUtils.isRemote()) {
                URL seleniumHubUrl = convertSeleniumHubToURL(seleniumHub);
                driver = new AndroidDriver<>(seleniumHubUrl,browserOptionInterceptor.getAndroidCapabilities());
            } else {
                driver = new AndroidDriver<>(browserOptionInterceptor.getAndroidCapabilities());
            }
        } else if (PlatformUtils.isIOS()) {
            if (RemoteSelectionUtils.isRemote()) {
                URL seleniumHubUrl = convertSeleniumHubToURL(seleniumHub);
                driver = new IOSDriver<>(seleniumHubUrl,browserOptionInterceptor.getIOsCapabilities());
            } else {
                driver = new IOSDriver<>(browserOptionInterceptor.getIOsCapabilities());
            }
            ((AppiumDriver) driver).context("NATIVE_APP");
        } else if (PlatformUtils.isLinux() || PlatformUtils.isWindows()) {
            if (RemoteSelectionUtils.isRemote()) {
                URL seleniumHubUrl = convertSeleniumHubToURL(seleniumHub);
                RemoteWebDriver remoteDriver;
                if (BrowserSelectionUtils.isFirefox()) {
                    remoteDriver = new RemoteWebDriver(seleniumHubUrl, browserOptionInterceptor.getFirefoxOptions());
                } else if (BrowserSelectionUtils.isChrome()) {
                    remoteDriver = new RemoteWebDriver(seleniumHubUrl, browserOptionInterceptor.getChromeOptions());
                } else if (BrowserSelectionUtils.isIE()) {
                    remoteDriver = new RemoteWebDriver(seleniumHubUrl, browserOptionInterceptor.getInternetExplorerOptions());
                } else if (BrowserSelectionUtils.isEdge()) {
                    remoteDriver = new RemoteWebDriver(seleniumHubUrl, browserOptionInterceptor.getEdgeOptions());
                } else {
                    throw new RuntimeException("The specified browser '" + BrowserSelectionUtils.getBrowser() + "' is not supported");
                }
                remoteDriver.setFileDetector(new LocalFileDetector());
                this.driver = remoteDriver;
            } else {
                if (BrowserSelectionUtils.isFirefox()) {
                    driver = new FirefoxDriver(browserOptionInterceptor.getFirefoxOptions());
                    //Could be replaced by Boolean.getBoolean("maximizeFF"), which is the same
                } else if (BrowserSelectionUtils.isChrome()) {
                    driver = new ChromeDriver(browserOptionInterceptor.getChromeOptions());
                } else if (BrowserSelectionUtils.isIE()) {
                    driver = new InternetExplorerDriver(browserOptionInterceptor.getInternetExplorerOptions());
                } else {
                    throw new RuntimeException("The specified browser '" + BrowserSelectionUtils.getBrowser() + "' is not supported");
                }
            }
        } else {
            throw new RuntimeException("The specified platform '" + PlatformUtils.getPlatform() + "' is not supported.");
        }
        store.put(DRIVER_KEY, driver);
        return driver;
    }

    private URL convertSeleniumHubToURL(String seleniumHub) {
        URI uri;
        try {
            uri = new URI(seleniumHub);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Couldn't create URI from '" + seleniumHub + "'", e);
        } catch (NullPointerException e) {
            throw new NullPointerException("The seleniumHub system property was not set! Use with -DseleniumHub='ExampleValue'");
        }
        URL seleniumHubUrl;
        try {
            seleniumHubUrl = uri.toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed url created", e);
        }
        return seleniumHubUrl;
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        ExtensionContext.Store store = extensionContext.getStore(ExtensionContext.Namespace.GLOBAL);

        WebDriver driver = (WebDriver) store.get(DRIVER_KEY);
        if (driver != null) {
            driver.quit();
        }
    }

    public WebDriver getWebDriver() {
        if (driver == null) {
            throw new RuntimeException("WebDriver has not been created. Did you use this class as a junit rule? " +
                    "Can it be, that the before method has not been called?");
        }
        return driver;
    }

    private BrowserOptionInterceptor getBrowserOptionInterceptor(ExtensionContext context) {
        BrowserUtil browserUtil = context.getRequiredTestMethod().getAnnotation(BrowserUtil.class);
        if (browserUtil == null) {
            browserUtil = context.getRequiredTestClass().getAnnotation(BrowserUtil.class);
        }
        if (browserUtil == null) {
            return new DefaultBrowserOptionInterceptor();
        }
        Optional<Class<? extends BrowserUtilExtension>> browserOptionInterceptor = Arrays.stream(browserUtil.value())
                .filter(BrowserOptionInterceptor.class::isAssignableFrom)
                .findFirst();
        if (browserOptionInterceptor.isPresent()) {
            try {
                return (BrowserOptionInterceptor) browserOptionInterceptor.get().getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Can't instantiate BrowserOption", e);
            }
        } else {
            return new DefaultBrowserOptionInterceptor();
        }
    }

    private WebDriverPostInterceptor getBrowserPostProcess(ExtensionContext context) {
        BrowserUtil browserUtil = context.getRequiredTestMethod().getAnnotation(BrowserUtil.class);
        if (browserUtil == null) {
            browserUtil = context.getRequiredTestClass().getAnnotation(BrowserUtil.class);
        }
        if (browserUtil == null) {
            return new DefaultWebDriverPostInterceptor();
        }
        Optional<Class<? extends BrowserUtilExtension>> webDriverPostInterceptor = Arrays.stream(browserUtil.value())
                .filter(WebDriverPostInterceptor.class::isAssignableFrom)
                .findFirst();
        if (webDriverPostInterceptor.isPresent()) {
            try {
                return (WebDriverPostInterceptor) webDriverPostInterceptor.get().getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Can't instantiate BrowserOption", e);
            }
        } else {
            return new DefaultWebDriverPostInterceptor();
        }
    }
}
