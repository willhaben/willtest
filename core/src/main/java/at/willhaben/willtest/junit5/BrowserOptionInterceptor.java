package at.willhaben.willtest.junit5;

import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Objects;

public abstract class BrowserOptionInterceptor implements BrowserUtilExtension {

    public FirefoxOptions getFirefoxOptions() {
        return new FirefoxOptions();
    }

    public ChromeOptions getChromeOptions() {
        return new ChromeOptions();
    }

    public EdgeOptions getEdgeOptions() {
        return new EdgeOptions();
    }

    public InternetExplorerOptions getInternetExplorerOptions() {
        return new InternetExplorerOptions();
    }

    public DesiredCapabilities getAndroidCapabilities() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        String appPath = System.getProperty("appPath");
        String appActivity = System.getProperty("appActivity");
        String appWaitActivity = System.getProperty("appWaitActivity");
        capabilities.setCapability(MobileCapabilityType.APP, appPath);
        capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UiAutomator2");
        capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
        capabilities.setCapability("appActivity", appActivity);
        capabilities.setCapability(AndroidMobileCapabilityType.APP_WAIT_ACTIVITY, appWaitActivity);

        String deviceId = System.getProperty("deviceId");
        if (Objects.nonNull(deviceId)) {
            capabilities.setCapability(MobileCapabilityType.UDID, deviceId);
        }
        return capabilities;
    }

    public DesiredCapabilities getIOsCapabilities() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        String appPath = System.getProperty("appPath");
        String deviceName = System.getProperty("deviceName");
        String platformVersion = System.getProperty("platformVersion");
        capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, "");
        capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, platformVersion);
        capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, deviceName);
        capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "XCUITest");
        capabilities.setCapability(MobileCapabilityType.APP, appPath);
        return capabilities;
    }

}
