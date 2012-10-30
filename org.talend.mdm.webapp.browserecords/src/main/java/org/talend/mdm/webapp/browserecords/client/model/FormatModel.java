package org.talend.mdm.webapp.browserecords.client.model;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FormatModel implements IsSerializable {

    private String format;

    private Serializable object;

    private String language;

    public FormatModel() {

    }

    public FormatModel(String format, Serializable object, String language) {
        this.format = format;
        this.object = object;
        this.language = language;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Serializable getObject() {
        return object;
    }

    public void setObject(Serializable object) {
        this.object = object;
    }

    public String getLanguage() {
        return language == null ? "en" : language; //$NON-NLS-1$
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
