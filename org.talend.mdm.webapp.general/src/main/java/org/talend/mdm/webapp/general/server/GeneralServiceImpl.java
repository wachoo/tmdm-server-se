package org.talend.mdm.webapp.general.server;

import java.util.ArrayList;
import java.util.List;

import org.talend.mdm.webapp.general.client.GeneralService;
import org.talend.mdm.webapp.general.model.MenuBean;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GeneralServiceImpl extends RemoteServiceServlet implements GeneralService {

    public String greetServer(String input) throws IllegalArgumentException {
        System.out.println("hello");
        System.out.println("bye");
        return "Hello, " + input;
    }

    @Override
    public List<MenuBean> getMenus(String language) throws Exception {
        return fakeMenu();
    }
    
    //temp
    private List<MenuBean> fakeMenu(){
        String[][] data = {
                {"baidu", "baidu", "baidu", "baidu", "http://www.baidu.com", "http://talendforge.org/forum/img/talend.jpg"},
                {"google", "google", "google", "google", "http://www.google.com", "http://talendforge.org/forum/img/talend.jpg"},
                {"yahoo", "yahoo", "yahoo", "yahoo", "http://www.yahoo.com", "http://talendforge.org/forum/img/talend.jpg"},
                {"talendforge", "talendforge", "talendforge", "talendforge", "http://talendforge.org", "http://talendforge.org/forum/img/talend.jpg"},
                {"test1", "test1", "test1", "test1", "test1.html", "http://talendforge.org/forum/img/talend.jpg"},
                {"test2", "test2", "test2", "test2", "test2.html", "http://talendforge.org/forum/img/talend.jpg"}
        };
        List<MenuBean> menus = new ArrayList<MenuBean>();
        for (int i = 0;i < data.length;i++){
            MenuBean item = new MenuBean();
            item.setId(i);
            item.setName(data[i][0]);
            item.setApplication(data[i][1]);
            item.setContext(data[i][2]);
            item.setDesc(data[i][3]);
            item.setUrl(data[i][4]);
            item.setIcon(data[i][5]);
            menus.add(item);

        }
        return menus;
    }

    @Override
    public String getMsg() {
        return "server message";
    }
}
