/*
 * Ext GWT - Ext for GWT Copyright(c) 2007-2009, Ext JS, LLC. licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package org.talend.mdm.webapp.itemsbrowser2.client.widget;

import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;
import org.talend.mdm.webapp.itemsbrowser2.client.util.XmlHelper;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

public class ItemsFormPanel extends ContentPanel {

    private FormPanel content;

    private FormData formData;

    private ItemBean item;

    public ItemsFormPanel() {

        setHeaderVisible(false);
        setLayout(new FitLayout());

        content = new FormPanel();
        content.setFrame(false);
        content.setBodyBorder(false);
        content.setHeaderVisible(false);
        content.setScrollMode(Scroll.AUTO);

        add(content);

        formData = new FormData("-20");

    }
   

    public ItemsFormPanel(ItemBean item) {
        this();
        this.item = item;
    }


    public String getDisplayTitle() {
        String title="Item's form";
        if(item!=null)title=item.getConcept()+" "+item.getIds();
        return title;
    }

    public ItemBean getItem() {
        return item;
    }

    public void setItem(ItemBean item) {
        this.item = item;
    }
    
    public void showItem() {
        showItem(null,false);
    }

    public void showItem(ItemBean _item, boolean override) {
        if(override)setItem(_item);
        if (item != null) {

            content.removeAll();
            Document itemDoc = XmlHelper.parse(item.getItemXml());

            // go through item
            NodeList list = itemDoc.getDocumentElement().getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                // TODO check with schema
                Node node = list.item(i);
                String label = node.getNodeName();
                String value = XmlHelper.getFirstTextValue((Element) node);

                TextField<String> field = new TextField<String>();
                field.setFieldLabel(label);
                field.setAllowBlank(false);
                field.setValue(value);
                // field.setData("text", "Enter your fist name");
                content.add(field, formData);
            }

            content.layout();

        } else {
            content.removeAll();
        }
    }

}
