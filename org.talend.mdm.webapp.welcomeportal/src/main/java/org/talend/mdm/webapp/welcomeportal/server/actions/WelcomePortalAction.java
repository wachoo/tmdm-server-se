// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.welcomeportal.server.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.talend.mdm.webapp.base.client.exception.ServiceException;
import org.talend.mdm.webapp.welcomeportal.client.WelcomePortal;
import org.talend.mdm.webapp.welcomeportal.client.WelcomePortalService;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.amalto.webapp.core.util.Menu;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.Webapp;
import com.amalto.webapp.core.util.dwr.WebappInfo;
import com.amalto.webapp.util.webservices.WSByteArray;
import com.amalto.webapp.util.webservices.WSExecuteTransformerV2;
import com.amalto.webapp.util.webservices.WSGetTransformer;
import com.amalto.webapp.util.webservices.WSGetTransformerPKs;
import com.amalto.webapp.util.webservices.WSTransformer;
import com.amalto.webapp.util.webservices.WSTransformerContext;
import com.amalto.webapp.util.webservices.WSTransformerContextPipelinePipelineItem;
import com.amalto.webapp.util.webservices.WSTransformerPK;
import com.amalto.webapp.util.webservices.WSTransformerV2PK;
import com.amalto.webapp.util.webservices.WSTypedContent;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class WelcomePortalAction extends RemoteServiceServlet implements WelcomePortalService {

    private static final Logger LOG = Logger.getLogger(WelcomePortalAction.class);

    private static final String STANDALONE_PROCESS_PREFIX = "Runnable#"; //$NON-NLS-1$

    /**
     * check if is show license link.
     * 
     * @return
     */
    public boolean isHiddenLicense() {
        return isHiddenMenu(WelcomePortal.LICENSEAPP);
    }

    /**
     * check if is show task link.
     * 
     * @return
     */
    public boolean isHiddenTask() {
        return isHiddenMenu(WelcomePortal.TASKAPP);
    }

    /**
     * check if is it standalong process.
     */
    public boolean isStandaloneProcess(String wstransformerpk) {
        return wstransformerpk.startsWith(STANDALONE_PROCESS_PREFIX);
    }

    private String getDescriptionByLau(String language, String description) {
        Map<String, String> des = new HashMap<String, String>();

        for (int i = 0; i < description.length(); i++) {
            if ('[' == description.charAt(i)) {
                for (int j = i; j < description.length(); j++) {
                    if (']' == description.charAt(j)) {
                        String[] de = description.substring(i + 1, j).split(":"); //$NON-NLS-1$
                        des.put(de[0].toLowerCase(), de[1]);
                        break;
                    }
                }
            }
        }
        return des.get(language.toLowerCase());
    }

    /**
     * check if is show specify menu.
     * 
     * @param menu
     * @return
     */
    private boolean isHiddenMenu(String menu) {
        try {
            TreeMap<String, Menu> subMenus = Menu.getRootMenu().getSubMenus();
            for (Iterator<String> iter = subMenus.keySet().iterator(); iter.hasNext();) {
                String key = iter.next();
                Menu subMenu = subMenus.get(key);

                if (menu.equals(subMenu.getApplication())) {
                    return false;
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return true;
    }

    public String getAlertMsg(String language) {
        try {
            WebappInfo webappInfo = new WebappInfo();
            Webapp.INSTANCE.getInfo(webappInfo, language);
            if (webappInfo.getLicense() == null)
                return WelcomePortal.NOLICENSE;
            else if (!webappInfo.isLicenseValid())
                return WelcomePortal.EXPIREDLICENSE;
            return null;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return e.getMessage();
        }
    }

    /**
     * get workflow task informations.
     * 
     * @return
     */
    public int getTaskMsg() {
        return Webapp.INSTANCE.getTaskMsg();
    }

    public List<String> getStandaloneProcess(String language) {
        List<String> process = new ArrayList<String>();

        try {
            WSTransformerPK[] wst = Util.getPort().getTransformerPKs(new WSGetTransformerPKs("*")).getWsTransformerPK(); //$NON-NLS-1$

            for (WSTransformerPK wstransformerpk : wst) {
                if (isStandaloneProcess(wstransformerpk.getPk())) {
                    WSTransformer wsTransformer = Util.getPort().getTransformer(new WSGetTransformer(wstransformerpk));
                    String desc = getDescriptionByLau(language, wsTransformer.getDescription());
                    if (desc == null || desc.equals("")) //$NON-NLS-1$
                        process.add(wstransformerpk.getPk());
                    else
                        process.add(getDescriptionByLau(language, wsTransformer.getDescription()));
                }
            }

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return process;
    }

    /**
     * run the standalone process.
     * 
     * @param transformerPK
     * @return
     */
    public String runProcess(String transformerPK) {
        WSTransformerContext wsTransformerContext = new WSTransformerContext(new WSTransformerV2PK(transformerPK), null, null);

        try {
            StringBuilder result = new StringBuilder();
            // yguo, plugin input parameters
            String content = "<root/>"; //$NON-NLS-1$
            WSTypedContent typedContent = new WSTypedContent(null, new WSByteArray(content.getBytes("UTF-8")),//$NON-NLS-1$
                    "text/xml; charset=UTF-8");//$NON-NLS-1$
            WSExecuteTransformerV2 wsExecuteTransformerV2 = new WSExecuteTransformerV2(wsTransformerContext, typedContent);
            WSTransformerContextPipelinePipelineItem[] entries = Util.getPort().executeTransformerV2(wsExecuteTransformerV2)
                    .getPipeline().getPipelineItem();
            if (entries.length > 0) {
                WSTransformerContextPipelinePipelineItem item = entries[entries.length - 1];
                if (item.getVariable().equals("output_url")) {//$NON-NLS-1$
                    byte[] bytes = item.getWsTypedContent().getWsBytes().getBytes();
                    String urlcontent = new String(bytes);
                    Document resultDoc = Util.parse(urlcontent);
                    NodeList attrList = Util.getNodeList(resultDoc, "//attr");//$NON-NLS-1$
                    if (attrList != null && attrList.getLength() > 0) {
                        String downloadUrl = attrList.item(0).getTextContent();
                        result.append(downloadUrl);
                    }
                }
            }
            return result.toString();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage());
        }

    }

    public boolean isExpired() throws Exception {
        return Webapp.INSTANCE.isExpired();
    }
}
