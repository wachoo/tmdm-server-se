package org.talend.mdm.webapp.welcome.dwr;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.amalto.webapp.core.bean.ListRange;
import com.amalto.webapp.core.util.Menu;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.Webapp;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.core.util.dwr.ExtJSFormResponse;
import com.amalto.webapp.core.util.dwr.ExtJSFormSuccessResponse;
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

public class WelcomeDWR {

    private static final Logger LOG = Logger.getLogger(Webapp.class);

    private static final String STANDALONE_PROCESS_PREFIX = "Runnable#"; //$NON-NLS-1$

    public WelcomeDWR() {
        super();
    }

    /**
     * get license informations.
     * 
     * @return
     */
    public ExtJSFormResponse getLicenseMsg(String language) {
        WebappInfo webappInfo = new WebappInfo();
        Webapp.INSTANCE.getInfo(webappInfo, language);
        return new ExtJSFormSuccessResponse("", webappInfo); //$NON-NLS-1$
    }

    /**
     * get label of link on welcome page.
     * 
     * @param language
     * @return
     */
    public Map<String, String> getLabels(String language) {
        Map<String, String> labels = new HashMap<String, String>();

        try {
            TreeMap<String, Menu> subMenus = Menu.getRootMenu().getSubMenus();
            for (Iterator<String> iter = subMenus.keySet().iterator(); iter.hasNext();) {
                String key = iter.next();
                Menu subMenu = subMenus.get(key);

                labels.put(subMenu.getApplication(), subMenu.getLabels().get(language));
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return labels;
    }

    /**
     * get workflow task informations.
     * 
     * @return
     */
    public int getTaskMsg() {
        return Webapp.INSTANCE.getTaskMsg();
    }

    /**
     * check if is show license link.
     * 
     * @return
     */
    public boolean isHiddenLicense() {
        return isHiddenMenu("License"); //$NON-NLS-1$
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

    /**
     * check if is show task link.
     * 
     * @return
     */
    public boolean isHiddenTask() {
        return isHiddenMenu("WorkflowTasks"); //$NON-NLS-1$
    }
    
    public String getDescriptionByLau(String language, String description) {
    	Map<String, String> des = new HashMap<String, String>();
    	
    	try {
	    	for(int i = 0; i < description.length(); i++) {
	    		if('[' == description.charAt(i)) {
	    			for(int j = i; j < description.length(); j++) {
	    				if(']' == description.charAt(j)){
	    					String[] de = description.substring(i + 1, j).split(":"); 
	    					des.put(de[0].toLowerCase(), de[1]);
	    					break;
	    				}
	    			}
	    		}
	    	}
    	}
    	catch(Exception ex) {
//    		throw new Exception("description is wrong!");
    	}
    	return des.get(language.toLowerCase());
    }

    /**
     * get all standalone processes.
     */
    public List getStandaloneProcess(String language) {
        ListRange listRange = new ListRange();
        List<String> process = new ArrayList<String>();

        try {
            WSTransformerPK[] wst = Util.getPort().getTransformerPKs(new WSGetTransformerPKs("*")).getWsTransformerPK(); //$NON-NLS-1$

            for (WSTransformerPK wstransformerpk : wst) {
                if (isStandaloneProcess(wstransformerpk.getPk())) {
                	WSTransformer wsTransformer = Util.getPort().getTransformer(new WSGetTransformer(wstransformerpk));
                	process.add(getDescriptionByLau(language, wsTransformer.getDescription()));
                }
            }

            listRange.setData(process.toArray());
            listRange.setTotalSize(process.size());
        } catch (RemoteException e) {
            LOG.error(e.getMessage(), e);
        } catch (XtentisWebappException e) {
            LOG.error(e.getMessage(), e);
        }

        return process;
    }

    /**
     * check if is it standalong process.
     */
    public boolean isStandaloneProcess(String wstransformerpk) {
        return wstransformerpk.startsWith(STANDALONE_PROCESS_PREFIX);
    }

    /**
     * run the standalong process.
     * 
     * @param transformerPK
     * @return
     */
    public String runProcess(String transformerPK) {
        String sucess = "ok";//$NON-NLS-1$
        WSTransformerContext wsTransformerContext = new WSTransformerContext(new WSTransformerV2PK(transformerPK), null, null);

        try {
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
                        sucess += downloadUrl;
                    }
                }
            }
        } catch (RemoteException e) {
            LOG.error(e.getMessage(), e);
            sucess = "failed";//$NON-NLS-1$
        } catch (XtentisWebappException e) {
            LOG.error(e.getMessage(), e);
            sucess = "failed";//$NON-NLS-1$
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            sucess = "failed";//$NON-NLS-1$
        }

        return sucess;
    }
}
