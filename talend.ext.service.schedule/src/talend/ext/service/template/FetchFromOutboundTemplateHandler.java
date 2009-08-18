package talend.ext.service.template;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.amalto.core.util.Util;

public class FetchFromOutboundTemplateHandler implements ITemplateHandler{
	
	private static final Pattern pLoad = Pattern.compile("(<parameters>(.*)</parameters>)",Pattern.DOTALL);

	public String getContent(String serviceName) {
		StringBuffer sb=new StringBuffer();
		if(serviceName.equals("calltransformer")){
			sb.append("<input> \n"); 
			sb.append("   <command>?</command><!-- required --> \n"); 
			sb.append("   <parameters> \n"); 
			sb.append("        <transformer>?</transformer><!-- required --> \n"); 
			sb.append("		<typedContent> \n"); 
			sb.append("			 <type>?</type><!-- required --> \n"); 
			sb.append("			 <value>?</value><!-- required --> \n"); 
			sb.append("		</typedContent> \n"); 
			sb.append("   </parameters> \n"); 
			sb.append("   <schedulePlanID>?</schedulePlanID> \n"); 
			sb.append("</input> \n");
		}else if(serviceName.equals("jdbc")){
			sb.append("<input> \n"); 
			sb.append("   <command>?</command><!-- required --> \n"); 
			sb.append("   <parameters> \n"); 
			sb.append("		<driverClassName>?</driverClassName><!-- required --> \n"); 
			sb.append("		<url>?</url><!-- required --> \n"); 
			sb.append("		<username>?</username><!-- required --> \n"); 
			sb.append("		<password>?</password><!-- required --> \n"); 
			sb.append("		<selectSql>?</selectSql><!-- optional --> \n"); 
			sb.append("		<tableName>?</tableName><!-- optional --> \n"); 
			sb.append("   </parameters> \n"); 
			sb.append("   <schedulePlanID>?</schedulePlanID> \n"); 
			sb.append("</input> \n");
		}else{
//			sb.append("<input> \n"); 
//			sb.append("   <!-- No Template --> \n"); 
//			sb.append("</input> \n");
		}
		return sb.toString();
	}

	public Object[] parse(String inputInstance) {
		
		Object[] rtn=new Object[3];
		try {
			
			inputInstance=inputInstance.replace(">?</", "></");//replace ?
			
			Document doc=Util.parse(inputInstance);
			String arg0=Util.getFirstTextNode(doc, "//command");
			String arg1="";
			Matcher m1 = pLoad.matcher(inputInstance);
		    while(m1.find()){
		    	arg1=m1.group(1);
		    }
			String arg2=Util.getFirstTextNode(doc, "//schedulePlanID");
			
			rtn[0]=arg0;
			rtn[1]=arg1;
			rtn[2]=arg2;
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return rtn;
	}
	

}
