package at.willhaben.misc.test;

import org.junit.Test;

import static at.willhaben.willtest.misc.utils.ByWh.xpath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class XPathBuilderTest {

    @Test
    public void testLocatorCombination() {
        String xpath = xpath().byClass("myClass").andId("myId").withText("myText").buildExpression();
        assertThat(xpath, is("//*[contains(@class,'myClass') and @id='myId' and normalize-space(text())='myText']"));

        xpath = xpath().byClassOnly("myClass").parent().buildExpression();
        assertThat(xpath, is("//*[contains(@class,'myClass')]/parent::*"));

        xpath = xpath().byClassOnly("myClass").followingSibling("div").buildExpression();
        assertThat(xpath, is("//*[contains(@class,'myClass')]/following-sibling::div"));

        xpath = xpath().byClass("myClass", false).withTag("myTag").nth(1).buildExpression();
        assertThat(xpath, is("//myTag[@class='myClass'][1]"));

        xpath = xpath().byClass("myClass", false).andText("myText").withTag("myTag").nth(1).buildExpression();
        assertThat(xpath, is("//myTag[@class='myClass' and normalize-space(text())='myText'][1]"));
    }

    @Test
    public void testClassLocator() {
        String xpath = xpath().byClassOnly("myClass").buildExpression();
        assertThat(xpath, is("//*[contains(@class,'myClass')]"));

        xpath = xpath().byClassOnly("myClass", false).buildExpression();
        assertThat(xpath, is("//*[@class='myClass']"));
    }

    @Test
    public void testIdLocator() {
        String xpath = xpath().byIdOnly("myId").buildExpression();
        assertThat(xpath, is("//*[@id='myId']"));
    }

    @Test
    public void testTagLocator() {
        String xpath = xpath().byTagOnly("myTag").buildExpression();
        assertThat(xpath, is("//myTag"));
    }

    @Test
    public void testTextLocator() {
        String xpath = xpath().byTextOnly("myText").buildExpression();
        assertThat(xpath, is("//*[normalize-space(text())='myText']"));
    }


    @Test(expected = IllegalStateException.class)
    public void testDuplicatedSelector() {
        xpath().byClass("firstClass").andClass("secondClass");
    }

    @Test
    public void testClassOnlyContains() {
        String xpath = xpath().byClassOnly("myClass").buildExpression();
        assertThat(xpath, is("//*[contains(@class,'myClass')]"));

        xpath = xpath().byClass("myClass").withId("myId").buildExpression();
        assertThat(xpath, is("//*[contains(@class,'myClass') and @id='myId']"));
    }

    @Test
    public void testFollowingSibling() {
        String xpath = xpath().followingSibling().buildExpression();
        assertThat(xpath, is("/following-sibling::*"));

        xpath = xpath().followingSibling("mytag").buildExpression();
        assertThat(xpath, is("/following-sibling::mytag"));
    }

    @Test
    public void testnthElement() {
        String xpath = xpath().byTagOnly("mytag").nth(2).buildExpression();
        assertThat(xpath, is("//mytag[2]"));
    }

    @Test
    public void testDirectChild() {
        String xpath = xpath().byClassOnly("myClass").byTagOnly("myTag").directChild().buildExpression();
        assertThat(xpath, is("//*[contains(@class,'myClass')]/myTag"));
    }

    @Test(expected = IllegalStateException.class)
    public void testDirectChildAsFirstLocator() {
        xpath().directChild();
    }

    @Test(expected = IllegalStateException.class)
    public void testDirectChildOnFirstLocator() {
        xpath().byClassOnly("myClass").directChild();
    }

    @Test(expected = IllegalStateException.class)
    public void testDirectChildOnSingleSlashedLocator() {
        xpath().byClassOnly("myClass").parent().directChild();
    }
}
