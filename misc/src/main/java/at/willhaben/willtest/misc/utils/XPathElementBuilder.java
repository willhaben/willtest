package at.willhaben.willtest.misc.utils;

import java.util.Objects;

public class XPathElementBuilder {
    private String tag;
    private String className;
    private String id;
    private String text;
    private int innerElementNumber = 0;
    private XPathBuilder rootBuilder;

    public XPathElementBuilder(XPathBuilder rootBuilder) {
        this.rootBuilder = rootBuilder;
    }

    public XPathElementBuilder andTag(String tagName) {
        if(Objects.nonNull(this.tag)) {
            throw new IllegalStateException("Tag is already set to '" + this.tag + "' for this xpath element.");
        }
        this.tag = tagName;
        return this;
    }

    public XPathElementBuilder andId(String id) {
        if(Objects.nonNull(this.id)) {
            throw new IllegalStateException("Id is already set to '" + this.id + "' for this xpath element.");
        }
        this.id = id;
        return this;
    }

    public XPathElementBuilder andClass(String className) {
        if(Objects.nonNull(this.className)) {
            throw new IllegalStateException("Class is already set to '" + this.className + "' for this xpath element.");
        }
        this.className = className;
        return this;
    }

    public XPathElementBuilder andText(String text) {
        if(Objects.nonNull(this.text)) {
            throw new IllegalStateException("Text is already set to '" + this.text + "' for this xpath element.");
        }
        this.text = text;
        return this;
    }

    public XPathBuilder withTag(String tag) {
        andTag(tag);
        rootBuilder.addToXPath(build());
        return rootBuilder;
    }

    public XPathBuilder withId(String id) {
        andId(id);
        rootBuilder.addToXPath(build());
        return rootBuilder;
    }

    public XPathBuilder withClass(String className) {
        andClass(className);
        rootBuilder.addToXPath(build());
        return rootBuilder;
    }

    public XPathBuilder withText(String text) {
        andText(text);
        rootBuilder.addToXPath(build());
        return rootBuilder;
    }


    String build() {
        StringBuilder sb = new StringBuilder("//");
        if(Objects.nonNull(tag)) {
            sb.append(tag);
        } else {
            sb.append("*");
        }
        if(Objects.nonNull(className)) {
            appendInner(sb, "@class", className);
        }
        if(Objects.nonNull(id)) {
            appendInner(sb, "@id", id);
        }
        if(Objects.nonNull(text)) {
            appendInner(sb, "normalize-space(text())", text);
        }
        if(innerElementNumber != 0) {
            sb.append("]");
        }
        return sb.toString();
    }

    private void appendInner(StringBuilder sb, String key, String value) {
        if(innerElementNumber == 0) {
            sb.append("[").append(key).append("='").append(value).append("'");
        } else {
            sb.append(" and ").append(key).append("='").append(value).append("'");
        }
        innerElementNumber++;
    }

}