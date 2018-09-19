package at.willhaben.misc.test;

import at.willhaben.misc.test.pages.StaticPage;
import org.junit.Test;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TestCustomAnnotation {

    @Test
    public void test() {
        StaticPage staticPage = StaticPage.open(new PhantomJSDriver());
        assertThat(staticPage.getTextOfDivElement(), is("This text is in the div element"));
        assertThat(staticPage.getTextOfSpanElement(), is("This text is in the span element"));
        staticPage.select("ThirdOption");
        assertThat(staticPage.getSelectedValue(), is("ThirdOption"));
    }
}
