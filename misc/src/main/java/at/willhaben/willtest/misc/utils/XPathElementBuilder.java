package at.willhaben.willtest.misc.utils;

import java.util.Objects;

public class XPathElementBuilder {
    private String tag;
    private String className;
    private boolean containsClass;
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
        return andClass(className, true);
    }

    public XPathElementBuilder andClass(String className, boolean onlyContain) {
        this.containsClass = onlyContain;
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
            if(containsClass) {//contains(@class,'description')]
                appendFunction(sb, "contains(@class,", className, ")");
            } else {
                appendKeyValue(sb, "@class", className);
            }
        }
        if(Objects.nonNull(id)) {
            appendKeyValue(sb, "@id", id);
        }
        if(Objects.nonNull(text)) {
            appendKeyValue(sb, "normalize-space(text())", text);
        }
        if(innerElementNumber != 0) {
            sb.append("]");
        }
        return sb.toString();
    }

    private void appendFunction(StringBuilder sb, String first, String value, String end) {
        if(innerElementNumber == 0) {
            sb.append("[").append(first).append("'").append(value).append("'").append(end);
        } else {
            sb.append(" and ").append(first).append("'").append(value).append("'").append(end);
        }
        innerElementNumber++;
    }

    private void appendKeyValue(StringBuilder sb, String first, String value) {
        if(innerElementNumber == 0) {
            sb.append("[").append(first).append("='").append(value).append("'");
        } else {
            sb.append(" and ").append(first).append("='").append(value).append("'");
        }
        innerElementNumber++;
    }

}
