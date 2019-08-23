package at.willhaben.willtest.junit5.extensions;

import at.willhaben.willtest.junit5.BrowserOptionInterceptor;
import at.willhaben.willtest.junit5.WebDriverPostInterceptor;
import at.willhaben.willtest.proxy.BrowserProxyBuilder;
import at.willhaben.willtest.proxy.ProxyWrapper;
import at.willhaben.willtest.proxy.impl.ProxyWrapperImpl;
import at.willhaben.willtest.util.BrowserOptionProvider;
import at.willhaben.willtest.util.BrowserSelectionUtils;
import at.willhaben.willtest.util.PlatformUtils;
import at.willhaben.willtest.util.RemoteSelectionUtils;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import net.lightbody.bmp.BrowserMobProxy;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static at.willhaben.willtest.util.AnnotationHelper.getBrowserUtilExtensionList;


public class DriverParameterResolver implements ParameterResolver, AfterEachCallback, AfterAllCallback {

    public static final String DRIVER_KEY = "wh-webDriver";
    private static final String BEFOREALL_DRIVER_KEY = "wh-beforeall-webDriver";
    private static final String PROXY_KEY = "wh-proxy";
    private static final Logger LOGGER = LoggerFactory.getLogger(DriverParameterResolver.class);

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
            return driverCreatedInBeforeEach;
        } else if (parameterType.isAssignableFrom(ProxyWrapper.class) && proxyCreatedInBeforeEach.isPresent()) {
            return proxyCreatedInBeforeEach;
        } else if (parameterType.isAssignableFrom(WebDriver.class)) {
            DesiredCapabilities fixedCapabilities = new DesiredCapabilities();
            if (shouldStartProxy(extensionContext)) {
                BrowserMobProxy proxy = BrowserProxyBuilder.builder()
                        .startProxy();
                fixedCapabilities.setCapability(CapabilityType.PROXY, BrowserProxyBuilder.createSeleniumProxy(proxy));
                fixedCapabilities.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
                getStore(extensionContext).put(PROXY_KEY, new ProxyWrapperImpl(proxy));
            }
            BrowserOptionInterceptor optionProvider = getBrowserOptionInterceptor(extensionContext, fixedCapabilities);
            List<WebDriverPostInterceptor> driverPostInterceptorList = getBrowserPostProcess(extensionContext);
            WebDriver driver = createDriver(optionProvider, driverPostInterceptorList);
            if (extensionContext.getTestMethod().isPresent()) {
                getStore(extensionContext).put(DRIVER_KEY, driver);
            } else {
                getStore(extensionContext).put(BEFOREALL_DRIVER_KEY, driver);
            }
            return driver;
        } else if (parameterType.isAssignableFrom(ProxyWrapper.class)) {
            return getStore(extensionContext).get(PROXY_KEY, ProxyWrapper.class);
        } else {
            return null;
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
        return context.getStore(ExtensionContext.Namespace.create(DriverParameterResolver.class));
    }

    public static Optional<WebDriver> getDriverFromStore(ExtensionContext context, String driverKey) {
        return Optional.ofNullable(getStore(context).get(driverKey, WebDriver.class));
    }

    public static Optional<ProxyWrapper> getProxyFromStore(ExtensionContext context) {
        return Optional.ofNullable(getStore(context).get(PROXY_KEY, ProxyWrapper.class));
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

    private BrowserOptionInterceptor getBrowserOptionInterceptor(ExtensionContext context, DesiredCapabilities fixedCapabilities) {
        List<BrowserOptionInterceptor> browserOptionInterceptors = getBrowserUtilExtensionList(context, BrowserOptionInterceptor.class, false);
        return new BrowserOptionProvider(browserOptionInterceptors, fixedCapabilities);
    }

    private List<WebDriverPostInterceptor> getBrowserPostProcess(ExtensionContext context) {
        return getBrowserUtilExtensionList(context, WebDriverPostInterceptor.class, true);
    }
}
