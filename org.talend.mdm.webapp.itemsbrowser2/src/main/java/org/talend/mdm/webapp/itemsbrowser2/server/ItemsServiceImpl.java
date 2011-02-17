package org.talend.mdm.webapp.itemsbrowser2.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.talend.mdm.webapp.itemsbrowser2.client.ItemsService;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemFormBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemFormLineBean;
import org.talend.mdm.webapp.itemsbrowser2.server.dwr.ItemsBrowser2DWR;
import org.talend.mdm.webapp.itemsbrowser2.server.mockup.FakeCustomerConcept;
import org.talend.mdm.webapp.itemsbrowser2.server.mockup.FakeData;
import org.talend.mdm.webapp.itemsbrowser2.server.util.DynamicLabelUtil;
import org.talend.mdm.webapp.itemsbrowser2.server.util.XmlUtil;
import org.talend.mdm.webapp.itemsbrowser2.server.util.callback.ElementProcess;
import org.talend.mdm.webapp.itemsbrowser2.shared.FieldVerifier;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.dwr.CommonDWR;
import com.amalto.webapp.util.webservices.WSGetView;
import com.amalto.webapp.util.webservices.WSView;
import com.amalto.webapp.util.webservices.WSViewPK;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ItemsServiceImpl extends RemoteServiceServlet implements ItemsService {

    private static final Logger LOG = Logger.getLogger(ItemsBrowser2DWR.class);

    public String greetServer(String input) throws IllegalArgumentException {
        // Verify that the input is valid.
        if (!FieldVerifier.isValidName(input)) {
            // If the input is not valid, throw an IllegalArgumentException back to
            // the client.
            throw new IllegalArgumentException("Name must be at least 4 characters long");
        }

        String serverInfo = getServletContext().getServerInfo();
        String userAgent = getThreadLocalRequest().getHeader("User-Agent");
        return "Hello, " + input + "!<br><br>I am running " + serverInfo + ".<br><br>It looks like you are using:<br>"
                + userAgent;
    }

    public ArrayList<ItemBean> getFakeCustomerItems() {
    	return null;
    }
    
    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.mdm.webapp.itemsbrowser2.client.ItemsService#getEntityItems(java.lang.String)
     */
    public List<ItemBean> getEntityItems(String entityName) {
        List<ItemBean> items = null;

        if (entityName.equals("customer"))
            items = FakeData.getFakeCustomerItems();
        else if (entityName.equals("state"))
            items = FakeData.getFakeStateItems();

        return items;
    }

    /**
     * DOC HSHU Comment method "getView".
     */
    public ViewBean getView(String viewPk) {
        ViewBean viewBean = new ViewBean();
        viewBean.setViewPK(viewPk);
        // TODO mockup
        if (viewPk.equals(FakeData.DEFAULT_VIEW)) {
        	String[] viewables = null;
        	if (GWT.isScript()){
        		viewables = getViewables(viewPk, "en");
        	} else {
        		viewables = FakeData.getEntityViewables(viewPk);
        	}
            for (String viewable : viewables) {
                viewBean.addViewableXpath(viewable);
            }
        }
        return viewBean;
    }
    
    public String[] getViewables(String viewPK, String language) {
        try {
        	WSView wsView = null;
        	try{
        		wsView = com.amalto.webapp.core.util.Util.getPort().getView(new WSGetView(new WSViewPK(viewPK)));
        	} catch (Exception e){
        		System.out.println(e.getMessage());
        	}
            String[] viewables = wsView.getViewableBusinessElements();
            // TODO temp test
            System.out.println(viewables.length);
            String[] labelViewables = new String[viewables.length];
            Configuration config = Configuration.getInstance();
            HashMap<String, String> xpathToLabel = CommonDWR.getFieldsByDataModel(config.getModel(),
                    CommonDWR.getConceptFromBrowseItemView(viewPK), language, true);
            
            for (int i = 0; i < viewables.length; i++) {
                String labelViewable = "";
                String path = viewables[i];
                String label = xpathToLabel.get(viewables[i]);
                if (label == null) {
                    labelViewable = path;
                } else {
                    if (DynamicLabelUtil.isDynamicLabel(label)) {
                        String field = com.amalto.webapp.core.util.Util.getFieldFromPath(path);// get field
                        labelViewable = (field == null ? path : field);
                    } else {
                        labelViewable = label;
                    }
                }
                labelViewables[i] = labelViewable;
            }
            return labelViewables;
        } catch (Exception e) {
        	e.printStackTrace();
            LOG.error(e.getMessage(), e);
            return null;
        }

    }

    /**
     * DOC HSHU Comment method "setForm".
     */
    public ItemFormBean setForm(ItemBean item) {

        final ItemFormBean itemFormBean = new ItemFormBean();
        itemFormBean.setName(item.getConcept() + " " + item.getIds());

        // get item
        // TODO: add data cluster to the criteria
        String itemXml = null;
        String entityName = item.getConcept();
        if (entityName.equals("customer"))
            itemXml = FakeData.getFakeCustomerItem(item.getIds());
        else if (entityName.equals("state"))
            itemXml = FakeData.getFakeStateItem(item.getIds());

        // get datamodel
        final FakeCustomerConcept fakeCustomerConcept = new FakeCustomerConcept();

        // go through item
        try {
            
            Document itemDoc = XmlUtil.parseText(itemXml);
            XmlUtil.iterate(itemDoc, new ElementProcess() {

                @Override
                public void process(Element element) {

                    ItemFormLineBean itemFormLineBean = new ItemFormLineBean();
                    
                    String path=element.getUniquePath();

                    // TODO check with complete schema
                    String label = element.getName();
                    String value = element.getText();

                    itemFormLineBean.setFieldType(ItemFormLineBean.FIELD_TYPE_TEXTFIELD);
                    itemFormLineBean.setFieldLabel(label);
                    itemFormLineBean.setFieldValue(value);
                    
                    // check foreign key
                    List<String> paths = fakeCustomerConcept.getForeignKeyPaths();
                    if(paths.contains(path))itemFormLineBean.setHasForeignKey(true);

                    itemFormBean.addLine(itemFormLineBean);

                }

            });

        } catch (DocumentException e) {
            LOG.error(e);
        }

        return itemFormBean;
    }

}
