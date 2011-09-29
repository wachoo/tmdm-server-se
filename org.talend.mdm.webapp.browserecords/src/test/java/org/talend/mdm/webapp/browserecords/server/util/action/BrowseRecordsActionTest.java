package org.talend.mdm.webapp.browserecords.server.util.action;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.dom4j.DocumentException;
import org.talend.mdm.webapp.browserecords.client.model.DataTypeConstants;
import org.talend.mdm.webapp.browserecords.client.model.DataTypeCustomized;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.server.actions.BrowseRecordsAction;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;
import org.talend.mdm.webapp.browserecords.shared.EntityModel;
import org.talend.mdm.webapp.browserecords.shared.SimpleTypeModel;
import org.talend.mdm.webapp.browserecords.shared.TypeModel;

import com.extjs.gxt.ui.client.data.ModelData;

public class BrowseRecordsActionTest extends TestCase {
	BrowseRecordsAction action = new BrowseRecordsAction();
	
	protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testMultiOccurenceNode() throws DocumentException, Exception, IOException {
    	String language = "en"; //$NON-NLS-1$
    	ItemNodeModel model = action.getItemNodeModel(getItemBean(), getEntityModel(), language);
    	List<ModelData> child = model.getChildren();
    	
    	for(int i = 0; i < child.size(); i++) {
    		String value = ((ItemNodeModel)child.get(i)).getObjectValue().toString();
    		switch(i) {
	    		case 0 : {
					assertEquals("NJ01", value); //$NON-NLS-1$
					break;
				}
	    		case 1 : {
    				assertEquals("Newark", value); //$NON-NLS-1$
    				break;
    			}
	    		case 2 : {
    				assertEquals("Newark1", value); //$NON-NLS-1$
    				break;
    			}
	    		case 3 : {
    				assertEquals("Newark", value); //$NON-NLS-1$
    				break;
    			}
	    		case 4 : {
    				assertEquals("NJ", value); //$NON-NLS-1$
    				break;
    			}
	    		case 5 : {
    				assertEquals("07107", value); //$NON-NLS-1$
    				break;
    			}
	    		case 6 : {
    				assertEquals("EAST", value); //$NON-NLS-1$
    				break;
    			}
	    		case 7 : {
	    			assertEquals("Map@@http://maps.google.com/maps?q=40.760667,-74.1879&ll=40.760667,-74.1879&z=9", value); //$NON-NLS-1$
	    			break;
	    		}
	    		default : {
	    		}
    		}
    			
    	}
    }
    
    public void testVisibleRule() throws DocumentException, Exception, IOException {
    	
    }
    
    private ItemBean getItemBean() {
    	String xml = "<Agency><Name>Newark</Name><Name>Newark1</Name><City>Newark</City><State>NJ</State><Zip>07107</Zip><Region>EAST</Region><MoreInfo>Map@@http://maps.google.com/maps?q=40.760667,-74.1879&amp;ll=40.760667,-74.1879&amp;z=9</MoreInfo><Id>NJ01</Id></Agency>"; //$NON-NLS-1$
    	ItemBean item = new ItemBean("Agency", "NJ01", xml);  //$NON-NLS-1$//$NON-NLS-2$
    	return item;
    }
    
