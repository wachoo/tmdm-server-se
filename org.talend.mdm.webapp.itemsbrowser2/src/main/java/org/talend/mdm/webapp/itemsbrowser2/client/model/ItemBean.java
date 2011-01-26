// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.itemsbrowser2.client.model;

import java.util.List;

import org.talend.mdm.webapp.itemsbrowser2.client.util.XmlHelper;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.xml.client.Document;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class ItemBean extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = -1733441307059646670L;

    private String concept;

    private String ids;

    private String itemXml;

    /**
     * DOC HSHU ItemBean constructor comment.
     */
    @Deprecated
    public ItemBean() {
        // TODO Auto-generated constructor stub
    }

    public ItemBean(String concept, String ids, String itemXml) {
        super();
        this.concept = concept;
        this.ids = ids;
        this.itemXml = itemXml;

    }

    public void dynamicAssemble(ViewBean viewBean) {
        if (this.itemXml != null) {
            Document docXml = XmlHelper.parse(itemXml);

            List<String> viewables = viewBean.getViewableXpaths();
            for (String viewable : viewables) {
                String value = XmlHelper.getTextValueFromXpath(docXml, viewable);
                set(viewable, value);
            }

        }
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getItemXml() {
        return itemXml;
    }

    public void setItemXml(String itemXml) {
        this.itemXml = itemXml;
    }

    /**
     * DOC HSHU Comment method "getPk".
     */
    public String getPk() {
        String pk = concept;

        if (ids != null) {
            pk += " " + ids;
        }

        return pk;
    }

    @Override
    public String toString() {
        return "ItemBean [concept=" + concept + ", ids=" + ids + ", itemXml=" + itemXml + "]";
    }

}
