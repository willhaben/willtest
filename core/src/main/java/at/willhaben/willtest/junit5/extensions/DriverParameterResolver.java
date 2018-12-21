package at.willhaben.willtest.junit5.extensions;

import at.willhaben.willtest.junit5.*;
import at.willhaben.willtest.util.AnnotationHelper;
import at.willhaben.willtest.util.BrowserSelectionUtils;
import at.willhaben.willtest.util.PlatformUtils;
import at.willhaben.willtest.util.RemoteSelectionUtils;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class DriverParameterResolver implements ParameterResolver, AfterEachCallback {

    public static final String DRIVER_KEY = "wh-webDriver";
    private static Logger logger = LogManager.getLogger();

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Class<?> parameterType = parameterContext.getParameter().getType();
        return parameterType.isAssignableFrom(WebDriver.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        BrowserOptionInterceptor browserOptionInterceptor = getBrowserOptionInterceptor(extensionContext);
        List<WebDriverPostInterceptor> driverPostInterceptorList = getBrowserPostProcess(extensionContext);
        WebDriver driver = createDriver(browserOptionInterceptor, driverPostInterceptorList);
        getStore(extensionContext).put(DRIVER_KEY, driver);
        return driver;
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        WebDriver driver = getDriverFromStore(extensionContext);
        if (driver != null) {
            driver.quit();
        }
    }

    public static Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(DriverParameterResolver.class));
    }

    public static WebDriver getDriverFromStore(ExtensionContext context) {
        return (WebDriver) getStore(context).get(DRIVER_KEY);
    }

    private WebDriver createDriver(BrowserOptionInterceptor browserOptionInterceptor, List<WebDriverPostInterceptor> driverPostInterceptorList) {
        String seleniumHub = System.getProperty("seleniumHub");
        WebDriver driver;
        if (PlatformUtils.isAndroid()) {
            if (RemoteSelectionUtils.isRemote()) {
                URL seleniumHubUrl = convertSeleniumHubToURL(seleniumHub);
                driver = new AndroidDriver<>(seleniumHubUrl, browserOptionInterceptor.getAndroidCapabilities());
            } else {
                driver = new AndroidDriver<>(browserOptionInterceptor.getAndroidCapabilities());
            }
        } else if (PlatformUtils.isIOS()) {
            if (RemoteSelectionUtils.isRemote()) {
                URL seleniumHubUrl = convertSeleniumHubToURL(seleniumHub);
                driver = new IOSDriver<>(seleniumHubUrl, browserOptionInterceptor.getIOsCapabilities());
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
                driver = remoteDriver;
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

    private BrowserOptionInterceptor getBrowserOptionInterceptor(ExtensionContext context) {
        List<BrowserOptionInterceptor> browserOptionInterceptors = getBrowserUtilExtensionList(context, BrowserOptionInterceptor.class);
        if (browserOptionInterceptors.isEmpty()) {
            return new DefaultBrowserOptionInterceptor();
        } else {
            if (browserOptionInterceptors.size() > 1) {
                throw new RuntimeException("There should only be one " + BrowserOptionInterceptor.class.getName());
            }
            return browserOptionInterceptors.get(0);
        }
    }

    private List<WebDriverPostInterceptor> getBrowserPostProcess(ExtensionContext context) {
        return getBrowserUtilExtensionList(context, WebDriverPostInterceptor.class);
    }

    private <T extends BrowserUtilExtension> List<T> getBrowserUtilExtensionList(ExtensionContext context, Class<T> utilType) {
        BrowserUtil methodBrowserUtil = context.getRequiredTestMethod().getAnnotation(BrowserUtil.class);
        List<T> browserExtensions = getBrowserUtilList(methodBrowserUtil, utilType);
        if (!browserExtensions.isEmpty()) {
            return browserExtensions;
        }
        BrowserUtil testBrowserUtil = context.getRequiredTestClass().getAnnotation(BrowserUtil.class);
        browserExtensions = getBrowserUtilList(testBrowserUtil, utilType);
        if (!browserExtensions.isEmpty()) {
            return browserExtensions;
        }
        BrowserUtil superClassBrowserUtil = AnnotationHelper.getFirstSuperClassAnnotation(context.getRequiredTestClass(), BrowserUtil.class);
        browserExtensions = getBrowserUtilList(superClassBrowserUtil, utilType);
        return browserExtensions;
    }

    private <T extends BrowserUtilExtension> List<T> getBrowserUtilList(BrowserUtil browserUtilAnnotation, Class<T> type) {
        if (browserUtilAnnotation != null) {
            return Arrays.stream(browserUtilAnnotation.value())
                    .filter(type::isAssignableFrom)
                    .map(extension -> {
                        try {
                            return (T) extension.getConstructor().newInstance();
                        } catch (Exception e) {
                            throw new RuntimeException("Can't instantiate " + extension.getName() + ".", e);
                        }
                    })
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}
