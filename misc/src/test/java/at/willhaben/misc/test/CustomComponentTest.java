package at.willhaben.misc.test;

import at.willhaben.misc.test.pages.StaticPage;
import at.willhaben.misc.test.util.HeadlessBrowserConfig;
import at.willhaben.willtest.junit5.BrowserUtil;
import at.willhaben.willtest.junit5.extensions.DriverParameterResolver;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ISelect;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@Tag("realBrowser")

@ExtendWith(DriverParameterResolver.class)
@BrowserUtil(HeadlessBrowserConfig.class)
class CustomComponentTest {

    @Test
    void test(WebDriver driver) {
        StaticPage staticPage = StaticPage.open(driver);
        assertThat(staticPage.getTextOfDivElement(), is("This text is in the div element"));
        assertThat(staticPage.getTextOfSpanElement(), is("This text is in the span element"));
        assertThat(staticPage.getSelectedValue(), is("FirstOption"));
        staticPage.select("ThirdOption");
        assertThat(staticPage.getSelectedValue(), is("ThirdOption"));
        List<ISelect> selectList = staticPage.getSelectList();
        assertThat(selectList.get(0).getFirstSelectedOption().getText().trim(), is("First1Option"));
        selectList.get(0).selectByVisibleText("Second1Option");
        assertThat(selectList.get(0).getFirstSelectedOption().getText().trim(), is("Second1Option"));
        assertThat(selectList.get(1).getFirstSelectedOption().getText().trim(), is("First2Option"));
        assertThat(selectList, Matchers.hasSize(2));
    }
}