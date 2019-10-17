package at.willhaben.willtest.junit5;

import at.willhaben.willtest.exceptions.BrowserNotSupportedException;
import at.willhaben.willtest.util.AndroidOptions;
import at.willhaben.willtest.util.IOsOptions;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerOptions;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class OptionCombiner {

    private List<OptionModifier> optionModifiers;

    public OptionCombiner(List<OptionModifier> optionModifiers) {
        this.optionModifiers = optionModifiers;
    }

    @SuppressWarnings({"unchecked"})
    public <T extends MutableCapabilities> T getBrowserOptions(Class<T> optionType) {
        T options;
        try {
            options = optionType.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new BrowserNotSupportedException("The options class [" + optionType.getName() + "] can't be " +
                    "created. May this is not a valid option class for the supported browsers.");
        }
        for (OptionModifier modifier : optionModifiers) {
            if (optionType.isAssignableFrom(FirefoxOptions.class)) {
                options = modifier.modifyAllBrowsers(options);
                options = (T) modifier.modifyFirefoxOptions(((FirefoxOptions) options));
            } else if (optionType.isAssignableFrom(ChromeOptions.class)) {
                options = modifier.modifyAllBrowsers(options);
                options = (T) modifier.modifyChromeOptions(((ChromeOptions) options));
            } else if (optionType.isAssignableFrom(EdgeOptions.class)) {
                options = modifier.modifyAllBrowsers(options);
                options = (T) modifier.modifyEdgeOptions(((EdgeOptions) options));
            } else if (optionType.isAssignableFrom(InternetExplorerOptions.class)) {
                options = modifier.modifyAllBrowsers(options);
                options = (T) modifier.modifyInternetExplorerOptions(((InternetExplorerOptions) options));
            } else if (optionType.isAssignableFrom(AndroidOptions.class)) {
                options = modifier.modifyAllBrowsers(options);
                options = (T) modifier.modifyAndroidOptions(((AndroidOptions) options));
            } else if (optionType.isAssignableFrom(IOsOptions.class)) {
                options = modifier.modifyAllBrowsers(options);
                options = (T) modifier.modifyIOsOptions(((IOsOptions) options));
            } else {
                throw new BrowserNotSupportedException("The options class [" + optionType.getName() +
                        "] is not supported. There is no suitable Browser for this options.");
            }
        }
        return options;
    }
}
