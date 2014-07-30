package com.amalto.webapp.v3.viewbrowser.bean;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import com.amalto.webapp.core.util.Util;
import com.amalto.core.webservice.WSDataModelPK;
import com.amalto.core.webservice.WSGetBusinessConcepts;
import com.amalto.core.webservice.WSGetView;
import com.amalto.core.webservice.WSView;
import com.amalto.core.webservice.WSViewPK;
import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.dwr.CommonDWR;

public class View {

	public View() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	private String viewPK;
	private String description;
	private String descriptionLocalized;
	private String[] viewablesXpath;
	private Map<String,String> viewables;
	private Map<String,String> searchables;
	
	private String[] keys;

	
	public View(String viewPK, String language) throws RemoteException, Exception{
		this.viewPK = viewPK;
		WSView view = Util.getPort().getView(new WSGetView(new WSViewPK(viewPK)));
		this.description = view.getDescription();
		this.descriptionLocalized = view.getDescription().replaceAll(".*\\["+language.toUpperCase()+":(.*?)\\].*","$1");
		this.viewables = getViewables(viewPK, language);
		this.searchables = getSearchables(viewPK, language);
		this.viewablesXpath = view.getViewableBusinessElements();
	}

	
	public View(String viewPK) throws RemoteException, Exception{
		this.viewPK = viewPK;
		WSView view = Util.getPort().getView(new WSGetView(new WSViewPK(viewPK)));
		this.description = view.getDescription();
		this.viewablesXpath = view.getViewableBusinessElements();
	}
	

	
	
	
	private Map<String,String> getSearchables(String viewPK,String language){
		try{
			Configuration config = Configuration.getInstance(true);
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
				labelSearchables.put(searchables[i],xpathToLabel.get(searchables[i]));
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
	
	private Map<String,String> getViewables(String viewPK,String language){
		try{
			Configuration config = Configuration.getInstance(true);
			String[] viewables =Util.getPort().getView(new WSGetView(new WSViewPK(viewPK)))
				.getViewableBusinessElements();
			HashMap<String,String> label = new HashMap<String,String>();
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
			
			for (int i = 0; i < viewables.length; i++) {
				label.put(viewables[i],xpathToLabel.get(viewables[i]));
			}
			try{
				return CommonDWR.getMapSortedByValue(label);
			}
			catch(Exception e){
				return label;
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


	public String[] getViewablesXpath() {
		return viewablesXpath;
	}

	public void setViewablesXpath(String[] viewablesXpath) {
		this.viewablesXpath = viewablesXpath;
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


	public Map<String, String> getViewables() {
		return viewables;
	}


	public void setViewables(Map<String, String> viewables) {
		this.viewables = viewables;
	}
}
