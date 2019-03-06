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
    private DesiredCapabilities fixedCapabilities;

    public BrowserOptionProvider(List<BrowserOptionInterceptor> interceptors, DesiredCapabilities fixedCapabilities) {
        this.interceptors = interceptors;
        this.fixedCapabilities = fixedCapabilities;
    }

    public BrowserOptionProvider(List<BrowserOptionInterceptor> interceptors) {
        this.interceptors = interceptors;
        this.fixedCapabilities = new DesiredCapabilities();
    }

    @Override
    public FirefoxOptions getFirefoxOptions() {
        return super.getFirefoxOptions()
                .merge(combine(BrowserOptionInterceptor::getFirefoxOptions))
                .merge(fixedCapabilities);
    }

    @Override
    public ChromeOptions getChromeOptions() {
        return super.getChromeOptions()
                .merge(combine(BrowserOptionInterceptor::getChromeOptions))
                .merge(fixedCapabilities);
    }

    @Override
    public EdgeOptions getEdgeOptions() {
        return super.getEdgeOptions()
                .merge(combine(BrowserOptionInterceptor::getEdgeOptions))
                .merge(fixedCapabilities);
    }

    @Override
    public InternetExplorerOptions getInternetExplorerOptions() {
        return super.getInternetExplorerOptions()
                .merge(combine(BrowserOptionInterceptor::getInternetExplorerOptions))
                .merge(fixedCapabilities);
    }

    @Override
    public DesiredCapabilities getAndroidCapabilities() {
        return super.getAndroidCapabilities()
                .merge(combine(BrowserOptionInterceptor::getAndroidCapabilities))
                .merge(fixedCapabilities);
    }

    @Override
    public DesiredCapabilities getIOsCapabilities() {
        return super.getIOsCapabilities()
                .merge(combine(BrowserOptionInterceptor::getIOsCapabilities))
                .merge(fixedCapabilities);
    }

    private Capabilities combine(Function<BrowserOptionInterceptor, Capabilities> getCaps) {
        Iterator<BrowserOptionInterceptor> optionIterator = interceptors.iterator();
        Capabilities parentCaps;
        if (optionIterator.hasNext()) {
            parentCaps = getCaps.apply(optionIterator.next());
        } else {
            parentCaps = new DesiredCapabilities();
        }
        while (optionIterator.hasNext()) {
            parentCaps = parentCaps.merge(getCaps.apply(optionIterator.next()));
        }
        return parentCaps;
    }
}
