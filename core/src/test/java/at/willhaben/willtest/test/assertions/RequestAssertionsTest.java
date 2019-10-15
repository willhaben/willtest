package at.willhaben.willtest.test.assertions;

import at.willhaben.willtest.proxy.ProxyWrapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static at.willhaben.willtest.proxy.assertions.ProxyMatchers.requestAvailable;
import static at.willhaben.willtest.test.assertions.ProxyWrapperMockBuilder.builder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RequestAssertionsTest {

    private ProxyWrapper mockedProxyWrapper;

    @BeforeEach
    void setUp() {
        mockedProxyWrapper = builder()
                .addHarEntry("http://www.google.com", "mydata", Collections.singletonMap("[{\"jsonkey\":\"thisisthevalue\"}]", ""))
                .build();
    }

    @Test
    void testRequestAvailable() {
        assertThat(mockedProxyWrapper.getRequestsUrls(), requestAvailable(".*www\\.google\\.com"));
    }

    @Test
    void testAssertFormDataWithJson() {
        String json = mockedProxyWrapper.getRequestFormParamsByUrl(".*www\\.google\\.com").get(0).getName();
        String data = JsonPath.read(json, "$[0].jsonkey");
        assertThat(data, is("thisisthevalue"));
    }
}
