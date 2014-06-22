package com.amalto.webapp.core.util;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import org.talend.mdm.commmon.util.core.CommonUtil;
import org.talend.mdm.commmon.util.core.MDMConfiguration;

import com.amalto.webapp.util.webservices.WSBoolean;
import com.amalto.webapp.util.webservices.WSExistsMenu;
import com.amalto.webapp.util.webservices.WSGetMenu;
import com.amalto.webapp.util.webservices.WSGetRole;
import com.amalto.webapp.util.webservices.WSMenu;
import com.amalto.webapp.util.webservices.WSMenuEntry;
import com.amalto.webapp.util.webservices.WSMenuMenuEntriesDescriptions;
import com.amalto.webapp.util.webservices.WSMenuPK;
import com.amalto.webapp.util.webservices.WSRole;
import com.amalto.webapp.util.webservices.WSRolePK;
import com.amalto.webapp.util.webservices.WSRoleSpecification;
import com.amalto.webapp.util.webservices.WSRoleSpecificationInstance;

public class Menu {
	private String id;
	private HashMap<String, String> labels = new HashMap<String, String>();
	private Menu parent=null; //this value is not nulmm when the menu is "linked"
	private String parentID;
	private int position;
	private String context;
	private String application;
	private String icon;
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}



	private TreeMap<String, Menu> subMenus = new TreeMap<String, Menu>();
	
	
	
	public Menu getParent() {
		return parent;
	}
	public void setParent(Menu parent) {
		this.parent = parent;
	}
	public String getApplication() {
		return application;
	}
	public void setApplication(String application) {
		this.application = application;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public HashMap<String, String> getLabels() {
		return labels;
	}
	public void setLabels(HashMap<String, String> labels) {
		this.labels = labels;
	}
	public String getParentID() {
		return parentID;
	}
	public void setParentID(String parentID) {
		this.parentID = parentID;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public TreeMap<String, Menu> getSubMenus() {
		return subMenus;
	}
	public void setSubMenus(TreeMap<String, Menu> subMenus) {
		this.subMenus = subMenus;
	}
	
	

	private static DecimalFormat twoDigits = new DecimalFormat("00");
	
	public static Menu getRootMenu() throws XtentisWebappException{
		try {
			//first fetch the menu index
			HashMap<String, Menu> menuIndex = getMenuIndex();
			
			// create a RootMenu Holder
			Menu root = new Menu();
			
			//go over all the Menu Entries that have a null parent an try to put them at the appropriate location
			Set<String> ids = menuIndex.keySet();
			for (Iterator iter = ids.iterator(); iter.hasNext(); ) {
				String id = (String) iter.next();
				Menu menu = menuIndex.get(id);
				if (menu.getParent()!=null) continue; //we are good keep going
				if ((menu.getParentID()==null) || "".equals(menu.getParentID())) {
					//attach to root
					root.getSubMenus().put(
							twoDigits.format(menu.getPosition())+" - "+menu.getId(),
							menu
					);
					//update parent with root
					menu.setParent(root);
					continue;//done
				}
				//try to find the entry
				Menu parentMenu = menuIndex.get(menu.getParentID());
				if (parentMenu ==null) {
					//discard
					org.apache.log4j.Logger.getLogger(Menu.class).debug("getRootMenu() No parent found for "+menu.getId());
					continue;
				}
				//found - add it to parent
				parentMenu.getSubMenus().put(
						twoDigits.format(menu.getPosition())+" - "+menu.getId(),
						menu
				);
				//update menu with parent ref
				menu.setParent(parentMenu);
			}
			
			return root;
			
		} catch (XtentisWebappException e) {
			throw(e);
		}catch (Exception e) {
//			String err;
//			try {
//				err="Unable to get The root Menu for user "+Util.getAjaxSubject().getUsername()+
//					": "+(e.getClass().getName().contains("RemoteException")? "" : e.getClass().getName()+": ")+e.getLocalizedMessage();
//			}catch (Exception ex) {
//				err="Unable to get user when fetching the root menu "+
//					": "+ex.getClass().getName()+": "+ex.getLocalizedMessage();
//			}
			throw new XtentisWebappException(CommonUtil.getErrMsgFromException(e));
		}
	}
	
    private static HashMap<String, Menu> getMenuIndex() throws XtentisWebappException {

		//The index of Menu Entries
		HashMap<String, Menu> menuIndex = new HashMap<String, Menu>();

		try {
			AjaxSubject as = Util.getAjaxSubject();
			org.apache.log4j.Logger.getLogger(Menu.class).debug("getMenus() "+as.getUsername());
			
            if (MDMConfiguration.getAdminUser().equals(as.getUsername())) {
				//TODO: should we do anything here?
				return menuIndex;
			}
			
			//not admin
			for (Iterator iter = as.getRoles().iterator(); iter.hasNext(); ) {
				String role = (String) iter.next();
				org.apache.log4j.Logger.getLogger(Menu.class).debug("getMenuIndex() ROLE "+role);
				
				if  (! 
						(
						"authenticated".equals(role) || 
						"administration".equals(role) ||
						"disabled".equals(role)
						)
					) {
					
					WSRole wsRole = Util.getPort().getRole(new WSGetRole(new WSRolePK(role)));
					org.apache.log4j.Logger.getLogger(Menu.class).debug("getMenuIndex() WSROLE "+wsRole.getName());
					WSRoleSpecification[] specifications = wsRole.getSpecification();
					if (specifications != null) {
						for (int i = 0; i < specifications.length; i++) {
							org.apache.log4j.Logger.getLogger(Menu.class).debug("getMenuIndex() OBJECT TYPE "+specifications[i].getObjectType());
							if (specifications[i].getObjectType().equals("Menu")) {
								WSRoleSpecificationInstance[] instances = specifications[i].getInstance();
								if (instances != null) {
									 for (int j = 0; j < instances.length; j++) {
										WSRoleSpecificationInstance instance = instances[j];
										org.apache.log4j.Logger.getLogger(Menu.class).debug("getMenuIndex() INSTANCE NAME "+instance.getInstanceName());
										try {
										addMenuEntries(menuIndex, instance);
										}catch(Exception e) {
											e.printStackTrace();
										}
									}//for instances
								}//instances=null
								break;
							}//if menus
						}//for specififcations
					}
					
				}//role.startsWith("b2box")					

			}//for AjaxSubject Roles			
			
			return menuIndex;
	
		} catch (XtentisWebappException e) {
			throw(e);
		} catch (Exception e) {
			String err;
//			try {
//				err="Unable to fetch roles for user "+Util.getAjaxSubject().getUsername()+
//					": "+e.getLocalizedMessage();
//			}catch (Exception ex) {
//				err="Unable to get user when fetching roles "+
//					": "+": "+ex.getLocalizedMessage();
//			}

			throw new XtentisWebappException(CommonUtil.getErrMsgFromException(e));
		}
	}
	
	
	
	private static void addMenuEntries(HashMap<String, Menu> index,WSRoleSpecificationInstance instance) throws XtentisWebappException{
		org.apache.log4j.Logger.getLogger(Menu.class).debug("addMenuEntries() "+instance.getInstanceName());
		try {		
			//check menu exist 
			WSBoolean menuExist=Util.getPort().existsMenu(new WSExistsMenu(new WSMenuPK(instance.getInstanceName())));			
			if(menuExist.is_true()){
				RoleMenuParameters params = RoleMenuParameters.unmarshalMenuParameters(instance.getParameter()[0]);
				WSMenu wsMenu = Util.getPort().getMenu(new WSGetMenu(new WSMenuPK(instance.getInstanceName())));
				WSMenuEntry[] wsEntries = wsMenu.getMenuEntries();
				if (wsEntries!=null) {
					for (int i = 0; i < wsEntries.length; i++) {
						index.put(
								wsEntries[i].getId(),
								wsMenu2Menu(index, wsEntries[i],null,params.getParentID(),params.getPosition())
						);
					}
				}
			}
		} catch (XtentisWebappException e) {
			throw(e);
		} catch (Exception e) {
//			String err;
//			try {
//				err="Unable to build the menu entry "+instance.getInstanceName()+" for user "+Util.getAjaxSubject().getUsername()+
//					": "+(e.getClass().getName().contains("RemoteException")? "" : e.getClass().getName()+": ")+e.getLocalizedMessage();
//			}catch (Exception ex) {
//				err="Unable to get user when adding the menu entries "+
//					": "+ex.getClass().getName()+": "+ex.getLocalizedMessage();
//			}
			throw new XtentisWebappException(CommonUtil.getErrMsgFromException(e));
		}
	}
	
	
	
    public static Menu wsMenu2Menu(HashMap<String, Menu> index, WSMenuEntry entry, Menu parent, String parentID, int position)
            throws XtentisWebappException {
		try {
			Menu menu = new Menu();
			menu.setApplication(entry.getApplication());
			menu.setContext(entry.getContext());
			menu.setId(entry.getId());
			menu.setIcon(entry.getIcon());
			WSMenuMenuEntriesDescriptions[] descriptions = entry.getDescriptions();
			HashMap<String, String> labels = new HashMap<String, String>();
			if (descriptions!=null) {
				for (int i = 0; i < descriptions.length; i++) {
                    labels.put(descriptions[i].getLanguage().toLowerCase(), descriptions[i].getLabel());
				}
			}
			menu.setLabels(labels);
			menu.setParent(parent);
			menu.setParentID(parentID);
			menu.setPosition(position);
			//recursively add the the submenus. These ones have a parent
			WSMenuEntry[] wsSubMenus = entry.getSubMenus();
			TreeMap<String, Menu> subMenus = new TreeMap<String, Menu>();
			if (wsSubMenus!=null) {
				for (int i = 0; i < wsSubMenus.length; i++) {
					subMenus.put(
							twoDigits.format(i)+" - "+wsSubMenus[i].getId(), 
							wsMenu2Menu(index, wsSubMenus[i], menu, menu.getParentID(),i)
					);
				}
			}
			menu.setSubMenus(subMenus);
			index.put(menu.getId(), menu);
			return menu;
		} catch (XtentisWebappException e) {
			throw(e);
		} catch (Exception e) {
//			String err;
//			try {
//				err="Unable to convert the ws menu Entry "+entry.getId()+" for user "+Util.getAjaxSubject().getUsername()+
//					": "+(e.getClass().getName().contains("RemoteException")? "" : e.getClass().getName()+": ")+e.getLocalizedMessage();
//			}catch (Exception ex) {
//				err="Unable to get user when converting the ws menu entries "+
//					": "+ex.getClass().getName()+": "+ex.getLocalizedMessage();
//			}
			throw new XtentisWebappException(CommonUtil.getErrMsgFromException(e));
		}

	}
		
    public static String getMenuLabel(String language, String menuIndex) throws Exception {
        HashMap<String, Menu> menus = getMenuIndex();
        Menu menu = menus.get(menuIndex);
        if ((null == language) || (language.trim().equals(""))) //$NON-NLS-1$
            language = "en"; //$NON-NLS-1$
        return menu.getLabels().get(language);
    }
}
