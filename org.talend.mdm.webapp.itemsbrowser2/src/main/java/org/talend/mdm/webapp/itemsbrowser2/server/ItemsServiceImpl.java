package org.talend.mdm.webapp.itemsbrowser2.server;

import java.util.List;

import org.talend.mdm.webapp.itemsbrowser2.client.ItemsService;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;
import org.talend.mdm.webapp.itemsbrowser2.server.mockup.FakeData;
import org.talend.mdm.webapp.itemsbrowser2.shared.FieldVerifier;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ItemsServiceImpl extends RemoteServiceServlet implements ItemsService {

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
    
    /* (non-Jsdoc)
     * @see org.talend.mdm.webapp.itemsbrowser2.client.ItemsService#getEntityItems(java.lang.String)
     */
    public List<ItemBean> getEntityItems(String entityName) {
        List<ItemBean> items = null;
        
        if(entityName.equals("customer"))
            items=FakeData.getFakeCustomerItems();
        else if(entityName.equals("state"))
            items=FakeData.getFakeStateItems();
        
        return items;
    }
    
    
    /**
     * DOC HSHU Comment method "getView".
     */
    public ViewBean getView(String viewName) {
        ViewBean viewBean=new ViewBean();
        viewBean.setViewName(viewName);
        //TODO mockup
        if(viewName.equals(FakeData.DEFAULT_VIEW)) {
            
            String[] viewables=FakeData.getEntityViewables(viewName);
            for (String viewable : viewables) {
                viewBean.addViewableXpath(viewable);
            }
            
        }
        return viewBean;
    }
    
}
