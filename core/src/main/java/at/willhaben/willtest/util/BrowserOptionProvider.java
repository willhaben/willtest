package at.willhaben.willtest.util;

import at.willhaben.willtest.junit5.BrowserOptionInterceptor;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class BrowserOptionProvider extends BrowserOptionInterceptor {

    private List<BrowserOptionInterceptor> interceptors;

    public BrowserOptionProvider(List<BrowserOptionInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    @Override
    public FirefoxOptions getFirefoxOptions() {
        return super.getFirefoxOptions().merge(combine(BrowserOptionInterceptor::getFirefoxOptions));
    }

    @Override
    public ChromeOptions getChromeOptions() {
        return super.getChromeOptions().merge(combine(BrowserOptionInterceptor::getChromeOptions));
    }

    @Override
    public EdgeOptions getEdgeOptions() {
        return super.getEdgeOptions().merge(combine(BrowserOptionInterceptor::getEdgeOptions));
    }

    @Override
    public InternetExplorerOptions getInternetExplorerOptions() {
        return super.getInternetExplorerOptions().merge(combine(BrowserOptionInterceptor::getInternetExplorerOptions));
    }

    @Override
    public DesiredCapabilities getAndroidCapabilities() {
        return super.getAndroidCapabilities().merge(combine(BrowserOptionInterceptor::getChromeOptions));
    }

    @Override
    public DesiredCapabilities getIOsCapabilities() {
        return super.getIOsCapabilities().merge(combine(BrowserOptionInterceptor::getChromeOptions));
    }

    private Capabilities combine(Function<BrowserOptionInterceptor, Capabilities> getCaps) {
        Iterator<BrowserOptionInterceptor> optionIterator = interceptors.iterator();
        Capabilities parentCaps = getCaps.apply(optionIterator.next());
        while (optionIterator.hasNext()) {
            parentCaps = parentCaps.merge(getCaps.apply(optionIterator.next()));
        }
        return parentCaps;
    }
}
