// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecords.server.util.action;

import java.util.List;

import junit.framework.TestCase;

import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.server.actions.BrowseRecordsAction;
import org.talend.mdm.webapp.browserecords.server.util.TestData;

import com.extjs.gxt.ui.client.data.ModelData;

@SuppressWarnings("nls")
public class BrowseRecordsActionTest extends TestCase {

    private BrowseRecordsAction action = new BrowseRecordsAction();

    private String xml = "<Agency><Name>Newark</Name><Name>Newark1</Name><City>Newark</City><State>NJ</State><Zip>07107</Zip><Region>EAST</Region><MoreInfo>Map@@http://maps.google.com/maps?q=40.760667,-74.1879&amp;ll=40.760667,-74.1879&amp;z=9</MoreInfo><Id>NJ01</Id></Agency>"; //$NON-NLS-1$

    public void testMultiOccurenceNode() throws Exception {
        String language = "en"; //$NON-NLS-1$
        ItemNodeModel model = action.getItemNodeModel(getItemBean(), TestData.getEntityModel(), language);
        List<ModelData> child = model.getChildren();

        for (int i = 0; i < child.size(); i++) {
            String value = ((ItemNodeModel) child.get(i)).getObjectValue().toString();
            switch (i) {
            case 0: {
                assertEquals("NJ01", value); //$NON-NLS-1$
                break;
            }
            case 1: {
                assertEquals("Newark", value); //$NON-NLS-1$
                break;
            }
            case 2: {
                assertEquals("Newark1", value); //$NON-NLS-1$
                break;
            }
            case 3: {
                assertEquals("Newark", value); //$NON-NLS-1$
                break;
            }
            case 4: {
                assertEquals("NJ", value); //$NON-NLS-1$
                break;
            }
            case 5: {
                assertEquals("07107", value); //$NON-NLS-1$
                break;
            }
            case 6: {
                assertEquals("EAST", value); //$NON-NLS-1$
                break;
            }
            case 7: {
                assertEquals("Map@@http://maps.google.com/maps?q=40.760667,-74.1879&ll=40.760667,-74.1879&z=9", value); //$NON-NLS-1$
                break;
            }
            default: {
            }
            }

        }
    }
    
	public void test_TaskIdIsNull() {
        ItemBean itemBean = getItemBean();

        boolean taskIdNotNull = itemBean.getTaskId() != null && itemBean.getTaskId().trim().length() > 0
                && !"null".equalsIgnoreCase(itemBean.getTaskId().trim());
        assertEquals(false, taskIdNotNull);
        // 1
        itemBean.setTaskId("");
        taskIdNotNull = itemBean.getTaskId() != null && itemBean.getTaskId().trim().length() > 0
                && !"null".equalsIgnoreCase(itemBean.getTaskId().trim());
        assertEquals(false, taskIdNotNull);
        // 2
        itemBean.setTaskId(" ");
        taskIdNotNull = itemBean.getTaskId() != null && itemBean.getTaskId().trim().length() > 0
                && !"null".equalsIgnoreCase(itemBean.getTaskId().trim());
        assertEquals(false, taskIdNotNull);
        // 3
        itemBean.setTaskId("null");
        taskIdNotNull = itemBean.getTaskId() != null && itemBean.getTaskId().trim().length() > 0
                && !"null".equalsIgnoreCase(itemBean.getTaskId().trim());
        assertEquals(false, taskIdNotNull);
        // 4
        itemBean.setTaskId("Null");
        taskIdNotNull = itemBean.getTaskId() != null && itemBean.getTaskId().trim().length() > 0
                && !"null".equalsIgnoreCase(itemBean.getTaskId().trim());
        assertEquals(false, taskIdNotNull);
        // 5
        itemBean.setTaskId("null ");
        taskIdNotNull = itemBean.getTaskId() != null && itemBean.getTaskId().trim().length() > 0
                && !"null".equalsIgnoreCase(itemBean.getTaskId().trim());
        assertEquals(false, taskIdNotNull);
        // 6
        itemBean.setTaskId("NULL");
        taskIdNotNull = itemBean.getTaskId() != null && itemBean.getTaskId().trim().length() > 0
                && !"null".equalsIgnoreCase(itemBean.getTaskId().trim());
        assertEquals(false, taskIdNotNull);
        // 7
        itemBean.setTaskId("123");
        taskIdNotNull = itemBean.getTaskId() != null && itemBean.getTaskId().trim().length() > 0
                && !"null".equalsIgnoreCase(itemBean.getTaskId().trim());
        assertEquals(true, taskIdNotNull);
    }
    
    private ItemBean getItemBean() {
        ItemBean item = new ItemBean("Agency", "NJ01", xml); //$NON-NLS-1$//$NON-NLS-2$
        return item;
    }

}