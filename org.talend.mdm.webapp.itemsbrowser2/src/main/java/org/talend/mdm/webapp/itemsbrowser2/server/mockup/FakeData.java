package org.talend.mdm.webapp.itemsbrowser2.server.mockup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;

/**
 * DOC HSHU  class global comment. Detailled comment
 */
public class FakeData {
    
    public static final String MDM_DEFAULT_ENDPOINTADDRESS="http://localhost:8080/talend/TalendPort";//$NON-NLS-1$
    
    public static final String MDM_DEFAULT_USERNAME="administrator";//$NON-NLS-1$
    
    public static final String MDM_DEFAULT_PASSWORD="administrator";//$NON-NLS-1$
    
    public static final String DATA_MODEL="DStar";//$NON-NLS-1$
    
    public static final String DATA_CLUSTER="DStar";//$NON-NLS-1$
    
    public static final String DEFAULT_VIEW="Browse_items_customer";
    
    private static ArrayList<ItemBean> customer_items = new ArrayList<ItemBean>();
    
    private static ArrayList<ItemBean> state_items = new ArrayList<ItemBean>();
    
    private static final String[] customers = new String[] {
        "<customer><id>0</id><name>markboland05</name><mail>mark@example.com</mail><address>[0]</address><url><![CDATA[http://maps.google.com/maps?q=39.364966,-74.43903&ll=39.364966,-74.43903&z=9]]></url></customer>",
        "<customer><id>1</id><name>Hollie Voss</name><mail>hollie@example.com</mail><address>[1]</address><url><![CDATA[http://maps.google.com/maps?q=39.364966,-74.43903&ll=39.364966,-74.43903&z=9]]></url></customer>",
        "<customer><id>2</id><name>boticario</name><mail>boticario@example.com</mail><address>[2]</address><url><![CDATA[http://maps.google.com/maps?q=39.364966,-74.43903&ll=39.364966,-74.43903&z=9]]></url></customer>",
        "<customer><id>3</id><name>Emerson Milton</name><mail>healy@example.com</mail><address>[3]</address><url><![CDATA[http://maps.google.com/maps?q=39.364966,-74.43903&ll=39.364966,-74.43903&z=9]]></url></customer>",
        "<customer><id>4</id><name>Healy Colette</name><mail>emerson@example.com</mail><address>[4]</address><url><![CDATA[http://maps.google.com/maps?q=39.364966,-74.43903&ll=39.364966,-74.43903&z=9]]></url></customer>"};
    
    private static final String[] states = new String[] {
        "<state><id>0</id><name>Alabama</name><desc>The Heart of Dixie</desc></state>",
        "<state><id>1</id><name>Alaska</name><desc>The Land of the Midnight Sun</desc></state>",
        "<state><id>2</id><name>Arizona</name><desc>The Grand Canyon State</desc></state>",
        "<state><id>3</id><name>Arkansas</name><desc>The Natural State</desc></state>",
        "<state><id>4</id><name>California</name><desc>The Golden State</desc></state>",
        "<state><id>5</id><name>Colorado</name><desc>The Mountain State</desc></state>",
        "<state><id>6</id><name>Connecticut</name><desc>The Constitution State</desc></state>"};
    
    private static final String[] customers_viewables = new String[] {
        "customer/id",
        "customer/name",
        "customer/mail",
        "customer/url"};
    
    private static final Map<String, String> customers_viewables_label = new HashMap<String, String>();
    static {
    	customers_viewables_label.put("customer/id", "ID identify");
    	customers_viewables_label.put("customer/name", "Name");
    	customers_viewables_label.put("customer/mail", "E-mail");
    	customers_viewables_label.put("customer/url", "Url");
    }
    
    private static final Map<String, String> customers_viewables_type = new HashMap<String, String>();
    static {
    	customers_viewables_type.put("id", "string");
    	customers_viewables_type.put("name", "string");
    	customers_viewables_type.put("mail", "string");
    	customers_viewables_type.put("address", "string");
    	customers_viewables_type.put("url", "URL");
    }
    
    public static String getTypeByViewable(String viewable){
    	return customers_viewables_type.get(viewable);
    }
    
    private static final String[] states_viewables = new String[] {
        "state/id",
        "state/name",
        "state/desc"};

    static {
      for (int i = 0; i < customers.length; ++i) {
          customer_items.add(createFakeCustomerItem(i));
          state_items.add(createFakeStateItem(i));
      }
    }
    
    private static ItemBean createFakeCustomerItem(int index) {

        String customer = customers[index];

        ItemBean itemBean = new ItemBean("customer",String.valueOf(index),customer);
        return itemBean;
        
     }
    
    private static ItemBean createFakeStateItem(int index) {

        String state = states[index];

        ItemBean itemBean = new ItemBean("state",String.valueOf(index),state);
        return itemBean;
        
     }

    public static ArrayList<ItemBean> getFakeCustomerItems() {
        return customer_items;
    }
    
    public static String getFakeCustomerItem(String id) {
        return customers[Integer.valueOf(id)];
    }

    public static ArrayList<ItemBean> getFakeStateItems() {
        return state_items;
    }
    
    public static String getFakeStateItem(String id) {
        return states[Integer.valueOf(id)];
    }

    public static String[] getEntityViewables(String viewName) {
        if(viewName.equals("Browse_items_customer")){
        	return customers_viewables;
        }
        else if(viewName.equals("Browse_items_state"))return states_viewables;
        
        return null;
    }

}