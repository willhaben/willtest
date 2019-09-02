package at.willhaben.willtest.test;

import at.willhaben.willtest.junit5.BrowserOptionInterceptor;
import at.willhaben.willtest.util.BrowserOptionProvider;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Collections;

class BrowserOptionsTest {

    @Test
    void mergeOptionsTest() {
        BrowserOptionProvider provider = new BrowserOptionProvider(Collections.singletonList(new Option1()));
        ChromeOptions finalChromeOptions = provider.getChromeOptions();
        System.out.println(finalChromeOptions.getCapability(ChromeOptions.CAPABILITY));
    }

    class Option1 extends BrowserOptionInterceptor {
        @Override
        public ChromeOptions getChromeOptions() {
            ChromeOptions options = super.getChromeOptions();
            options.setHeadless(true);
            return options;
        }
    }
}
