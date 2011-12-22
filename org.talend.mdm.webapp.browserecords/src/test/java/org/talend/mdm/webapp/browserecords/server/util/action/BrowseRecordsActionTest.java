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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.talend.mdm.webapp.base.client.model.DataTypeConstants;
import org.talend.mdm.webapp.base.client.model.DataTypeCustomized;
import org.talend.mdm.webapp.base.shared.SimpleTypeModel;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.server.actions.BrowseRecordsAction;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;
import org.talend.mdm.webapp.browserecords.shared.EntityModel;

import com.extjs.gxt.ui.client.data.ModelData;

@SuppressWarnings("nls")
public class BrowseRecordsActionTest extends TestCase {

    private BrowseRecordsAction action = new BrowseRecordsAction();

    private String xml = "<Agency><Name>Newark</Name><Name>Newark1</Name><City>Newark</City><State>NJ</State><Zip>07107</Zip><Region>EAST</Region><MoreInfo>Map@@http://maps.google.com/maps?q=40.760667,-74.1879&amp;ll=40.760667,-74.1879&amp;z=9</MoreInfo><Id>NJ01</Id></Agency>";

    public void testMultiOccurenceNode() throws Exception {
        String language = "en";
        ItemNodeModel model = action.getItemNodeModel(getItemBean(), getEntityModel(), language);
        List<ModelData> child = model.getChildren();

        for (int i = 0; i < child.size(); i++) {
            String value = ((ItemNodeModel) child.get(i)).getObjectValue().toString();
            switch (i) {
            case 0: {
                assertEquals("NJ01", value);
                break;
            }
            case 1: {
                assertEquals("Newark", value);
                break;
            }
            case 2: {
                assertEquals("Newark1", value);
                break;
            }
            case 3: {
                assertEquals("Newark", value);
                break;
            }
            case 4: {
                assertEquals("NJ", value);
                break;
            }
            case 5: {
                assertEquals("07107", value);
                break;
            }
            case 6: {
                assertEquals("EAST", value);
                break;
            }
            case 7: {
                assertEquals("Map@@http://maps.google.com/maps?q=40.760667,-74.1879&ll=40.760667,-74.1879&z=9", value);
                break;
            }
            default: {
            }
            }

        }
    }

    private ItemBean getItemBean() {
        ItemBean item = new ItemBean("Agency", "NJ01", xml);
        return item;
    }

    public static EntityModel getEntityModel() {
        Map<String, TypeModel> metadata = new LinkedHashMap<String, TypeModel>();
        DataTypeCustomized agency = new DataTypeCustomized("AgencyType", "anyType");
        ComplexTypeModel complexModel = new ComplexTypeModel("Agency", agency);
        complexModel.getLabelMap().put("en", "D* Agence");
        complexModel.getLabelMap().put("fr", "Agency");
        complexModel.setXpath("Agency");
        metadata.put("Agency", complexModel);

        SimpleTypeModel typeModel = new SimpleTypeModel("Id", DataTypeConstants.STRING);
        complexModel.addSubType(typeModel);
        typeModel.getLabelMap().put("fr", "Identifiant");
        typeModel.getLabelMap().put("en", "Identifier");
        typeModel.setXpath("Agency/Id");
        typeModel.setTypePath("Agency/Id");
        metadata.put("Agency/Id", typeModel);

        typeModel = new SimpleTypeModel("Name", DataTypeConstants.STRING);
        typeModel.getLabelMap().put("fr", "Nom");
        typeModel.getLabelMap().put("en", "Name");
        typeModel.setMaxOccurs(-1);
        typeModel.setMinOccurs(1);
        metadata.put("Agency/Name", typeModel);
        typeModel.setXpath("Agency/Name");
        typeModel.setTypePath("Agency/Name");
        complexModel.addSubType(typeModel);

        typeModel = new SimpleTypeModel("City", DataTypeConstants.STRING);
        typeModel.getLabelMap().put("fr", "City");
        typeModel.getLabelMap().put("en", "City");
        typeModel.setXpath("Agency/City");
        typeModel.setTypePath("Agency/City");
        metadata.put("Agency/City", typeModel);
        complexModel.addSubType(typeModel);

        typeModel = new SimpleTypeModel("State", DataTypeConstants.STRING);
        typeModel.getLabelMap().put("fr", "State");
        typeModel.getLabelMap().put("en", "State");
        typeModel.setXpath("Agency/State");
        typeModel.setTypePath("Agency/State");
        metadata.put("Agency/State", typeModel);
        complexModel.addSubType(typeModel);

        typeModel = new SimpleTypeModel("Zip", DataTypeConstants.STRING);
        typeModel.getLabelMap().put("fr", "Zip");
        typeModel.getLabelMap().put("en", "Zip");
        typeModel.setXpath("Agency/Zip");
        typeModel.setTypePath("Agency/Zip");
        metadata.put("Agency/Zip", typeModel);
        complexModel.addSubType(typeModel);

        typeModel = new SimpleTypeModel("Region", DataTypeConstants.STRING);
        typeModel.getLabelMap().put("fr", "Region");
        typeModel.getLabelMap().put("en", "Region");
        typeModel.setXpath("Agency/Region");
        typeModel.setTypePath("Agency/Region");
        metadata.put("Agency/Region", typeModel);
        complexModel.addSubType(typeModel);

        typeModel = new SimpleTypeModel("MoreInfo", DataTypeConstants.STRING);
        typeModel.getLabelMap().put("fr", "MoreInfo");
        typeModel.getLabelMap().put("en", "MoreInfo");
        typeModel.setXpath("Agency/MoreInfo");
        typeModel.setTypePath("Agency/MoreInfo");
        metadata.put("Agency/MoreInfo", typeModel);
        complexModel.addSubType(typeModel);

        EntityModel entity = new EntityModel();
        entity.setMetaDataTypes(metadata);
        entity.setConceptName("Agency");
        String[] keys = { "Agency/Id" };
        entity.setKeys(keys);

        return entity;
    }
}