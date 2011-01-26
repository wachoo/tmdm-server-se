package org.talend.mdm.webapp.itemsbrowser2.server.mockup;

import java.util.ArrayList;
import java.util.List;

import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;

/**
 * DOC HSHU  class global comment. Detailled comment
 */
public class FakeData {
    
    private static ArrayList<ItemBean> items = new ArrayList<ItemBean>();
    
    private static final int NUM_ITEMS = 3;
    
    private static int customerIdx = 0;
    
    private static final String[] customers = new String[] {
        "<customer><id>0</id><name>markboland05</name><mail>mark@example.com</mail><address>Alabama</address></customer>",
        "<customer><id>1</id><name>Hollie Voss</name><mail>hollie@example.com</mail><address>Alaska</address></customer>",
        "<customer><id>2</id><name>boticario</name><mail>boticario@example.com</mail><address>Arizona</address></customer>",
        "<customer><id>3</id><name>Emerson Milton</name><mail>healy@example.com</mail><address>Arkansas</address></customer>",
        "<customer><id>4</id><name>Healy Colette</name><mail>emerson@example.com</mail><address>California</address></customer>"};
    
    private static final String[] customers_viewables = new String[] {
        "customer/id",
        "customer/name",
        "customer/mail",
        "customer/address"};

    static {
      for (int i = 0; i < NUM_ITEMS; ++i) {
        items.add(createFakeItem());
      }
    }
    
    private static ItemBean createFakeItem() {

        String customer = customers[customerIdx++];
        if (customerIdx == customers.length) customerIdx = 0;

        ItemBean itemBean = new ItemBean("customer",String.valueOf(customerIdx-1),customer);
        return itemBean;
     }
    
    public static List<ItemBean> getFakeItems() {
        List<ItemBean> list = new ArrayList<ItemBean>();
        int size = items.size();
        for (int i = 0; i < size; i++) {
          list.add(items.get(i));
        }
        return list;
      }

    
    public static String[] getCustomersViewables() {
        return customers_viewables;
    }
    
}