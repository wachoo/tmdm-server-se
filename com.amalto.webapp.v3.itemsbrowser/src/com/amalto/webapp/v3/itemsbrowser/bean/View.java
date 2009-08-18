package com.amalto.webapp.v3.itemsbrowser.bean;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.dwr.CommonDWR;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSGetBusinessConcepts;
import com.amalto.webapp.util.webservices.WSGetView;
import com.amalto.webapp.util.webservices.WSView;
import com.amalto.webapp.util.webservices.WSViewPK;

public class View {

	public View() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public View(String viewPK, String language) throws RemoteException, Exception{
		this.viewPK = viewPK;
		WSView view = Util.getPort().getView(new WSGetView(new WSViewPK(viewPK)));
		this.description = view.getDescription();
		this.descriptionLocalized = view.getDescription().replaceAll(".*\\["+language.toUpperCase()+":(.*?)\\].*","$1");
		this.viewables = view.getViewableBusinessElements();
		this.searchables = getSearchables(viewPK, language);
	}

	
	public View(String viewPK) throws RemoteException, Exception{
		this.viewPK = viewPK;
		WSView view = Util.getPort().getView(new WSGetView(new WSViewPK(viewPK)));
		this.description = view.getDescription();
		this.viewables = view.getViewableBusinessElements();
	}
	
	private String viewPK;
	private String description;
	private String descriptionLocalized;
	private String[] viewables;
	private Map<String,String> searchables;
	
	private String[] keys;

	
	
	
	private Map<String,String> getSearchables(String viewPK,String language){
		try{
			Configuration config = Configuration.getInstance();
			String[] searchables =Util.getPort().getView(new WSGetView(new WSViewPK(viewPK)))
				.getSearchableBusinessElements();
			HashMap<String,String> labelSearchables = new HashMap<String,String>();
			HashMap<String,String> xpathToLabel = new HashMap<String,String>();
			if(viewPK.contains("Browse_items_")){
				String concept = CommonDWR.getConceptFromBrowseItemView(viewPK);
				xpathToLabel = CommonDWR.getFieldsByDataModel(config.getModel(),concept,language, true);
			}
			else {
				String[] concepts = Util.getPort().getBusinessConcepts(new WSGetBusinessConcepts(new WSDataModelPK(config.getModel()))).getStrings();
				for (int i = 0; i < concepts.length; i++) {
					xpathToLabel.putAll(CommonDWR.getFieldsByDataModel(config.getModel(), concepts[i], language,true));
				}	
			}	
			
			for (int i = 0; i < searchables.length; i++) {
				String label=xpathToLabel.get(searchables[i]);
				if(label!=null)labelSearchables.put(searchables[i],label);
			}
			try{
				return CommonDWR.getMapSortedByValue(labelSearchables);
			}
			catch(Exception e){
				return labelSearchables;
			}

		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	
	public String[] getKeys() {
		return keys;
	}

	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public Map<String, String> getSearchables() {
		return searchables;
	}

	public void setSearchables(Map<String, String> searchables) {
		this.searchables = searchables;
	}

	public String[] getViewables() {
		return viewables;
	}
	public void setViewables(String[] viewables) {
		this.viewables = viewables;
	}
	public String getViewPK() {
		return viewPK;
	}
	public void setViewPK(String viewPK) {
		this.viewPK = viewPK;
	}

	public String getDescriptionLocalized() {
		return descriptionLocalized;
	}

	public void setDescriptionLocalized(String descriptionLocalized) {
		this.descriptionLocalized = descriptionLocalized;
	}
}
