package com.amalto.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import sun.misc.BASE64Encoder;

public class SecurityEntityResolver implements EntityResolver { 

   public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException 
   { 
	   if (systemId != null) 
	   { 
			Pattern httpUrl = Pattern.compile("(http|https|ftp):(\\//|\\\\)(.*):(.*)");
			Matcher match = httpUrl.matcher(systemId);
			if(match.matches())
			{
	          BASE64Encoder encoder = new BASE64Encoder();
	          StringBuffer buffer = new StringBuffer();
	          String credentials = encoder.encode("admin:talend".getBytes());
	          URL url = new URL(systemId);
				    URLConnection conn = url.openConnection();
				    conn.setAllowUserInteraction(true);
				    conn.setDoOutput(true);
				    conn.setDoInput(true);
		            conn.setRequestProperty("Authorization", "Basic " + credentials);
		            conn.setRequestProperty("Expect", "100-continue"); 
		   	         if (url != null) 
		   	         { 
		   	             InputStreamReader doc = 
		   	                 new InputStreamReader(conn.getInputStream());
		   	             BufferedReader reader = new BufferedReader(doc);
		   	             String line = reader.readLine();
		   	             while(line != null)
		   	             {
		   	             	buffer.append(line);
		   	             	line = reader.readLine();
		   	             }
		   	             InputSource source = new InputSource(new StringReader(buffer.toString()));
		                 return source; 
		              } 
			 }
			 else
			 {
	 			 int mark = systemId.indexOf("file:///");
	 			 String path = systemId.substring((mark != -1 ? mark + "file:///".length() : 0));
	 			 File file = new File(path);
	 			 return new InputSource(file.toURL().openStream());
			 }
	
		} 
       return null; 
  } 

}