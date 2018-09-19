package at.willhaben.willtest.misc.utils;

import org.openqa.selenium.By;

public class XPathBuilder {

    private StringBuilder sb = new StringBuilder();

    public XPathBuilder byTextOnly(String text) {
        sb.append("//*[normalize-space(text())='").append(text).append("']");
        return this;
    }

    public XPathBuilder byTagOnly(String tag) {
        sb.append("//").append(tag);
        return this;
    }

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

    public XPathBuilder byClassOnly(String className) {
        return byClassOnly(className, true);
    }

    public XPathBuilder byClassOnly(String className, boolean onlyContain) {
        if(onlyContain) {
            sb.append("//*[contains(@class,'").append(className).append("')]");
        } else {
            sb.append("//*[@class='").append(className).append("']");
        }
        return this;
    }

    public XPathBuilder byIdOnly(String id) {
        sb.append("//*[@id='").append(id).append("']");
        return this;
    }

    public XPathBuilder parent() {
        sb.append("/parent::*");
        return this;
    }

    public XPathBuilder parent(String elementTag) {
        sb.append("/parent::").append(elementTag);
        return this;
    }

    public XPathBuilder followingSibling() {
        sb.append("/following-sibling::*");
        return this;
    }

    public XPathBuilder followingSibling(String elementTag) {
        sb.append("/following-sibling::").append(elementTag);
        return this;
    }

    public XPathBuilder nth(int nthElement) {
        sb.append("[").append(nthElement).append("]");
        return this;
    }

    public XPathElementBuilder byClass(String className) {
        return byClass(className, true);
    }

    public XPathElementBuilder byClass(String className, boolean onlyContain) {
        return new XPathElementBuilder(this).andClass(className, onlyContain);
    }

    public XPathElementBuilder byId(String id) {
        return new XPathElementBuilder(this).andId(id);
    }

    public XPathElementBuilder byText(String text) {
        return new XPathElementBuilder(this).andText(text);
    }

    public XPathElementBuilder byTag(String tag) {
        return new XPathElementBuilder(this).andTag(tag);
    }

    void addToXPath(String addText) {
        sb.append(addText);
    }

    public By build() {
        return By.xpath(sb.toString());
    }

    public String buildExpression() {
        return sb.toString();
    }
}
