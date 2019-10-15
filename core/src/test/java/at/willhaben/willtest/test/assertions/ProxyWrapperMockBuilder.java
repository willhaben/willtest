package at.willhaben.willtest.test.assertions;

import at.willhaben.willtest.proxy.ProxyWrapper;
import at.willhaben.willtest.proxy.impl.ProxyWrapperImpl;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.core.har.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProxyWrapperMockBuilder {

    private ArrayList<HarEntry> harEntries = new ArrayList<>();

    public static ProxyWrapperMockBuilder builder() {
        return new ProxyWrapperMockBuilder();
    }

    public ProxyWrapperMockBuilder addHarEntry(String url, String body, Map<String, String> formData) {
        HarEntry entry = mock(HarEntry.class);
        HarRequest request = mock(HarRequest.class);
        when(request.getUrl()).thenReturn(url);
        if (formData != null) {
            List<HarPostDataParam> postDataParams = formData.keySet().stream()
                    .map(key -> {
                        HarPostDataParam postDataParam = mock(HarPostDataParam.class);
                        when(postDataParam.getName()).thenReturn(key);
                        when(postDataParam.getValue()).thenReturn(formData.get(key));
                        return postDataParam;
                    })
                    .collect(Collectors.toList());
            HarPostData postData = mock(HarPostData.class);
            when(postData.getParams()).thenReturn(postDataParams);
            when(request.getPostData()).thenReturn(postData);
        }
        when(entry.getRequest()).thenReturn(request);
        harEntries.add(entry);
        return this;
    }

    public ProxyWrapper build() {
        BrowserMobProxy mockedProxy = mock(BrowserMobProxy.class);
        Har mockedHar = mock(Har.class);
        HarLog mockedHarLog = mock(HarLog.class);
        when(mockedHarLog.getEntries()).thenReturn(harEntries);
        when(mockedHar.getLog()).thenReturn(mockedHarLog);
        when(mockedProxy.getHar()).thenReturn(mockedHar);
        return new ProxyWrapperImpl(mockedProxy);
    }
}
