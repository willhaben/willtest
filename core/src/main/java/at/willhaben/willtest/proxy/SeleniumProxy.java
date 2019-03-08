package at.willhaben.willtest.proxy;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.client.ClientUtil;
import org.openqa.selenium.Proxy;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class SeleniumProxy extends Proxy {

    public SeleniumProxy(BrowserMobProxy proxy) {
        super();
        this.setProxyType(ProxyType.MANUAL);
        InetAddress connectableAddress = ClientUtil.getConnectableAddress();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(connectableAddress, proxy.getPort());
        String proxyStr = String.format("%s:%d", ClientUtil.getConnectableAddress().getHostAddress(), inetSocketAddress.getPort());
        this.setHttpProxy(proxyStr);
        this.setSslProxy(proxyStr);
    }
}
