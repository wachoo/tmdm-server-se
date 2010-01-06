package com.amalto.webapp.v3.hierarchical.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;

import com.amalto.webapp.core.json.JSONArray;
import com.amalto.webapp.core.json.JSONObject;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSGetItem;
import com.amalto.webapp.util.webservices.WSItem;
import com.amalto.webapp.util.webservices.WSItemPK;
import com.amalto.webapp.v3.hierarchical.bean.HierarchicalTreeCriterion;
import com.amalto.webapp.v3.hierarchical.util.HierarchicalUtil;
import com.talend.dom4j.util.XmlUtil;

/**
 * @author starkey
 * 
 * not in use
 *
 */
public class ItemTreeServlet extends HttpServlet{
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		doPost(req, resp);
	}
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		
		

		String jsonTree="";
		
		String keys=req.getParameter("keys");
		Object hierarchicalTreeCriterionObj=req.getSession().getAttribute(HierarchicalUtil.HIERARCHICAL_TREE_CRITERION);
		if(keys!=null&&!keys.equals("undefined")&&hierarchicalTreeCriterionObj!=null){
			try {
				HierarchicalTreeCriterion hierarchicalTreeCriterion=(HierarchicalTreeCriterion)hierarchicalTreeCriterionObj;
				
				String clusterName=hierarchicalTreeCriterion.getClusterName();
				String conceptName=hierarchicalTreeCriterion.getDataObjectName();
				String[] keysArray=keys.split("\\.");
				
				WSItem wsItem=Util.getPort().getItem(new WSGetItem(new WSItemPK(new WSDataClusterPK(clusterName),conceptName,keysArray)));
				
				Document doc = XmlUtil.parse(new ByteArrayInputStream(wsItem.getContent().getBytes()));
				JSONObject json = new JSONObject();
		    	JSONArray jsonChildren=new JSONArray();
		    	XmlUtil.visitElementList(doc.getRootElement(),jsonChildren);
				json.put("text", conceptName);
			    json.put("leaf", false);
			    json.put("children", jsonChildren);
				
			    //jsonTree=json.toString();
			    jsonTree=jsonChildren.toString();
			} catch (XtentisWebappException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			
		PrintWriter out = resp.getWriter();
		out.println(jsonTree);
		out.close();
		
	}


}
