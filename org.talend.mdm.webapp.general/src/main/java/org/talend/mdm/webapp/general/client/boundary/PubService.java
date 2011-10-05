package org.talend.mdm.webapp.general.client.boundary;


public class PubService {

    public native static void registerLanguageService()/*-{
        $wnd.getLanguage = function(){
        return @org.talend.mdm.webapp.base.client.util.UrlUtil::getLanguage()();
        };
    }-*/;
}
