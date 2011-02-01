package org.talend.mdm.webapp.itemsbrowser2.server.mockup;

import java.util.ArrayList;
import java.util.List;

import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;

/**
 * DOC HSHU  class global comment. Detailled comment
 */
public class FakeData {
    
    public static final String DEFAULT_VIEW="Browse_items_customer";
    
    private static ArrayList<ItemBean> customer_items = new ArrayList<ItemBean>();
    
    private static ArrayList<ItemBean> state_items = new ArrayList<ItemBean>();
    
    private static final String[] customers = new String[] {
        "<customer><id>0</id><name>markboland05</name><mail>mark@example.com</mail><address>[0]</address></customer>",
        "<customer><id>1</id><name>Hollie Voss</name><mail>hollie@example.com</mail><address>[1]</address></customer>",
        "<customer><id>2</id><name>boticario</name><mail>boticario@example.com</mail><address>[2]</address></customer>",
        "<customer><id>3</id><name>Emerson Milton</name><mail>healy@example.com</mail><address>[3]</address></customer>",
        "<customer><id>4</id><name>Healy Colette</name><mail>emerson@example.com</mail><address>[4]</address></customer>"};
    
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
        "customer/address"};
    
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

    
    public static ArrayList<ItemBean> getFakeStateItems() {
        return state_items;
    }

    public static String[] getEntityViewables(String viewName) {
        
        if(viewName.equals("Browse_items_customer"))return customers_viewables;
        else if(viewName.equals("Browse_items_state"))return states_viewables;
        
        return null;
    }

}