package org.talend.mdm.webapp.browserecords.client.model;

import java.io.Serializable;
import java.util.List;

public class ColumnElement implements Serializable {

    private String cssSnippet;

    private String jsSnippet;

    private String bkColor;

    private String foreColor;

    private String name;

    private String xpath;

    private List<ColumnElementChildren> children;

    public ColumnElement() {

    }

    public String getCssSnippet() {
        return cssSnippet;
    }

    public void setCssSnippet(String cssSnippet) {
        this.cssSnippet = cssSnippet;
    }

    public String getJsSnippet() {
        return jsSnippet;
    }

    public void setJsSnippet(String jsSnippet) {
        this.jsSnippet = jsSnippet;
    }

    public String getBkColor() {
        return bkColor;
    }

    
    public void setBkColor(String bkColor) {
        this.bkColor = bkColor;
    }

    public String getForeColor() {
        return foreColor;
    }

    public void setForeColor(String foreColor) {
        this.foreColor = foreColor;
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
