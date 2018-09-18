package at.willhaben.misc.test;

import org.junit.Test;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

public class TestCustomAnnotation {

    @Test
    public void test() {
        StaticPage staticPage = StaticPage.open(new PhantomJSDriver());
        assertThat(staticPage.getTextOfTestElement(), containsString("That should be in the text"));
    }
}
