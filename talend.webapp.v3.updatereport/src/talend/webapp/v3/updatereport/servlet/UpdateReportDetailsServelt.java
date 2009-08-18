package talend.webapp.v3.updatereport.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.webapp.core.json.JSONArray;
import com.amalto.webapp.core.json.JSONObject;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XSystemObjects;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSGetItem;
import com.amalto.webapp.util.webservices.WSItem;
import com.amalto.webapp.util.webservices.WSItemPK;

public class UpdateReportDetailsServelt extends HttpServlet{
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		doPost(req, resp);
	}
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		
		String ids=req.getParameter("ids");
		
		String jsonTree="";
		JSONObject json = new JSONObject();
		
		if(ids!=null&&ids.length()>0){
			try {
				WSDataClusterPK wsDataClusterPK=new WSDataClusterPK(XSystemObjects.DC_UPDATE_PREPORT.getName());
		 		String conceptName="Update";//Hard Code
		 		String[] idss=ids.split("\\.");
				WSGetItem wsGetItem=new WSGetItem(new WSItemPK(
						wsDataClusterPK,
						conceptName,
						idss
				));
				WSItem wsItem=Util.getPort().getItem(wsGetItem);
				String content=wsItem.getContent();
				if(content!=null&&content.length()>0){
					//no recursion
					Document doc=Util.parse(content);
					String userName=Util.getFirstTextNode(doc, "/Update/UserName");
					String source=Util.getFirstTextNode(doc, "/Update/Source");
					String timeInMillis=Util.getFirstTextNode(doc, "/Update/TimeInMillis");
					String operationType=Util.getFirstTextNode(doc, "/Update/OperationType");
					String revisionID=Util.getFirstTextNode(doc, "/Update/RevisionID");
					String dataCluster=Util.getFirstTextNode(doc, "/Update/DataCluster");
					String dataModel=Util.getFirstTextNode(doc, "/Update/DataModel");
					String concept=Util.getFirstTextNode(doc, "/Update/Concept");
					String key=Util.getFirstTextNode(doc, "/Update/Key");
								
					ArrayList<JSONObject> rootGroup = new ArrayList<JSONObject>();
					JSONObject userNameNode = new JSONObject();
					userNameNode.put("id", "userName");
					userNameNode.put("text", "UserName:"+cleanOutput(userName));
					userNameNode.put("leaf", true);
					rootGroup.add(userNameNode);
					
					JSONObject sourceNode = new JSONObject();
					sourceNode.put("id", "source");
					sourceNode.put("text", "Source:"+source);
					sourceNode.put("leaf", true);
					rootGroup.add(sourceNode);
					
					JSONObject timeInMillisNode = new JSONObject();
					timeInMillisNode.put("id", "timeInMillis");
					timeInMillisNode.put("text", "TimeInMillis:"+timeInMillis);
					timeInMillisNode.put("leaf", true);
					rootGroup.add(timeInMillisNode);
					
					JSONObject operationTypeNode = new JSONObject();
					operationTypeNode.put("id", "operationType");
					operationTypeNode.put("text", "OperationType:"+operationType);
					operationTypeNode.put("leaf", true);
					rootGroup.add(operationTypeNode);
					
					JSONObject conceptNode = new JSONObject();
					conceptNode.put("id", "concept");
					conceptNode.put("text", "Concept:"+concept);
					conceptNode.put("leaf", true);
					rootGroup.add(conceptNode);
					
					JSONObject revisionIDNode = new JSONObject();
					revisionIDNode.put("id", "revisionID");
					revisionIDNode.put("text", "RevisionID:"+cleanOutput(revisionID));
					revisionIDNode.put("leaf", true);
					rootGroup.add(revisionIDNode);
					
					JSONObject dataClusterNode = new JSONObject();
					dataClusterNode.put("id", "dataCluster");
					dataClusterNode.put("text", "DataCluster:"+cleanOutput(dataCluster));
					dataClusterNode.put("leaf", true);
					rootGroup.add(dataClusterNode);
					
					JSONObject dataModelNode = new JSONObject();
					dataModelNode.put("id", "dataModel");
					dataModelNode.put("text", "DataModel:"+cleanOutput(dataModel));
					dataModelNode.put("leaf", true);
					rootGroup.add(dataModelNode);
					
					JSONObject keyNode = new JSONObject();
					keyNode.put("id", "key");
					keyNode.put("text", "Key:"+key);
					keyNode.put("leaf", true);
					rootGroup.add(keyNode);
					
					NodeList ls=Util.getNodeList(doc, "/Update/Item");
					if(ls.getLength()>0){
						
						for (int i = 0; i < ls.getLength(); i++) {
							
							String path=Util.getFirstTextNode(doc, "/Update/Item["+(i+1)+"]/path");
							String oldValue=Util.getFirstTextNode(doc, "/Update/Item["+(i+1)+"]/oldValue");
							if(oldValue==null||oldValue.equals("null"))oldValue="";
							String newValue=Util.getFirstTextNode(doc, "/Update/Item["+(i+1)+"]/newValue");
							if(newValue==null||newValue.equals("null"))newValue="";
							
							JSONArray array=new JSONArray();
							JSONObject pathNode = new JSONObject();
							pathNode.put("id", "item"+i+"-path");
							pathNode.put("text", "path:"+path);
							pathNode.put("leaf", true);
							array.put(pathNode);
							JSONObject oldValueNode = new JSONObject();
							oldValueNode.put("id", "item"+i+"-oldValue");
							oldValueNode.put("text", "oldValue:"+oldValue);
							oldValueNode.put("leaf", true);
							array.put(oldValueNode);
							JSONObject newValueNode = new JSONObject();
							newValueNode.put("id", "item"+i+"-newValue");
							newValueNode.put("text", "newValue:"+newValue);
							newValueNode.put("leaf", true);
							array.put(newValueNode);
							
							JSONObject itemNode = new JSONObject();
							itemNode.put("id", "item"+i);
							itemNode.put("text", "Item");
							itemNode.put("leaf", false);
							itemNode.put("children", array);
							rootGroup.add(itemNode);
							
						}
					}
					
					json.put("head", rootGroup);
					jsonTree=((JSONArray) json.get("head")).toString();
				}
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
	
    private String cleanOutput(String output) {
		
		if(output==null||output.equals("null"))output="";
		
		output=output.trim();
		
		return output;
	}

}
