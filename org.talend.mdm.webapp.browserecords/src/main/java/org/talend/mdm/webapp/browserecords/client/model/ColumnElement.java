package org.talend.mdm.webapp.browserecords.client.model;

import java.io.Serializable;

public class ColumnElement implements Serializable {

    private int order;

    private String cssSnippet;

    private String jsSnippet;

    private String bkColor;

    private String foreColor;

    private String name;

    private String xpath;

    public ColumnElement() {

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
}
