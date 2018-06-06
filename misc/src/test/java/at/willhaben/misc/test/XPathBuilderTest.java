package at.willhaben.misc.test;

import org.junit.Test;

import static at.willhaben.willtest.misc.utils.ByWh.xpath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class XPathBuilderTest {

    @Test
    public void testXpathBuilder() {
        String xpath = xpath().byClass("myClass").andId("myId").withText("myText").buildExpression();
        assertThat(xpath, is("//*[@class='myClass' and @id='myId' and normalize-space(text())='myText']"));

        xpath = xpath().byClassOnly("myClass").parent().buildExpression();
        assertThat(xpath, is("//*[@class='myClass']/parent::*"));

        xpath = xpath().byClassOnly("myClass").followingSiblingAndTag("div").buildExpression();
        assertThat(xpath, is("//*[@class='myClass']/following-sibling::div"));
    }

    @Test(expected = IllegalStateException.class)
    public void testDuplicatedSelector() {
        xpath().byClass("firstClass").andClass("secondClass");
    }

    @Test
    public void testClassOnlyContains() {
        String xpath = xpath().byClassOnly("myClass", true).buildExpression();
        assertThat(xpath, is("//*[contains(@class,'myClass')]"));

        xpath = xpath().byClass("myClass", true).withId("myId").buildExpression();
        assertThat(xpath, is("//*[contains(@class,'myClass') and @id='myId']"));
    }
}
