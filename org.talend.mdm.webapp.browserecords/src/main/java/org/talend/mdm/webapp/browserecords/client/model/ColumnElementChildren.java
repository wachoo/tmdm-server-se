package org.talend.mdm.webapp.browserecords.client.model;

import java.io.Serializable;
import java.util.List;

public class ColumnElementChildren implements Serializable {

    private int order;

    private String cssSnippet;

    private String bkColor;

    private String name;

    private String xpath;

    List<ColumnElementChildren> children;

    public ColumnElementChildren() {

    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getCssSnippet() {
        return cssSnippet;
    }

    public void setCssSnippet(String cssSnippet) {
        this.cssSnippet = cssSnippet;
    }

    public String getBkColor() {
        return bkColor;
    }

    public void setBkColor(String bkColor) {
        this.bkColor = bkColor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public List<ColumnElementChildren> getChildren() {
        return children;
    }

    public void setChildren(List<ColumnElementChildren> children) {
        this.children = children;
    }

}
