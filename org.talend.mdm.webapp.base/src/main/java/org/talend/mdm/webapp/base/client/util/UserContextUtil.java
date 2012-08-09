package org.talend.mdm.webapp.base.client.util;

import org.talend.mdm.webapp.base.client.model.UserContextModel;

public class UserContextUtil {

    static {
        init();
    }
    
    public native static void init()/*-{
        $wnd.mdm_ucx = $wnd.mdm_ucx || {};
    }-*/;

    public static UserContextModel getUserContext() {
        UserContextModel ucx = new UserContextModel();
        ucx.setDataContainer(getDataContainer());
        ucx.setDataModel(getDataModel());
        return ucx;
    }


    public static native String getDataContainer()/*-{
        return $wnd.mdm_ucx.dataContainer;
    }-*/;
    
    public static native void setDataContainer(String dataContainer)/*-{
        $wnd.mdm_ucx.dataContainer = dataContainer;
    }-*/;
    
    public static native String getDataModel()/*-{
        return $wnd.mdm_ucx.dataModel;
    }-*/;
    
    public static native void setDataModel(String dataModel)/*-{
        $wnd.mdm_ucx.dataModel = dataModel;
    }-*/;
}
