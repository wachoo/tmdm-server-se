package com.amalto.webapp.core.bean;

public class ComboItemBean implements Comparable<ComboItemBean> {

    private String value;

    private String text;

    public ComboItemBean(String value, String text) {
        super();
        this.text = text;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public int compareTo(ComboItemBean bean) {
        if (this == bean)
            return 0;

        int result = this.getValue().compareTo(bean.getValue());
        if (result != 0) {
            return result;
        }

        return this.getText().compareTo(bean.getText());
    }

    public boolean equals(ComboItemBean bean) {
        if (this == bean)
            return true;

        return this.getValue().equals(bean.getValue()) && this.getText().equals(bean.getText());
    }
}
