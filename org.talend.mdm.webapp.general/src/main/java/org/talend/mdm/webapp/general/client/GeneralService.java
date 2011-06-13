package org.talend.mdm.webapp.general.client;

import java.util.List;

import org.talend.mdm.webapp.general.model.MenuBean;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("GeneralService")
public interface GeneralService extends RemoteService {

    String greetServer(String name) throws IllegalArgumentException;
    
    List<MenuBean> getMenus(String language) throws Exception;
    
    String getMsg();
}
