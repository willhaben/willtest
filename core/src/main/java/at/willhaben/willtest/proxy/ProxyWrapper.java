package at.willhaben.willtest.proxy;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarPostDataParam;
import net.lightbody.bmp.core.har.HarRequest;

import java.util.List;

public interface ProxyWrapper {

    BrowserMobProxy getProxy();

    List<HarRequest> getRequests();

    List<String> getRequestsUrls();

    List<HarEntry> getRequestsByUrl(String urlPattern);

    HarEntry getRequestByUrl(String urlPattern);

    List<HarPostDataParam> getRequestFormParamsByUrl(String urlPattern);

    void clearRequests();
}
