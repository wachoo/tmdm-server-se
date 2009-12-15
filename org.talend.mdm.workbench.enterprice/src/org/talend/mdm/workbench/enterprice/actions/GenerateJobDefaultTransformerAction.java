package org.talend.mdm.workbench.enterprice.actions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.JobInfo;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.views.ServerView;
import com.amalto.workbench.webservices.WSPutTransformerV2;
import com.amalto.workbench.webservices.WSTransformerProcessStep;
import com.amalto.workbench.webservices.WSTransformerV2;
import com.amalto.workbench.webservices.WSTransformerVariablesMapping;
import com.amalto.workbench.webservices.WSViewPK;

public class GenerateJobDefaultTransformerAction extends Action{

	private ServerView server = ServerView.show();
	private TreeObject xobject;

				
	public GenerateJobDefaultTransformerAction() {
		super();			
		setImageDescriptor(ImageCache.getImage(EImage.JOB.getPath()));
		setText("Generate TIS Job default transformer");
		setToolTipText("Generate TIS Job default transformer");
	}
	
	public void run() {
		if (this.server != null) { //called from ServerView
			ISelection selection = server.getViewer().getSelection();
			xobject = (TreeObject)((IStructuredSelection)selection).getFirstElement();
		}
        
        if (xobject.getType()!=TreeObject.JOB) return;
       try{ 
	
   		WSTransformerV2 transformer=new WSTransformerV2();
		try {
		WSTransformerProcessStep[] steps=new WSTransformerProcessStep[3];
		WSTransformerVariablesMapping[] input=new WSTransformerVariablesMapping[1];
		input[0]=new WSTransformerVariablesMapping("_DEFAULT_","xml",null);
		WSTransformerVariablesMapping[] output=new WSTransformerVariablesMapping[1];
		output[0]=new WSTransformerVariablesMapping("item_xml","text",null);
		
		steps[0]=new WSTransformerProcessStep("amalto/local/transformer/plugin/xslt","Generate the XSLT step to retrieve the item from an update report","<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"  xmlns:mdm=\"java:com.amalto.core.plugin.base.xslt.MdmExtension\" version=\"1.0\"> <xsl:output method=\"xml\" indent=\"yes\" omit-xml-declaration=\"yes\" /> <xsl:template match=\"/\" priority=\"1\">\n"+
"<item><xsl:copy-of select='mdm:getItemProjection(Update/RevisionID,Update/DataCluster,Update/Concept,Update/Key)'/></item>"+
"</xsl:template> </xsl:stylesheet>\n",input,output,false);
		
		//step 2
		input=new WSTransformerVariablesMapping[1];
		input[0]=new WSTransformerVariablesMapping("item_xml","law_text",null);
		output=new WSTransformerVariablesMapping[1];
		output[0]=new WSTransformerVariablesMapping("decode_xml","codec_text",null);
		String parameter="<parameters>\n"+
				"<method>DECODE</method>\n"+
				"<algorithm>XMLESCAPE</algorithm>\n"+
				"</parameters>\n";
		steps[1]=new WSTransformerProcessStep("amalto/local/transformer/plugin/codec","Generate the codec step to escape the item xml",parameter,input,output,false);
		
		//step 3
		input=new WSTransformerVariablesMapping[1];
		input[0]=new WSTransformerVariablesMapping("decode_xml","text",null);
		output=new WSTransformerVariablesMapping[1];
		output[0]=new WSTransformerVariablesMapping("output","result",null);
		String server="http://"+xobject.getEndpointHost()+":"+xobject.getEndpointPort();
		
		JobInfo info = new JobInfo("", "");
		//add ymli
		//if the xobject is generated just now. the xobject.getWsKey() is instance of JobInfo;
		// if it already exists when login workbench, the  xobject.getWsKey() is instance of WSViewPK.
		if (xobject.getWsKey() instanceof WSViewPK) {
					String jobName = ((WSViewPK) xobject.getWsKey()).getPk();
					if (jobName.matches("(.*?)_(\\d*.\\d*)")) {
						Pattern p = Pattern.compile("(.*?)_(\\d*.\\d*)");
						Matcher m = p.matcher(jobName);
						if (m.matches()) {
							info.setJobversion(m.group(m.groupCount()));
							info.setJobname(m.group(m.groupCount() - 1));
						}
					}
				}
		else
			info=(JobInfo)xobject.getWsKey();
		String jobname=info.getJobname();
		String jobversion=info.getJobversion();
		parameter="<configuration>\n"+
		"<url>"+server+"/"+jobname+"_"+jobversion+"/services/"+jobname+"</url>\n"+
		"<contextParam>\n"+
			"<name>xmlInput</name>\n"+
			"<value>{decode_xml}</value>\n"+
		"</contextParam>\n"+
		"</configuration>\n";
		steps[2]=new WSTransformerProcessStep("amalto/local/transformer/plugin/callJob","Generate the job call",parameter,input,output,false);
		
		transformer.setName("default_TISJob_"+jobname+"_transformer");
		transformer.setDescription("default tis job"+jobname+" transformer");
		transformer.setProcessSteps(steps);
		
		Util.getPort(xobject).putTransformerV2(new WSPutTransformerV2(transformer));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	   
       }catch(Exception e){
    	//add ymli. To refresh the view; fix the bug:0010322  
       } finally{
      		server.forceAllSiteToRefresh();
      	}
      
	}

}
