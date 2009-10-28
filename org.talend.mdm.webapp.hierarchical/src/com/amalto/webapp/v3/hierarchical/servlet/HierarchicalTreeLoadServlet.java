package com.amalto.webapp.v3.hierarchical.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;

import com.amalto.webapp.core.json.JSONArray;
import com.amalto.webapp.core.json.JSONObject;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.v3.hierarchical.bean.HierarchicalTreeCriterion;
import com.amalto.webapp.v3.hierarchical.util.HierarchicalUtil;

public class HierarchicalTreeLoadServlet extends HttpServlet{
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		doPost(req, resp);
	}
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		
		resp.setCharacterEncoding("utf-8");
		
		String jsonTree="";
		
		HierarchicalTreeCriterion hierarchicalTreeCriterion=(HierarchicalTreeCriterion) req.getSession().getAttribute(HierarchicalUtil.HIERARCHICAL_TREE_CRITERION);
		
		try {
			
			if(hierarchicalTreeCriterion!=null){
				
					JSONObject json = new JSONObject();
					ArrayList<JSONObject> rootGroup = new ArrayList<JSONObject>();
					JSONObject lastL0PivotNode=null;
					JSONObject lastL1PivotNode=null;
					String[] results=HierarchicalUtil.getHierarchicalTreeView(
							hierarchicalTreeCriterion.getClusterName(),
							hierarchicalTreeCriterion.getModelName(),
							hierarchicalTreeCriterion.getDataObjectName(), 
							hierarchicalTreeCriterion.getPivotPath(), 
							hierarchicalTreeCriterion.getTitleFieldPath(), 
							hierarchicalTreeCriterion.getFilters(),
							hierarchicalTreeCriterion.getPivotDirections(),
							hierarchicalTreeCriterion.getIndexDirections(),
							hierarchicalTreeCriterion.getLimit());
					for (int i = 0; i < results.length; i++) {
						//parse each path
						String result=results[i];
						if(result!=null&&result.length()>0){
							
							Document doc=Util.parse(result);
							
							//TODO WE only support two level at most now, so just hard code here
							
							//pivot
							List pivotTags=hierarchicalTreeCriterion.parsePivotTagOrderList();
							//title
							String titleTag=(String) hierarchicalTreeCriterion.parseTitleTag().get(0);//TODO Maybe it's a list further
							String titleValue=Util.getFirstTextNode(doc, "/result/result-title/"+titleTag);
							//keys
							String keys="";
							List keyTags=hierarchicalTreeCriterion.parseKeysTagList();
							for (int j = 0; j < keyTags.size(); j++) {
								String keyTag=(String) keyTags.get(j);
								String keyValue=Util.getFirstTextNode(doc, "/result/result-key/"+keyTag);
								keys+=("."+keyValue);
							}
							keys=keys.substring(1);
							
							if(pivotTags.size()==1){
								
								JSONObject titleNode = new JSONObject();
								titleNode.put("id", keys);
								titleNode.put("text", titleValue);
								titleNode.put("leaf", true);
								titleNode.put("type", "t0");
								titleNode.put("draggable", true);
								titleNode.put("allowDrop", false);
								titleNode.put("xpath", hierarchicalTreeCriterion.parsePivotMainPath());
								
								String l0PivotTag=(String) pivotTags.get(0);
								String l0PivotText=Util.getFirstTextNode(doc, "/result/result-pivot/"+l0PivotTag)==null?"":Util.getFirstTextNode(doc, "/result/result-pivot/"+l0PivotTag);
								if(lastL0PivotNode==null||!l0PivotText.equals((String)lastL0PivotNode.get("text"))){
									JSONObject pivotL0Node = new JSONObject();
									pivotL0Node.put("id", System.currentTimeMillis()+"");
									pivotL0Node.put("text", l0PivotText);
									pivotL0Node.put("leaf", false);
									pivotL0Node.put("type", "p0");
									pivotL0Node.put("draggable", false);
									pivotL0Node.put("allowDrop", true); 
									rootGroup.add(pivotL0Node);
									lastL0PivotNode=pivotL0Node;
									
									pivotL0Node.put("children", new JSONArray().put(titleNode));
								}else{
									JSONArray titleNodes=lastL0PivotNode.getJSONArray("children");
									titleNodes.put(titleNode);
									lastL0PivotNode.put("children", titleNodes);
								}
								
							}else if(pivotTags.size()==2){
								
								JSONObject titleNode = new JSONObject();
								titleNode.put("id", keys);
								titleNode.put("text", titleValue);
								titleNode.put("leaf", true);
								titleNode.put("type", "t0");
								titleNode.put("draggable", true);
								titleNode.put("allowDrop", false);
								titleNode.put("xpath", hierarchicalTreeCriterion.parsePivotMainPath());
								
								String l1PivotTag=(String) pivotTags.get(1);
								String l1PivotText=Util.getFirstTextNode(doc, "/result/result-pivot/"+l1PivotTag)==null?"":Util.getFirstTextNode(doc, "/result/result-pivot/"+l1PivotTag);
								if(lastL1PivotNode==null||!l1PivotText.equals((String)lastL1PivotNode.get("text"))){
									//new l1
									JSONObject pivotL1Node = new JSONObject();
									pivotL1Node.put("id", System.currentTimeMillis()+"");
									pivotL1Node.put("text", l1PivotText);
									pivotL1Node.put("leaf", false);
									pivotL1Node.put("type", "p1");
									pivotL1Node.put("draggable", false);
									pivotL1Node.put("allowDrop", false);
									rootGroup.add(pivotL1Node);
									lastL1PivotNode=pivotL1Node;
									
									String l0PivotTag=(String) pivotTags.get(0);
									String l0PivotText=Util.getFirstTextNode(doc, "/result/result-pivot/"+l0PivotTag)==null?"":Util.getFirstTextNode(doc, "/result/result-pivot/"+l0PivotTag);
									JSONObject pivotL0Node = new JSONObject();
									pivotL0Node.put("id", System.currentTimeMillis()+"");
									pivotL0Node.put("text", l0PivotText);
									pivotL0Node.put("leaf", false);
									pivotL0Node.put("type", "p0");
									pivotL0Node.put("draggable", false);
									pivotL0Node.put("allowDrop", true);
									pivotL1Node.put("children", new JSONArray().put(pivotL0Node));
									lastL0PivotNode=pivotL0Node;
									
									pivotL0Node.put("children", new JSONArray().put(titleNode));
								}else{
									//duplicate l1
									String l0PivotTag=(String) pivotTags.get(0);
									String l0PivotText=Util.getFirstTextNode(doc, "/result/result-pivot/"+l0PivotTag)==null?"":Util.getFirstTextNode(doc, "/result/result-pivot/"+l0PivotTag);
									if(lastL0PivotNode==null||!l0PivotText.equals((String)lastL0PivotNode.get("text"))){
										//new l0
										JSONObject pivotL0Node = new JSONObject();
										pivotL0Node.put("id", System.currentTimeMillis()+"");
										pivotL0Node.put("text", l0PivotText);
										pivotL0Node.put("leaf", false);
										pivotL0Node.put("type", "p0");
										pivotL0Node.put("draggable", false);
										pivotL0Node.put("allowDrop", true);
										JSONArray l1Nodes=lastL1PivotNode.getJSONArray("children");
										l1Nodes.put(pivotL0Node);
										lastL1PivotNode.put("children", l1Nodes);
										lastL0PivotNode=pivotL0Node;
										
										pivotL0Node.put("children", new JSONArray().put(titleNode));
										
									}else{
										//duplicate l0
										JSONArray titleNodes=lastL0PivotNode.getJSONArray("children");
										titleNodes.put(titleNode);
										lastL0PivotNode.put("children", titleNodes);
									}
								}
							}
							
						}
						
					}
					
					json.put("head", rootGroup);
					jsonTree=((JSONArray) json.get("head")).toString();
				
			}//end if null
			
			PrintWriter out = resp.getWriter();
			out.println(jsonTree);
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			
			PrintWriter out = resp.getWriter();
			out.println("[Exception]:"+e.getLocalizedMessage());
			out.close();
		}
		
	}

}
