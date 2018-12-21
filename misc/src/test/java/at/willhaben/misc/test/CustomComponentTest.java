package at.willhaben.misc.test;

import at.willhaben.misc.test.pages.StaticPage;
import at.willhaben.misc.test.util.category.PhantomTest;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.ISelect;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@Category(PhantomTest.class)
public class CustomComponentTest {

    WebDriver driver;

    @Before
    public void setUpDriver() {
        driver = new PhantomJSDriver();
    }

    @Test
    public void test() {
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