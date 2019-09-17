package at.willhaben.willtest.proxy;

import at.willhaben.willtest.util.RemoteSelectionUtils;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.client.ClientUtil;
import org.openqa.selenium.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class SeleniumProxy extends Proxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeleniumProxy.class);
    public static final String PROXY_URL_PROPERTY_KEY = "browsermobProxyUrl";

    public SeleniumProxy(BrowserMobProxy proxy) {
        super();
        this.setProxyType(ProxyType.MANUAL);
        InetAddress connectableAddress = ClientUtil.getConnectableAddress();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(connectableAddress, proxy.getPort());
        String proxyUrl = System.getProperty(PROXY_URL_PROPERTY_KEY);
        String proxyStr;
        if (proxyUrl != null) {
            proxyStr = String.format("%s:%d", proxyUrl, inetSocketAddress.getPort());
            LOGGER.debug("Use the following proxy address for the browser to connect '" + proxyStr + "'. " +
                    "(selected from system property [" + PROXY_URL_PROPERTY_KEY + "])");
        } else if (!RemoteSelectionUtils.isRemote() && isOnMacOS()) {
            proxyStr = String.format("%s:%d", "localhost", inetSocketAddress.getPort());
        } else {
            proxyStr = String.format("%s:%d", ClientUtil.getConnectableAddress().getHostAddress(), inetSocketAddress.getPort());
            LOGGER.debug("Use the following proxy address for the browser to connect '" + proxyStr + "'. " +
                    "(automatically selected )");
        }
        this.setHttpProxy(proxyStr);
        this.setSslProxy(proxyStr);
    }

    private boolean isOnMacOS() {
        return System.getProperty("os.name").startsWith("Mac");
    }
}
