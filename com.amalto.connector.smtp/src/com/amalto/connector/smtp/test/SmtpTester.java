
package com.amalto.connector.smtp.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.amalto.connector.smtp.eis.SmtpSender;


/**
 * 
 * @author jfeltesse
 */
public class SmtpTester {
	
	
	
	
	public static void main(String args[]) {
		
		if (args.length < 2) {
			System.out.println("Usage : java SmtpOutTester [input file] [host] [port] [username] [password]");
			System.out.println("NOTE : username & password are useless in the case of a non-secured connection");
			System.exit(1);
		}
		
		String inputPath = args[0];
		String host = args[1];
		int port = Integer.parseInt(args[2]);
		String username = null, password = null;
		
		boolean auth = false;
		if (args.length > 3) {
			username = args[3];
			password = args[4];
			auth = true;
		}
		
		PropertyConfigurator.configure("/opt/workspace/com.amalto.connector.smtp.outbound/log4j.properties");
		
		Logger.getLogger("SmtpTester.class").debug("Attempting to send a mail using " + host + ":" + port + (auth ? " using an authenticated connection" : ""));
		
		
		
		
		try {
			
			Logger.getLogger("SmtpTester").debug("main() : Reading xml...");
			String xml = "";
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath),"UTF-8"));
			String str;
			while ((str = in.readLine()) != null) {
				xml += str;
			}
			in.close();
			Logger.getLogger("SmtpTester").debug("main() : Read xml.");
			
			SmtpSender smtp = new SmtpSender(host, port, username, password, true, null);
			try {
				smtp.testConnection();
				smtp.processMails(xml);
			}
			catch (Exception e) {
				Logger.getLogger("SmtpTester").error("main() : Exception thrown :",e);
			}
			Logger.getLogger("SmtpTester").debug("main() : status : "+smtp.getStatusOutCode()+" - "+smtp.getStatusOutText());
			
			
			
			
			
			/*
			 
			 String from = "julien.feltesse@amalto.com";
			 String to = "julien.feltesse@amalto.com";
			 //			String to = "test.technology@amalto.eu";
			  String cc = "test.technology@gmail.com, test.technology@amalto.eu";
			  String bcc = null;
			  String subject = null; //"test mail...";
			  
			  OutboundMail om = new OutboundMail(from, to, bcc, cc, subject);
			  
			  String test = "test test test ���";
			  byte[] bs = test.getBytes("UTF-8");	
			  
			  om.addPart(new MailPart("text/plain", "UTF-8", bs));
			  
			  String f = "/home/jfeltesse/boot.jpg";
			  FileInputStream fis = new FileInputStream(new File(f));
			  FileChannel fc = fis.getChannel();
			  byte[] bf = new byte[(int)fc.size()];   // fc.size returns the size of the file which backs the channel
			  ByteBuffer bb = ByteBuffer.wrap(bf);
			  fc.read(bb);
			  fis.close();
			  
			  om.addPart(new MailPart("image/jpeg", null, bf));
			  
			  smtp.sendMail(om);
			  */
		}
		catch (Exception e) {
			try {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				Logger.getLogger("SmtpTester.class").debug(sw.toString());
			} catch (Exception etc) {/**/} 
		}
		
	}
	
}