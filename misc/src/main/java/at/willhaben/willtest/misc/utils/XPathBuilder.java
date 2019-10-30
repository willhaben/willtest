package at.willhaben.willtest.misc.utils;

import org.openqa.selenium.By;

public class XPathBuilder {

    private StringBuilder sb = new StringBuilder();

    /**
     * Searches for an element with the specified text <br>
     * XPath: //*[normalize-space(text())='TEXT']
     * @param text string with text to search
     * @return builder
     */
    public XPathBuilder byTextOnly(String text) {
        sb.append("//*[normalize-space(text())='").append(text).append("']");
        return this;
    }

    /**
     * Searches for an element by the html tag <br>
     * XPath: //TAG
     * @param tag string with tag to search
     * @return builder
     */
    public XPathBuilder byTagOnly(String tag) {
        sb.append("//").append(tag);
        return this;
    }

    /**
     * Modifies the last double slashed locator to a single slashed one. The element must be the direct
     * child of the parent locator to match. <br>
     * XPATH: //div[@id='ID']//a[@class='CLASS']  --->   //div[@id='ID']//a[@class='CLASS']
     * @return builder
     */
    public XPathBuilder directChild() {
        int singleSlashIndex = sb.lastIndexOf("/");
        int doubleSlashIndex = sb.lastIndexOf("//");
        if (singleSlashIndex > doubleSlashIndex + 1) {
            throw new IllegalStateException("The direct child operation can only be applied if the last xpath " +
                    "locator was a double slashed one. " +
                    "Actual locator: '" + buildExpression() + "'");
        }
        if(doubleSlashIndex == -1) {
            throw new IllegalStateException("The direct child operation can not be used as first locator. " +
                    "It modifies the last double slashed locator to a single slashed one. " +
                    "Actual locator: '" + buildExpression() + "'");
        }
        if (doubleSlashIndex == 0) {
            throw new IllegalStateException("The direct child operation can not be used on the first locator " +
                    "because this must be a double slashed one. " +
                    "Actual locator: '" + buildExpression() + "'");
        }
        sb.deleteCharAt(doubleSlashIndex);
        return this;
    }

    /**
     * Searches for an element by the css class name <br>
     * XPath: //*[contains(@class,'CLASSNAME')]
     * @param className css class to search
     * @return builder
     */
    public XPathBuilder byClassOnly(String className) {
        return byClassOnly(className, true);
    }

    /**
     * Searches for an element by the css class name <br>
     * XPath: //*[contains(@class,'CLASSNAME')]
     * @param className css class to search
     * @param onlyContain if false the css class must match exactly XPATH: //*[@class='CLASSNAME']
     * @return builder
     */
    public XPathBuilder byClassOnly(String className, boolean onlyContain) {
        if(onlyContain) {
            sb.append("//*[contains(@class,'").append(className).append("')]");
        } else {
            sb.append("//*[@class='").append(className).append("']");
        }
        return this;
    }

    /**
     * Searches for an element by the css id <br>
     * XPath: //*[@id='CLASSNAME']
     * @param id css id to search
     * @return builder
     */
    public XPathBuilder byIdOnly(String id) {
        sb.append("//*[@id='").append(id).append("']");
        return this;
    }

    /**
     * Get the parent element of the current locator <br>
     * XPATH: /parent::*
     * @return builder
     */
    public XPathBuilder parent() {
        sb.append("/parent::*");
        return this;
    }

    /**
     * Get the parent element with a specific tag of the current locator <br>
     * XPATH: /parent::TAG
     * @param elementTag name of the html tag
     * @return builder
     */
    public XPathBuilder parent(String elementTag) {
        sb.append("/parent::").append(elementTag);
        return this;
    }

    /**
     * Get the following-sibling element of the current locator <br>
     * XPATH: /following-sibling::*
     * @return builder
     */
    public XPathBuilder followingSibling() {
        sb.append("/following-sibling::*");
        return this;
    }

    /**
     * Get the following-sibling element with a specific tag of the current locator <br>
     * XPATH: /following-sibling::TAG
     * @param elementTag name of the html tag
     * @return builder
     */
    public XPathBuilder followingSibling(String elementTag) {
        sb.append("/following-sibling::").append(elementTag);
        return this;
    }

    /**
     * Appends the given array index to the locator <br>
     * XPATH: ...[NTH_ELEMENT]
     * @param nthElement index of the element
     * @return builder
     */
    public XPathBuilder nth(int nthElement) {
        sb.append("[").append(nthElement).append("]");
        return this;
    }

    /**
     * Searches for an element by css class name and provides the possibility to combine with other locator
     * @param className css class name
     * @return locator builder
     */
    public XPathElementBuilder byClass(String className) {
        return byClass(className, true);
    }

    public XPathElementBuilder byClass(String className, boolean onlyContain) {
        return new XPathElementBuilder(this).andClass(className, onlyContain);
    }

    /**
     * Searches for an element by css id name and provides the possibility to combine with other locator
     * @param id css id name
     * @return locator builder
     */
    public XPathElementBuilder byId(String id) {
        return new XPathElementBuilder(this).andId(id);
    }

    /**
     * Searches for an element by text and provides the possibility to combine with other locator
     * @param text text in the html element
     * @return locator builder
     */
    public XPathElementBuilder byText(String text) {
        return new XPathElementBuilder(this).andText(text);
    }

    /**
     * Searches for an element by the html tag and provides the possibility to combine with other locator
     * @param tag html tag name
     * @return locator builder
     */
    public XPathElementBuilder byTag(String tag) {
        return new XPathElementBuilder(this).andTag(tag);
    }

    void addToXPath(String addText) {
        sb.append(addText);
    }

    /**
     * Generates the selenium {@link By} locator
     * @return selenium locator
     */
    public By build() {
        return By.xpath(sb.toString());
    }

    public String buildExpression() {
        return sb.toString();
    }
}
