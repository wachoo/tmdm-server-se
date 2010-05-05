package com.amalto.core.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.talend.mdm.commmon.util.time.TimeMeasure;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.util.Util;
import com.amalto.core.util.XSDKey;


/**
 * @author starkey
 * 
 *
 */

public class LoadServlet extends HttpServlet {


	private static final String PARAMETER_CLUSTER = "cluster";
	private static final String PARAMETER_CONCEPT = "concept";
	private static final String PARAMETER_DATAMODEL = "datamodel";
	private static final String PARAMETER_VALIDATE = "validate";
	private static final String PARAMETER_SMARTPK = "smartpk";
	private static final String PARAMETER_ITEMDATA = "itemdata";
	
    /**
     * Constructor
     * 
     */
    public LoadServlet() {
        super();
    }

    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
    }

    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
            doGet(request,response);
        }
    

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException,
        IOException {

        req.setCharacterEncoding("UTF-8");
        
        String action = req.getParameter("action");
        if(action==null||action.length()==0)action=getServletConfig().getInitParameter("action");

        resp.setContentType("text/html; charset=\"UTF-8\"");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter writer = resp.getWriter();
        writer.write("<html><body>");
        
        String timeMeasureTag=System.currentTimeMillis()+": Bulk load items";
        ResultLogger resultLogger=new ResultLogger();
        
    	try {
    		if ("load".equals(action)) {
    			writer.write(
   					"<p><b>Load datas into MDM</b><br/>" +
   					"Check jboss/server/default/log/server.log or the jboss console output to determine when load is completed</b></p>"
    			);
    			
    			TimeMeasure.begin(timeMeasureTag);
    			resultLogger.logTimeMeasureBegin(timeMeasureTag);
    			
    			
    			String cluster = null;
    			String concept = null;
    			String datamodel = null;
    		    List<String> itemdatas =new ArrayList<String>();
    		    
    		    boolean needAutoGenPK=false;
    		    boolean needValidate=false;
    			
    			Enumeration<String> parameterNames=req.getParameterNames();
    			for (;parameterNames.hasMoreElements();) {
					String parameterName = parameterNames.nextElement();
					
					if(parameterName.equals(PARAMETER_CLUSTER)) {
						cluster = req.getParameter(parameterName);
						continue;
					}
					
					if(parameterName.equals(PARAMETER_CONCEPT)) {
						concept = req.getParameter(parameterName);
						continue;
					}
					
					if(parameterName.equals(PARAMETER_DATAMODEL)) {
						datamodel = req.getParameter(parameterName);
						continue;
					}
					
					//optional
					if(parameterName.equals(PARAMETER_VALIDATE)) {
						String validate = req.getParameter(parameterName);
						needValidate=(validate!=null&&validate.equals("true"))?true:false;
						continue;
					}
					
					//optional
					if(parameterName.equals(PARAMETER_SMARTPK)) {
						String smartpk = req.getParameter(parameterName);
						needAutoGenPK=(smartpk!=null&&smartpk.equals("true"))?true:false;
						continue;
					}
					
					if(parameterName.startsWith(PARAMETER_ITEMDATA)) {
						itemdatas.add(req.getParameter(parameterName));
					}
				}
    			
    			
    			DataClusterPOJOPK clusterPK=new DataClusterPOJOPK(cluster);
    			
 
                DataModelPOJO dataModel = Util.getDataModelCtrlLocal().getDataModel(new DataModelPOJOPK(datamodel));
                String schemaString=dataModel.getSchema();
                Document schema = Util.parse(schemaString);
                XSDKey conceptKey = com.amalto.core.util.Util.getBusinessConceptKey(schema, concept);
 
                resultLogger.logTimeMeasureStep(timeMeasureTag, "Parse schema", TimeMeasure.step(timeMeasureTag,"Parse schema"));
                //each item
                for (int i = 0; i < itemdatas.size(); i++) {
                	
                    try {
						String xmldata=itemdatas.get(i);
						
						if(xmldata==null||xmldata.trim().equals(""))continue;
						
						Element root = Util.parse(xmldata).getDocumentElement();
						
						
						//get key values
						//support UUID or auto-increase temporarily
						String[] ids=null;
						if(!needAutoGenPK) {
							ids = com.amalto.core.util.Util.getKeyValuesFromItem(root, conceptKey);
						}else {
							
							if(Util.getUUIDNodes(schemaString, concept).size()>0){ //check uuid key exists
						    	
								Node n=Util.processUUID(root,schemaString , cluster, concept);
									       
								//get key values
								ids = com.amalto.core.util.Util.getKeyValuesFromItem(
										(Element)n,
										conceptKey
								);			
								//reset item projection
								xmldata=Util.nodeToString(n);
								
							}
						}
						
						
						ItemPOJO itemPOJO=new ItemPOJO(clusterPK,concept,ids,System.currentTimeMillis(),xmldata);
						
						// validate
						if(schemaString!=null&&needValidate)Util.validate(itemPOJO.getProjection(),schemaString);
						
						itemPOJO.setPlanPK(null);
						if(datamodel!=null&&datamodel.length()>0)itemPOJO.setDataModelName(datamodel);
						itemPOJO.setItemIds(ids);
						
						itemPOJO.store();
					} catch (Exception e) {
						resultLogger.logErrorMessage(e.getLocalizedMessage());
						e.printStackTrace();
					}
        			
        			if((i+1)%1000==0) {
        				String stepName="Loaded "+((i+1)/1000)+"k";
        				resultLogger.logTimeMeasureStep(timeMeasureTag, stepName, TimeMeasure.step(timeMeasureTag,stepName));
        				
        			}
				}
                resultLogger.logTimeMeasureEnd(timeMeasureTag, TimeMeasure.end(timeMeasureTag));
                
                writer.write(
       					"<p>"+resultLogger.print()
        			);
    			
    		} else {
    			writer.write(
   					"<p><b>Unknown action: </b>"+action+"<br/>"
    			);
    			
    		}
    	
    	} catch (Exception e) {
    		writer.write("<h1>An error occured: "+e.getLocalizedMessage()+"</h1>");
    		TimeMeasure.end(timeMeasureTag);
    		e.printStackTrace();
    	}
        writer.write("</body></html>");
                
    }
    
      
    /**
     * Represents an element on the stack.
     */
    private static class ResultLogger {

    	private StringBuffer resultLogger=null;

        public ResultLogger() {
            this.resultLogger = new StringBuffer();
        }

        public void logTimeMeasure(String msg) {
			
        	resultLogger.append(System.currentTimeMillis()+" [TimeMeasure]:"+msg+"</br>");

		}
        
        
        private void logTimeMeasureBegin(String timerId) {
        	
        	logTimeMeasure("Start '" + timerId + "' ...");

		}
        
        private void logTimeMeasureEnd(String timerId,long totalElapsedTime) {
        	
        	logTimeMeasure("End '" + timerId + "', total elapsed time: " + totalElapsedTime + " ms ");

		}
        
        public void logTimeMeasureStep(String timerId,String stepName,long stepCount) {
        	logTimeMeasure("&nbsp;&nbsp;&nbsp;&nbsp;-> '" + timerId + "', step name '" + stepName  + "', elapsed time since previous step: " + stepCount + " ms ");
        }

        public void logErrorMessage(String msg) {
        	
        	resultLogger.append(System.currentTimeMillis()+" [ErrorMessage]:"+msg+"</br>");
			
		}
        
        private void clear() {
        	resultLogger=null;
		}
        
        public String print() {
			return resultLogger.toString();
		}
    }
 
    
}