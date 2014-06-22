package com.amalto.webapp.core.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class Util2 {

	
	public static String readTextFile(String localFilename, String encoding) throws XtentisWebappException{
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(localFilename),encoding));
			String line;
			String text="";
			while ((line=br.readLine())!=null) {
				text += "".equals(text) ? line : "\n"+line;
			}
			return text;
		} catch (Exception e) {
			throw new XtentisWebappException(
					"Impossible to read the local web app server file at "+localFilename+" with encoding "+encoding+
					". "+e.getClass().getName()+": "+e.getMessage()
			);
		}
	}
	
	
	public static Element getFirstElement(Node n, String xPath) throws XtentisWebappException {
		try {
			NodeList nl = Util.getNodeList(n, xPath);
			if ((nl==null) || (nl.getLength()==0)) return null;
			return (Element)nl.item(0);
		} catch (XtentisWebappException e) {
			throw(e);
		} catch (Exception e) {
			throw new XtentisWebappException("Unable to get the First Element of node "+n.getLocalName()+" at xPath" +xPath);
		}
		
	}
	
	/*********************************************************************
	 * 
	 *      GUID Generator
	 *      
	 *********************************************************************/

	
	/** Cached per JVM server IP. */
	private static String hexServerIP = null;

	// initialise the secure random instance
	private static final java.security.SecureRandom seeder = new java.security.SecureRandom();

	/**
	 * A 32 byte GUID generator (Globally Unique ID). These artificial keys SHOULD <strong>NOT </strong> be seen by the user,
	 * not even touched by the DBA but with very rare exceptions, just manipulated by the database and the programs.
	 *
	 * Usage: Add an id field (type java.lang.String) to your EJB, and add setId(XXXUtil.generateGUID(this)); to the ejbCreate method.
	 */
	public static final String generateGUID(Object o) {
		StringBuffer tmpBuffer = new StringBuffer(16);
		if (hexServerIP == null) {
			java.net.InetAddress localInetAddress = null;
			try {
				// get the inet address

				localInetAddress = java.net.InetAddress.getLocalHost();
			}
			catch (java.net.UnknownHostException uhe) {
				System.err.println("JobUtil: Could not get the local IP address using InetAddress.getLocalHost()!");
				// todo: find better way to get around this...
				uhe.printStackTrace();
				return null;
			}
			byte serverIP[] = localInetAddress.getAddress();
			hexServerIP = hexFormat(getInt(serverIP), 8);
		}

		String hashcode = hexFormat(System.identityHashCode(o), 8);
		tmpBuffer.append(hexServerIP);
		tmpBuffer.append(hashcode);

		long timeNow= System.currentTimeMillis();
		int timeLow = (int)timeNow & 0xFFFFFFFF;
		int node = seeder.nextInt();

		StringBuffer guid = new StringBuffer(32);
		guid.append(hexFormat(timeLow, 8));
		guid.append(tmpBuffer.toString());
		guid.append(hexFormat(node, 8));
		return guid.toString();
	}

	private static int getInt(byte bytes[]) {
		int i = 0;
		int j = 24;
		for (int k = 0; j >= 0; k++) {
			int l = bytes[k] & 0xff;
			i += l << j;
			j -= 8;
		}
		return i;
	}

	private static String hexFormat(int i, int j) {
		String s = Integer.toHexString(i);
		return padHex(s, j) + s;
	}

	private static String padHex(String s, int i) {
		StringBuffer tmpBuffer = new StringBuffer();
		if (s.length() < i) {
			for (int j = 0; j < i - s.length(); j++) {
				tmpBuffer.append('0');
			}
		}
		return tmpBuffer.toString();
	}
	
	
}
