package at.willhaben.willtest.proxy;

import at.willhaben.willtest.junit5.OptionModifier;
import net.lightbody.bmp.BrowserMobProxy;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.CapabilityType;

public class ProxyOptionModifier implements OptionModifier {

    private BrowserMobProxy proxy;

    public ProxyOptionModifier(BrowserMobProxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public <T extends MutableCapabilities> T modifyAllBrowsers(T options) {
        options.setCapability(CapabilityType.PROXY, BrowserProxyBuilder.createSeleniumProxy(proxy));
        options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
        return options;
    }
}
