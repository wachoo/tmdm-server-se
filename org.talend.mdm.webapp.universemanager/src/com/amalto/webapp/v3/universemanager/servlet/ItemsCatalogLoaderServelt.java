package com.amalto.webapp.v3.universemanager.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amalto.webapp.core.json.JSONArray;
import com.amalto.webapp.core.json.JSONException;
import com.amalto.webapp.core.json.JSONObject;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.v3.universemanager.bean.ToCopyItemEntry;
import com.amalto.webapp.v3.universemanager.bean.UniverseQuickStartContext;
import com.amalto.webapp.v3.universemanager.bean.UniverseQuickStartStore;
import com.amalto.webapp.v3.universemanager.util.QuickStartWizzardHelper;
import com.amalto.webapp.v3.universemanager.util.UniverseHandler;

public class ItemsCatalogLoaderServelt extends HttpServlet{
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		doPost(req, resp);
	}
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
				
		String jsonTree="";
		try {
			
			JSONObject json = new JSONObject();
			JSONArray rootArray=new JSONArray();
			
			UniverseQuickStartStore universeQuickStartStore=QuickStartWizzardHelper.getStore(req.getSession());
			List<ToCopyItemEntry> toCopyItemEntries=new ArrayList<ToCopyItemEntry>();
			
			
			String[] dcPKs=UniverseHandler.getDefaultDataClustersNames();
			for (int i = 0; i <dcPKs .length; i++) {
				String dcPk=dcPKs[i];
				JSONArray conceptsOfDataClusterArray=new JSONArray();

				//get children
				UniverseQuickStartContext universeQuickStartContext=QuickStartWizzardHelper.getContext(req.getSession());
				String sourceUniverseName=universeQuickStartContext.getSourceUniverse().getName();
				//FIXME too much times of db connection
				Map conceptPair=UniverseHandler.getDataConceptRevisions(sourceUniverseName,dcPk);
				for (Iterator iterator = conceptPair.keySet().iterator(); iterator.hasNext();) {
					String concept = (String) iterator.next();
					String revision = conceptPair.get(concept)==null?"":(String)conceptPair.get(concept);
					if(revision==null||revision.equals(""))revision="[HEAD]";
					
					String id=dcPk+"@"+concept+"@"+revision;
					String text=concept+" "+revision;
					conceptsOfDataClusterArray.put(createTreeNode(id,text,true,true));					
					
					ToCopyItemEntry toCopyItemEntry=new ToCopyItemEntry(dcPk,concept,revision);
					toCopyItemEntries.add(toCopyItemEntry);
				}
				
				JSONObject dataClusterNode=createTreeNode(dcPk, dcPk, false,conceptsOfDataClusterArray,true);
				rootArray.put(dataClusterNode);
			
			}
	
			if(universeQuickStartStore!=null)universeQuickStartStore.setToCopyItemEntries(toCopyItemEntries);
			
			json.put("root", rootArray);
			jsonTree=((JSONArray) json.get("root")).toString();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (XtentisWebappException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		PrintWriter out = resp.getWriter();
		out.println(jsonTree);
		out.close();
		
	}
	
    private JSONObject createTreeNode(String id, String text, boolean isLeaf)throws JSONException {
		
		JSONObject treeNode = new JSONObject();
		treeNode.put("id", id);
		treeNode.put("text",text );
		treeNode.put("leaf", isLeaf);
		
		return treeNode;
	}


	private JSONObject createTreeNode(String id, String text, boolean isLeaf,boolean checked)throws JSONException {
		
		JSONObject treeNode = new JSONObject();
		treeNode.put("id", id);
		treeNode.put("text",text );
		treeNode.put("leaf", isLeaf);
		treeNode.put("checked", checked);
		
		return treeNode;
	}
	
    private JSONObject createTreeNode(String id, String text, boolean isLeaf,JSONArray children,boolean checked)throws JSONException {
		
		JSONObject treeNode = new JSONObject();
		treeNode.put("id", id);
		treeNode.put("text",text );
		treeNode.put("leaf", isLeaf);
		treeNode.put("children", children);
		treeNode.put("checked", checked);
		
		return treeNode;
	}


}
