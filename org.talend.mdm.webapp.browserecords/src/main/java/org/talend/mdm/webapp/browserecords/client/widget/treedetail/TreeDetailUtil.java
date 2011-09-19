package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.util.LabelUtil;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.widget.ForeignKeyFieldList;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;
import org.talend.mdm.webapp.browserecords.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * TreeDetail tool class
 */
public class TreeDetailUtil {

    public static Widget createWidget(ItemNodeModel itemNode, ViewBean viewBean, ClickHandler h) {

        HorizontalPanel hp = new HorizontalPanel();
        // create Field
        String xPath = itemNode.getBindingPath();
        TypeModel typeModel = viewBean.getBindingEntityModel().getMetaDataTypes().get(xPath);
        String dynamicLabel = typeModel.getLabel(Locale.getLanguage());
        HTML label = new HTML();
        String html = itemNode.getDescription();
        
        if(LabelUtil.isDynamicLabel(dynamicLabel)) {
        	html = itemNode.getDynamicLabel();
        }
        
        if (itemNode.isKey() || typeModel.getMinOccurs() >= 1)
            html = html + "<span style=\"color:red\"> *</span>"; //$NON-NLS-1$
        label.setHTML(html);
        hp.add(label);
        if (typeModel.isSimpleType()
                || (!typeModel.isSimpleType() && ((ComplexTypeModel) typeModel).getReusableComplexTypes().size() > 0)) {
            // if (typeModel.getName().equals("Family")) {
            // typeModel.setMaxOccurs(15);
            // typeModel.setMinOccurs(1);
            // }
            if (typeModel.getForeignkey() != null && typeModel.getMaxOccurs() > 1) {// FK list
                ForeignKeyFieldList fkList = new ForeignKeyFieldList(itemNode, typeModel);
                //fkList.setSize("400px", "200px"); //$NON-NLS-1$ //$NON-NLS-2$
                hp.add(fkList);
            } else {
                Field<?> field = TreeDetailGridFieldCreator.createField(itemNode, typeModel, Locale.getLanguage());
                field.setWidth(260);
                hp.add(field);
            }

            Image addNodeImg = new Image("/talendmdm/secure/img/genericUI/add.png"); //$NON-NLS-1$
            addNodeImg.getElement().getStyle().setMarginLeft(5.0, Unit.PX);
            Image removeNodeImg = new Image("/talendmdm/secure/img/genericUI/delete.png"); //$NON-NLS-1$
            removeNodeImg.getElement().getStyle().setMarginLeft(5.0, Unit.PX);
            if ((typeModel.getMaxOccurs() < 0 || typeModel.getMaxOccurs() > 1)
                    && typeModel.getForeignkey() == null) {
                hp.add(addNodeImg);
                hp.add(removeNodeImg);
            }

            hp.setCellWidth(label, "200px"); //$NON-NLS-1$

        }
        return hp;
    }
}
