package at.willhaben.willtest.junit5.extensions;

import at.willhaben.willtest.junit5.BrowserOptionInterceptor;
import at.willhaben.willtest.junit5.OptionCombiner;
import at.willhaben.willtest.junit5.OptionModifier;
import at.willhaben.willtest.junit5.WebDriverPostInterceptor;
import at.willhaben.willtest.proxy.BrowserProxyBuilder;
import at.willhaben.willtest.proxy.ProxyWrapper;
import at.willhaben.willtest.proxy.impl.ProxyWrapperImpl;
import at.willhaben.willtest.util.*;
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
            return driverCreatedInBeforeEach.get();
        } else if (parameterType.isAssignableFrom(ProxyWrapper.class) && proxyCreatedInBeforeEach.isPresent()) {
            return proxyCreatedInBeforeEach.get();
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
            List<OptionModifier> modifiers = getBrowserOptionModifiers(extensionContext);
            WebDriver driver = createDriver(optionProvider, modifiers, driverPostInterceptorList);
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

    private WebDriver createDriver(BrowserOptionInterceptor options, List<OptionModifier> modifiers, List<WebDriverPostInterceptor> driverPostInterceptorList) {
        String seleniumHub = System.getProperty("seleniumHub");
        FirefoxOptions firefoxOptions;
        ChromeOptions chromeOptions;
        EdgeOptions edgeOptions;
        InternetExplorerOptions internetExplorerOptions;
        // change this to android and ios options class when the old options implementation is removed
        DesiredCapabilities androidOptions;
        DesiredCapabilities iOsOptions;

        // use new optionmodifiers if the list is not empty
        if (modifiers.size() > 0) {
            OptionCombiner optionCombiner = new OptionCombiner(modifiers);
            firefoxOptions = optionCombiner.getBrowserOptions(FirefoxOptions.class);
            chromeOptions = optionCombiner.getBrowserOptions(ChromeOptions.class);
            edgeOptions = optionCombiner.getBrowserOptions(EdgeOptions.class);
            internetExplorerOptions = optionCombiner.getBrowserOptions(InternetExplorerOptions.class);
            androidOptions = optionCombiner.getBrowserOptions(AndroidOptions.class);
            iOsOptions = optionCombiner.getBrowserOptions(IOsOptions.class);
        } else {
            firefoxOptions = options.getFirefoxOptions();
            chromeOptions = options.getChromeOptions();
            edgeOptions = options.getEdgeOptions();
            internetExplorerOptions = options.getInternetExplorerOptions();
            androidOptions = options.getAndroidCapabilities();
            iOsOptions = options.getIOsCapabilities();
        }

        WebDriver driver;
        if (PlatformUtils.isAndroid()) {
            if (RemoteSelectionUtils.isRemote()) {
                URL seleniumHubUrl = convertSeleniumHubToURL(seleniumHub);
                driver = new AndroidDriver<>(seleniumHubUrl, androidOptions);
            } else {
                driver = new AndroidDriver<>(androidOptions);
            }
        } else if (PlatformUtils.isIOS()) {
            if (RemoteSelectionUtils.isRemote()) {
                URL seleniumHubUrl = convertSeleniumHubToURL(seleniumHub);
                driver = new IOSDriver<>(seleniumHubUrl, iOsOptions);
            } else {
                driver = new IOSDriver<>(iOsOptions);
            }
            ((AppiumDriver) driver).context("NATIVE_APP");
        } else if (PlatformUtils.isLinux() || PlatformUtils.isWindows()) {
            if (RemoteSelectionUtils.isRemote()) {
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

    @Deprecated
    private BrowserOptionInterceptor getBrowserOptionInterceptor(ExtensionContext context, DesiredCapabilities fixedCapabilities) {
        List<BrowserOptionInterceptor> browserOptionInterceptors = getBrowserUtilExtensionList(context, BrowserOptionInterceptor.class, false);
        return new BrowserOptionProvider(browserOptionInterceptors, fixedCapabilities);
    }

    private List<OptionModifier> getBrowserOptionModifiers(ExtensionContext context) {
        return getBrowserUtilExtensionList(context, OptionModifier.class, false);
    }

    private List<WebDriverPostInterceptor> getBrowserPostProcess(ExtensionContext context) {
        return getBrowserUtilExtensionList(context, WebDriverPostInterceptor.class, true);
    }
}
