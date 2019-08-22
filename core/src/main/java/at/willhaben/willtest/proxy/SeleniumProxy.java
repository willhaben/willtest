package at.willhaben.willtest.proxy;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.client.ClientUtil;
import org.openqa.selenium.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class SeleniumProxy extends Proxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeleniumProxy.class);

    public SeleniumProxy(BrowserMobProxy proxy) {
        super();
        this.setProxyType(ProxyType.MANUAL);
        InetAddress connectableAddress = ClientUtil.getConnectableAddress();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(connectableAddress, proxy.getPort());
        String proxyStr = String.format("%s:%d", ClientUtil.getConnectableAddress().getHostAddress(), inetSocketAddress.getPort());
        LOGGER.debug("Use the following proxy address for the Firefox to connect '" + proxyStr + "'.");
        this.setHttpProxy(proxyStr);
        this.setSslProxy(proxyStr);
    }
}
