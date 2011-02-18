package org.talend.mdm.webapp.itemsbrowser2.server;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.talend.mdm.webapp.itemsbrowser2.client.ItemsService;
import org.talend.mdm.webapp.itemsbrowser2.client.Itemsbrowser2;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemFormBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemFormLineBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.QueryModel;
import org.talend.mdm.webapp.itemsbrowser2.client.util.XmlHelper;
import org.talend.mdm.webapp.itemsbrowser2.server.dwr.ItemsBrowser2DWR;
import org.talend.mdm.webapp.itemsbrowser2.server.mockup.FakeCustomerConcept;
import org.talend.mdm.webapp.itemsbrowser2.server.mockup.FakeData;
import org.talend.mdm.webapp.itemsbrowser2.server.util.XmlUtil;
import org.talend.mdm.webapp.itemsbrowser2.server.util.callback.ElementProcess;
import org.talend.mdm.webapp.itemsbrowser2.shared.FieldVerifier;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.dwr.CommonDWR;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSGetView;
import com.amalto.webapp.util.webservices.WSGetViewPKs;
import com.amalto.webapp.util.webservices.WSView;
import com.amalto.webapp.util.webservices.WSViewPK;
import com.amalto.webapp.util.webservices.WSViewSearch;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ItemsServiceImpl extends RemoteServiceServlet implements ItemsService {

    private static final Logger LOG = Logger.getLogger(ItemsBrowser2DWR.class);
    
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

    public ArrayList<ItemBean> getFakeCustomerItems() {
        return null;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.mdm.webapp.itemsbrowser2.client.ItemsService#getEntityItems(java.lang.String)
     */
    public List<ItemBean> getEntityItems(String entityName) {
        List<ItemBean> items = null;

        if (entityName.equals("customer"))
        	if (Itemsbrowser2.IS_SCRIPT){
        		items = (List<ItemBean>) getItemBeans("DStar", "Browse_items_Agency", "Agency FULLTEXTSEARCH *", 0, 20)[0];
        	} else {
        		items = FakeData.getFakeCustomerItems();
        	}

        else if (entityName.equals("state"))
            items = FakeData.getFakeStateItems();

        return items;
    }

    private Object[] getItemBeans(String dataClusterPK, String viewPk, String criteria, int skip, int max) {

    	String sortDir = null;
    	String sortCol = null;
    	
    	int totalSize = 0;

    	List<ItemBean> itemBeans = new ArrayList<ItemBean>();
    	
    	try {
	    	WSWhereItem wi = com.amalto.webapp.core.util.Util.buildWhereItems(criteria);
	
	    	String[] results = com.amalto.webapp.core.util.Util.getPort().viewSearch(
						new WSViewSearch(
							new WSDataClusterPK(dataClusterPK),
							new WSViewPK(viewPk),
							wi,
							-1,
							skip,
							max,
							sortCol,
							sortDir
					)
				).getStrings();
	    	ViewBean viewBean = getView(FakeData.DEFAULT_VIEW);
	    	for (int i = 0; i < results.length; i++) {
	
			   if(i == 0) {
				   totalSize = Integer.parseInt(com.amalto.webapp.core.util.Util.parse(results[i]).
					         getDocumentElement().getTextContent());
			      continue;
			   }
	
				//aiming modify when there is null value in fields, the viewable fields sequence is the same as the childlist of result
				if(!results[i].startsWith("<result>")){
					results[i]="<result>" + results[i] + "</result>";
				}
				ItemBean itemBean = new ItemBean("customer", String.valueOf(i), results[i]);
				dynamicAssemble(itemBean, viewBean);
				itemBeans.add(itemBean);
	    	}
    	} catch (Exception e){
    		e.printStackTrace();
    		LOG.error(e.getMessage(), e);
    	}
    	return new Object[]{itemBeans, totalSize};

    }

    public void dynamicAssemble(ItemBean itemBean, ViewBean viewBean) throws DocumentException {
        if (itemBean.getItemXml() != null) {
            Document docXml = XmlUtil.parseText(itemBean.getItemXml());
            List<String> viewables = viewBean.getViewableXpaths();
            for (String viewable : viewables) {
                String value = XmlUtil.getTextValueFromXpath(docXml, viewable);
                itemBean.set(viewable, value);
            }
        }
    }
    
    /**
     * DOC HSHU Comment method "getView".
     */
    public ViewBean getView(String viewPk) {
        ViewBean vb = new ViewBean();
        vb.setViewPK(viewPk);
        // TODO mockup

        String[] viewables = null;
        if (Itemsbrowser2.IS_SCRIPT) {
            // test arguments Browse_items_Agency
            viewables = getViewables("Browse_items_Agency", "en");
        } else {
            viewables = FakeData.getEntityViewables(viewPk);
        }
        for (String viewable : viewables) {
            vb.addViewableXpath(viewable);
        }
        vb.setViewables(viewables);

        vb.setSearchables(getSearchables(viewPk, "en"));

        return vb;
    }

    private Map<String, String> getSearchables(String viewPk, String language) {
        try {
            String[] searchables = com.amalto.webapp.core.util.Util.getPort().getView(new WSGetView(new WSViewPK(viewPk)))
                    .getSearchableBusinessElements();
            Map<String, String> labelSearchables = new LinkedHashMap<String, String>();

            int index = -1;
            for (int i = 0; i < searchables.length; i++) {
                index = searchables[i].indexOf("/");
                labelSearchables.put(searchables[i], index == -1 ? searchables[i] : searchables[i].substring(index + 1));
            }

            return labelSearchables;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public String[] getViewables(String viewPK, String language) {
        try {
            WSView wsView = null;
            try {
                wsView = com.amalto.webapp.core.util.Util.getPort().getView(new WSGetView(new WSViewPK(viewPK)));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            String[] viewables = wsView.getViewableBusinessElements();
            return viewables;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage(), e);
            return null;
        }

    }

    /**
     * DOC HSHU Comment method "setForm".
     */
    public ItemFormBean setForm(ItemBean item) {

        final ItemFormBean itemFormBean = new ItemFormBean();
        if (item == null){
        	return itemFormBean;
        }
        itemFormBean.setName(item.getConcept() + " " + item.getIds());

        // get item
        // TODO: add data cluster to the criteria
        String itemXml = item.getItemXml();

        // get datamodel
        final FakeCustomerConcept fakeCustomerConcept = new FakeCustomerConcept();

        // go through item
        try {

            Document itemDoc = XmlUtil.parseText(itemXml);
            XmlUtil.iterate(itemDoc, new ElementProcess() {

                @Override
                public void process(Element element) {

                    ItemFormLineBean itemFormLineBean = new ItemFormLineBean();

                    String path = element.getUniquePath();

                    // TODO check with complete schema
                    String label = element.getName();
                    String value = element.getText();

                    itemFormLineBean.setFieldType(ItemFormLineBean.FIELD_TYPE_TEXTFIELD);
                    itemFormLineBean.setFieldLabel(label);
                    itemFormLineBean.setFieldValue(value);

                    // check foreign key
                    List<String> paths = fakeCustomerConcept.getForeignKeyPaths();
                    if (paths.contains(path))
                        itemFormLineBean.setHasForeignKey(true);

                    itemFormBean.addLine(itemFormLineBean);

                }

            });

        } catch (DocumentException e) {
            LOG.error(e);
        }

        return itemFormBean;
    }

	@Override
	public PagingLoadResult<ItemBean> queryItemBean(QueryModel config) {
		PagingLoadConfig pagingLoad = config.getPagingLoadConfig();
		Object[] result = getItemBeans(config.getDataClusterPK(), config.getViewPK(), config.getCriteria(), pagingLoad.getOffset(), pagingLoad.getLimit());
		List<ItemBean> itemBeans = (List<ItemBean>) result[0];
		int totalSize = (Integer) result[1];
		return new BasePagingLoadResult<ItemBean>(itemBeans, pagingLoad.getOffset(), totalSize);
	}

    public Map<String, String> getViewsList(String language) {
        try {
            WSViewPK[] wsViewsPK;
            wsViewsPK = com.amalto.webapp.core.util.Util.getPort().getViewPKs(new WSGetViewPKs("Browse_items.*")).getWsViewPK();

            String[] names = new String[wsViewsPK.length];
            TreeMap<String, String> views = new TreeMap<String, String>();
            Pattern p = Pattern.compile(".*\\[" + language.toUpperCase() + ":(.*?)\\].*", Pattern.DOTALL);
            for (int i = 0; i < wsViewsPK.length; i++) {
                WSView wsview = com.amalto.webapp.core.util.Util.getPort().getView(new WSGetView(wsViewsPK[i]));
                names[i] = wsViewsPK[i].getPk();
                String viewDesc = p.matcher(!wsview.getDescription().equals("") ? wsview.getDescription() : wsview.getName())
                        .replaceAll("$1");
                views.put(wsview.getName(), viewDesc.equals("") ? wsview.getName() : viewDesc);
            }
            return CommonDWR.getMapSortedByValue(views);
        } catch (XtentisWebappException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
