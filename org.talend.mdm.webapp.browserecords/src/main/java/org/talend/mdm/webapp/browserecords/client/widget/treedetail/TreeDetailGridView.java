package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridView;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.Joint;
import com.google.gwt.user.client.ui.AbstractImagePrototype;


public class TreeDetailGridView extends TreeGridView {

    public String getWidgetTemplate(ModelData m, String id, String text, AbstractImagePrototype icon, boolean checkable,
            Joint joint, int level) {

        StringBuffer sb = new StringBuffer();
        sb.append("<div unselectable=\"on\" id=\""); //$NON-NLS-1$
        sb.append(id);
        sb.append("\" class=\"x-tree3-node\">");
        // jumping content when inserting in column with cell widget the column
        // extra width fixes
        sb.append("<div unselectable=\"on\" class=\"x-tree3-el\" style=\"width: 1000px;height: auto;\">");

        sb.append("<table cellpadding=0 cellspacing=0><tr><td>");

        String h = "";
        switch (joint) {
        case COLLAPSED:
            h = tree.getStyle().getJointCollapsedIcon().getHTML();
            break;
        case EXPANDED:
            h = tree.getStyle().getJointExpandedIcon().getHTML();
            break;
        default:
            h = "<img src=\"" + GXT.BLANK_IMAGE_URL + "\" style='width: 16px'>";
        }

        sb.append("</td><td><img src=\"");
        sb.append(GXT.BLANK_IMAGE_URL);
        sb.append("\" style=\"height: 18px; width: ");
        sb.append(level * 18);
        sb.append("px;\" /></td><td  class='x-tree3-el-jnt'>");
        sb.append(h);
        if (checkable) {
            sb.append(GXT.IMAGES.unchecked().getHTML());
        } else {
            sb.append("<span></span>");
        }
        sb.append("</td><td>");
        icon = null;
        if (icon != null) {
            sb.append(icon.getHTML());
        } else {
            sb.append("<span></span>");
        }
        sb.append("</td><td>");
        sb.append("<span unselectable=\"on\" class=\"x-tree3-node-text\">");
        sb.append(text);
        sb.append("</span>");
        sb.append("</td></tr></table>");

        sb.append("</div>");
        sb.append("</div>");

        return sb.toString();
    }
}
