package org.talend.mdm.webapp.base.client.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.talend.mdm.webapp.base.client.model.ItemBaseModel;



public class LanguageUtil {

    private static LanguageUtil instance = null;

    private List<ItemBaseModel> languages = new ArrayList<ItemBaseModel>();

    private LinkedHashMap<String, ItemBaseModel> languageColumnMap = new LinkedHashMap<String, ItemBaseModel>();

    /**
     * Private constuctor
     */
    private LanguageUtil() {
        super();
    }

    /**
     * Get the unique instance of this class.
     */
    public static synchronized LanguageUtil getInstance() {
        if (instance == null) {
            instance = new LanguageUtil();
        }
        return instance;
    }

    public void setLanguags(List<ItemBaseModel> languages) {
        this.languages = languages;
    }

    public void setLanguageColumnMap(LinkedHashMap<String, ItemBaseModel> languageColumnMap) {
        this.languageColumnMap = languageColumnMap;
    }

    public List<ItemBaseModel> getLanguages() {
        return languages;
    }

    public LinkedHashMap<String, ItemBaseModel> getLanguageColumnMap() {
        return languageColumnMap;
    }

}
