/*
 * Ext GWT - Ext for GWT Copyright(c) 2007-2009, Ext JS, LLC. licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package org.talend.mdm.webapp.itemsbrowser2.client.widget;

import java.util.Iterator;

import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemFormBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemFormLineBean;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;

public class ItemsFormPanel extends ContentPanel {

    private FormPanel content;

    private FormData formData;

    private ItemFormBean itemFormBean;

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
   

    public ItemsFormPanel(ItemFormBean itemFormBean) {
        this();
        this.itemFormBean = itemFormBean;
    }
    
    
    public void setItemFormBean(ItemFormBean itemFormBean) {
        this.itemFormBean = itemFormBean;
    }


    public String getDisplayTitle() {
        String title="Item's form";
        if(itemFormBean!=null)title=itemFormBean.getName();
        return title;
    }
    
    public void showItem() {
        showItem(null,false);
    }

    public void showItem(ItemFormBean _itemForm, boolean override) {
        if(override)setItemFormBean(_itemForm);
        if (_itemForm != null) {

            content.removeAll();
            
            for (Iterator<ItemFormLineBean> iterator = _itemForm.iteratorLine(); iterator.hasNext();) {
                ItemFormLineBean itemFormLine = (ItemFormLineBean) iterator.next();
                content.add(itemFormLine.genField(), formData);
            }
            
            content.layout();

        } else {
            content.removeAll();
        }
    }

}