    private EntityModel getEntityModel() {
    	Map<String, TypeModel> metadata = new LinkedHashMap<String, TypeModel>();
    	DataTypeCustomized agency = new DataTypeCustomized("AgencyType", "anyType"); //$NON-NLS-1$ //$NON-NLS-2$
    	ComplexTypeModel complexModel = new ComplexTypeModel("Agency", agency); //$NON-NLS-1$
    	complexModel.getLabelMap().put("en", "D* Agence");  //$NON-NLS-1$//$NON-NLS-2$
    	complexModel.getLabelMap().put("fr", "Agency"); //$NON-NLS-1$ //$NON-NLS-2$
    	complexModel.setXpath("Agency"); //$NON-NLS-1$
    	metadata.put("Agency", complexModel); //$NON-NLS-1$
    	
    	SimpleTypeModel typeModel = new SimpleTypeModel("Id", DataTypeConstants.STRING); //$NON-NLS-1$
    	complexModel.addSubType(typeModel);
    	typeModel.getLabelMap().put("fr", "Identifiant");  //$NON-NLS-1$//$NON-NLS-2$
    	typeModel.getLabelMap().put("en", "Identifier"); //$NON-NLS-1$ //$NON-NLS-2$
    	typeModel.setXpath("Agency/Id"); //$NON-NLS-1$
    	metadata.put("Agency/Id", typeModel); //$NON-NLS-1$
    	
    	typeModel = new SimpleTypeModel("Name", DataTypeConstants.STRING); //$NON-NLS-1$
    	typeModel.getLabelMap().put("fr", "Nom"); //$NON-NLS-1$ //$NON-NLS-2$
    	typeModel.getLabelMap().put("en", "Name");  //$NON-NLS-1$//$NON-NLS-2$
    	typeModel.setMaxOccurs(-1);
    	typeModel.setMinOccurs(1);
    	metadata.put("Agency/Name", typeModel); //$NON-NLS-1$
    	typeModel.setXpath("Agency/Name"); //$NON-NLS-1$
    	complexModel.addSubType(typeModel);
    	
    	typeModel = new SimpleTypeModel("City", DataTypeConstants.STRING); //$NON-NLS-1$
    	typeModel.getLabelMap().put("fr", "City"); //$NON-NLS-1$ //$NON-NLS-2$
    	typeModel.getLabelMap().put("en", "City");  //$NON-NLS-1$//$NON-NLS-2$
    	typeModel.setXpath("Agency/City"); //$NON-NLS-1$
    	metadata.put("Agency/City", typeModel); //$NON-NLS-1$
    	complexModel.addSubType(typeModel);
    	
    	typeModel = new SimpleTypeModel("State", DataTypeConstants.STRING); //$NON-NLS-1$
    	typeModel.getLabelMap().put("fr", "State"); //$NON-NLS-1$ //$NON-NLS-2$
    	typeModel.getLabelMap().put("en", "State");  //$NON-NLS-1$//$NON-NLS-2$
    	typeModel.setXpath("Agency/State"); //$NON-NLS-1$
    	metadata.put("Agency/State", typeModel); //$NON-NLS-1$
    	complexModel.addSubType(typeModel);
    	
    	typeModel = new SimpleTypeModel("Zip", DataTypeConstants.STRING); //$NON-NLS-1$
    	typeModel.getLabelMap().put("fr", "Zip"); //$NON-NLS-1$ //$NON-NLS-2$
    	typeModel.getLabelMap().put("en", "Zip");  //$NON-NLS-1$//$NON-NLS-2$
    	typeModel.setXpath("Agency/Zip"); //$NON-NLS-1$
    	metadata.put("Agency/Zip", typeModel); //$NON-NLS-1$
    	complexModel.addSubType(typeModel);
    	
    	typeModel = new SimpleTypeModel("Region", DataTypeConstants.STRING); //$NON-NLS-1$
    	typeModel.getLabelMap().put("fr", "Region"); //$NON-NLS-1$ //$NON-NLS-2$
    	typeModel.getLabelMap().put("en", "Region");  //$NON-NLS-1$//$NON-NLS-2$
    	typeModel.setXpath("Agency/Region"); //$NON-NLS-1$
    	metadata.put("Agency/Region", typeModel); //$NON-NLS-1$
    	complexModel.addSubType(typeModel);
    	
    	typeModel = new SimpleTypeModel("MoreInfo", DataTypeConstants.STRING); //$NON-NLS-1$
    	typeModel.getLabelMap().put("fr", "MoreInfo"); //$NON-NLS-1$ //$NON-NLS-2$
    	typeModel.getLabelMap().put("en", "MoreInfo");  //$NON-NLS-1$//$NON-NLS-2$
    	typeModel.setXpath("Agency/MoreInfo"); //$NON-NLS-1$
    	metadata.put("Agency/MoreInfo", typeModel); //$NON-NLS-1$
    	complexModel.addSubType(typeModel);
    	
    	EntityModel entity = new EntityModel();
    	entity.setMetaDataTypes(metadata);
    	entity.setConceptName("Agency"); //$NON-NLS-1$
    	String[] keys = {"Agency/Id"};  //$NON-NLS-1$
    	entity.setKeys(keys);
    	
    	return entity;
    }
}
