package at.willhaben.willtest.misc.utils;

import org.openqa.selenium.By;

public class XPathBuilder {

    private StringBuilder sb = new StringBuilder();

    XPathBuilder() {}

    public XPathBuilder byTextOnly(String text) {
        sb.append("//*[normalize-space(text())='").append(text).append("']");
        return this;
    }

    public XPathBuilder byTagOnly(String tag) {
        sb.append("//").append(tag);
        return this;
    }

    public XPathBuilder byClassOnly(String className) {
        return byClassOnly(className, false);
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

    public XPathBuilder parentAndTag(String elementTag) {
        sb.append("/parent::").append(elementTag);
        return this;
    }

    public XPathBuilder followingSibling() {
        sb.append("/following-sibling::*");
        return this;
    }

    public XPathBuilder followingSiblingAndTag(String elementTag) {
        sb.append("/following-sibling::").append(elementTag);
        return this;
    }

    public XPathElementBuilder byClass(String className) {
        return byClass(className, false);
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
