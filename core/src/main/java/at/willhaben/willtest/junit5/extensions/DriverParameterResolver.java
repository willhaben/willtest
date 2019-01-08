package at.willhaben.willtest.junit5.extensions;

import at.willhaben.willtest.junit5.BrowserOptionInterceptor;
import at.willhaben.willtest.junit5.DefaultBrowserOptionInterceptor;
import at.willhaben.willtest.junit5.WebDriverPostInterceptor;
import at.willhaben.willtest.util.BrowserOptionProvider;
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
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static at.willhaben.willtest.util.AnnotationHelper.getBrowserUtilExtensionList;


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
        BrowserOptionInterceptor optionProvider = getBrowserOptionInterceptor(extensionContext);
        List<WebDriverPostInterceptor> driverPostInterceptorList = getBrowserPostProcess(extensionContext);
        WebDriver driver = createDriver(optionProvider, driverPostInterceptorList);
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

    private WebDriver createDriver(BrowserOptionInterceptor options, List<WebDriverPostInterceptor> driverPostInterceptorList) {
        String seleniumHub = System.getProperty("seleniumHub");
        WebDriver driver;
        if (PlatformUtils.isAndroid()) {
            DesiredCapabilities androidCaps = options.getAndroidCapabilities();
            if (RemoteSelectionUtils.isRemote()) {
                URL seleniumHubUrl = convertSeleniumHubToURL(seleniumHub);
                driver = new AndroidDriver<>(seleniumHubUrl, androidCaps);
            } else {
                driver = new AndroidDriver<>(androidCaps);
            }
        } else if (PlatformUtils.isIOS()) {
            DesiredCapabilities iosCaps = options.getIOsCapabilities();
            if (RemoteSelectionUtils.isRemote()) {
                URL seleniumHubUrl = convertSeleniumHubToURL(seleniumHub);
                driver = new IOSDriver<>(seleniumHubUrl, iosCaps);
            } else {
                driver = new IOSDriver<>(iosCaps);
            }
            ((AppiumDriver) driver).context("NATIVE_APP");
        } else if (PlatformUtils.isLinux() || PlatformUtils.isWindows()) {
            if (RemoteSelectionUtils.isRemote()) {
                URL seleniumHubUrl = convertSeleniumHubToURL(seleniumHub);
                RemoteWebDriver remoteDriver;
                if (BrowserSelectionUtils.isFirefox()) {
                    remoteDriver = new RemoteWebDriver(seleniumHubUrl, options.getFirefoxOptions());
                } else if (BrowserSelectionUtils.isChrome()) {
                    remoteDriver = new RemoteWebDriver(seleniumHubUrl, options.getChromeOptions());
                } else if (BrowserSelectionUtils.isIE()) {
                    remoteDriver = new RemoteWebDriver(seleniumHubUrl, options.getInternetExplorerOptions());
                } else if (BrowserSelectionUtils.isEdge()) {
                    remoteDriver = new RemoteWebDriver(seleniumHubUrl, options.getInternetExplorerOptions());
                } else {
                    throw new RuntimeException("The specified browser '" + BrowserSelectionUtils.getBrowser() + "' is not supported");
                }
                remoteDriver.setFileDetector(new LocalFileDetector());
                driver = remoteDriver;
            } else {
                if (BrowserSelectionUtils.isFirefox()) {
                    driver = new FirefoxDriver(options.getFirefoxOptions());
                    //Could be replaced by Boolean.getBoolean("maximizeFF"), which is the same
                } else if (BrowserSelectionUtils.isChrome()) {
                    driver = new ChromeDriver(options.getChromeOptions());
                } else if (BrowserSelectionUtils.isIE()) {
                    driver = new InternetExplorerDriver(options.getInternetExplorerOptions());
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
        List<BrowserOptionInterceptor> browserOptionInterceptors = getBrowserUtilExtensionList(context, BrowserOptionInterceptor.class, false);
        if (browserOptionInterceptors.isEmpty()) {
            return new DefaultBrowserOptionInterceptor();
        }
        return new BrowserOptionProvider(browserOptionInterceptors);
    }

    private List<WebDriverPostInterceptor> getBrowserPostProcess(ExtensionContext context) {
        return getBrowserUtilExtensionList(context, WebDriverPostInterceptor.class, true);
    }

}
