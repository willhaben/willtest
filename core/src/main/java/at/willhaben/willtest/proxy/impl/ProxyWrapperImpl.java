package at.willhaben.willtest.proxy.impl;

import at.willhaben.willtest.proxy.ProxyWrapper;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarPostDataParam;
import net.lightbody.bmp.core.har.HarRequest;

import java.util.List;
import java.util.stream.Collectors;

public class ProxyWrapperImpl implements ProxyWrapper {

    private BrowserMobProxy proxy;

    public ProxyWrapperImpl(BrowserMobProxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public BrowserMobProxy getProxy() {
        return proxy;
    }

    @Override
    public List<HarRequest> getRequests() {
        return getEntries()
                .stream()
                .map(HarEntry::getRequest)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getRequestsUrls() {
        return getRequests().stream()
                .map(HarRequest::getUrl)
                .collect(Collectors.toList());
    }

    @Override
    public List<HarEntry> getRequestsByUrl(String urlPattern) {
        return getEntries().stream()
                .filter(entry -> entry.getRequest().getUrl().matches(urlPattern))
                .collect(Collectors.toList());
    }

    @Override
    public HarEntry getRequestByUrl(String urlPattern) {
        List<HarEntry> matchedEntries = getRequestsByUrl(urlPattern);
        if (matchedEntries.size() == 1) {
            return matchedEntries.get(0);
        } else if (matchedEntries.size() == 0) {
            throw new RuntimeException("No request match the given pattern '" + urlPattern + "'.");
        } else {
            List<String> matchedUrlList = matchedEntries.stream()
                    .map(entry -> entry.getRequest().getUrl())
                    .collect(Collectors.toList());
            throw new RuntimeException("Multiple request match the given pattern '" + urlPattern + "'. " + matchedUrlList.toString());
        }
    }

    @Override
    public List<HarPostDataParam> getRequestFormParamsByUrl(String urlPattern) {
        return getRequestByUrl(urlPattern)
                .getRequest()
                .getPostData()
                .getParams();
    }

    @Override
    public void clearRequests() {
        proxy.newHar();
    }

    private List<HarEntry> getEntries() {
        return proxy.getHar()
                .getLog()
                .getEntries();
    }
}
