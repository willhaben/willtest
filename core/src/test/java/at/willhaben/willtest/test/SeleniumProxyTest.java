package at.willhaben.willtest.test;

import at.willhaben.willtest.proxy.SeleniumProxy;
import net.lightbody.bmp.BrowserMobProxy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SeleniumProxyTest {

    public static final String PROXY_URL = "http://ThisIsTheSetUrl";
    public static final int PROXY_PORT = 1234;

    static {
        System.setProperty(SeleniumProxy.PROXY_URL_PROPERTY_KEY, PROXY_URL);
    }

    private BrowserMobProxy proxy;

    @BeforeEach
    void setup() {
        proxy = mock(BrowserMobProxy.class);

        when(proxy.getPort()).thenReturn(PROXY_PORT);
    }

    @Test
    void testProxy() {
        SeleniumProxy seleniumProxy = new SeleniumProxy(proxy);
        assertThat(seleniumProxy.getHttpProxy(), is(PROXY_URL + ":" + PROXY_PORT));
    }
}
